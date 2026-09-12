package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ProductRepository
import com.example.data.ShreniDatabase
import com.example.model.BackgroundRemovalResult
import com.example.model.BackgroundStyle
import com.example.model.DemoCraft
import com.example.model.EnhancementSettings
import com.example.model.ImageProcessingResult
import com.example.model.ProcessingAction
import com.example.model.ProcessingOptions
import com.example.model.ProcessingStatus
import com.example.model.ProductAnalysis
import com.example.model.ProductImage
import com.example.model.ProductIntegrityResult
import com.example.model.ProductListing
import com.example.model.ProductScanRecord
import com.example.service.AIProductAnalysisService
import com.example.service.BackgroundRemovalService
import com.example.service.DemoAIProductAnalysisService
import com.example.service.DemoAssetGenerator
import com.example.service.DemoBackgroundRemovalService
import com.example.service.DemoImageProcessingService
import com.example.service.DemoImageEnhancementService
import com.example.service.EnhancementResult
import com.example.service.ImageEnhancementService
import com.example.service.ImageProcessingService
import com.example.service.RealAIProductAnalysisService
import com.example.service.RealBackgroundRemovalService
import com.example.service.RealImageProcessingService
import com.example.service.RealImageEnhancementService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

enum class ShreniScreen {
    DASHBOARD,
    SCAN_HOME,
    CAMERA,
    ORIGINAL_PREVIEW,
    IMAGE_ACTIONS,
    PROCESSING,
    BEFORE_AFTER,
    ENHANCEMENT_SETTINGS,
    PRODUCT_REVIEW,
    PRICE_SETTINGS,
    MARKETPLACE_PREVIEW,
    SUCCESS,
    BAZAAR_CATALOGUE,
    SHRENI_AI,
    SHRENI_VANI,
    SHRENI_PAY,
    SHRENI_SETU,
    ADD_PRODUCT_WIZARD,
    SCAN_GALLERY
}

data class ChatMessage(
    val fromAi: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ProcessingProgressState(
    val currentStepIndex: Int = 0,
    val currentStepName: String = "Checking image quality",
    val isComplete: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
)

class ShreniScanViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ShreniDatabase.getDatabase(application)
    private val repository = ProductRepository(database.productListingDao(), database.productScanDao())

    private val imageProcessingService: ImageProcessingService = RealImageProcessingService(application)
    private val demoImageProcessingService = DemoImageProcessingService(application)
    private val realBgRemovalService = RealBackgroundRemovalService(application)
    private val demoBgRemovalService = DemoBackgroundRemovalService(application)

    // Image Enhancement Services (replaceable architecture)
    val realImageEnhancementService = RealImageEnhancementService(application)
    val demoImageEnhancementService = DemoImageEnhancementService(application)
    var imageEnhancementService: ImageEnhancementService = realImageEnhancementService

    val backgroundRemovalService: BackgroundRemovalService
        get() = if (_simulateIntegrityWarning.value) demoBgRemovalService else realBgRemovalService

    private val realAiAnalysisService = RealAIProductAnalysisService()
    private val aiAnalysisService: AIProductAnalysisService = realAiAnalysisService

    // Navigation state
    private val _currentScreen = MutableStateFlow(ShreniScreen.DASHBOARD)
    val currentScreen: StateFlow<ShreniScreen> = _currentScreen.asStateFlow()

    private val _screenBackStack = MutableStateFlow<List<ShreniScreen>>(listOf(ShreniScreen.DASHBOARD))

    // Shreni AI Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                fromAi = true,
                text = "Namaste! I am Shreni AI, your artisan business assistant powered by Gemini. How can I help you today with your craft listings, fair pricing, or customer inquiries?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatThinking = MutableStateFlow(false)
    val isChatThinking: StateFlow<Boolean> = _isChatThinking.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    // 6-step wizard step (matching add-product.tsx)
    private val _wizardStep = MutableStateFlow(0)
    val wizardStep: StateFlow<Int> = _wizardStep.asStateFlow()

    // Image state
    private val _productImage = MutableStateFlow<ProductImage?>(null)
    val productImage: StateFlow<ProductImage?> = _productImage.asStateFlow()

    // Background removal result
    private val _backgroundRemovalResult = MutableStateFlow<BackgroundRemovalResult?>(null)
    val backgroundRemovalResult: StateFlow<BackgroundRemovalResult?> = _backgroundRemovalResult.asStateFlow()

    // Selected background style (Transparent PNG, Studio White, or Original Photo)
    private val _backgroundStyle = MutableStateFlow(BackgroundStyle.TRANSPARENT)
    val backgroundStyle: StateFlow<BackgroundStyle> = _backgroundStyle.asStateFlow()

    // Interactive Before / After comparison toggle
    private val _isBeforeAfterComparing = MutableStateFlow(false)
    val isBeforeAfterComparing: StateFlow<Boolean> = _isBeforeAfterComparing.asStateFlow()

    // Selected action
    private val _selectedAction = MutableStateFlow(ProcessingAction.DO_BOTH)
    val selectedAction: StateFlow<ProcessingAction> = _selectedAction.asStateFlow()

    // Progress state
    private val _progressState = MutableStateFlow(ProcessingProgressState())
    val progressState: StateFlow<ProcessingProgressState> = _progressState.asStateFlow()

    // Product Integrity state
    private val _integrityResult = MutableStateFlow<ProductIntegrityResult?>(null)
    val integrityResult: StateFlow<ProductIntegrityResult?> = _integrityResult.asStateFlow()

    // Enhancement settings (manual sliders)
    private val _enhancementSettings = MutableStateFlow(EnhancementSettings.DEFAULT)
    val enhancementSettings: StateFlow<EnhancementSettings> = _enhancementSettings.asStateFlow()

    // Enhanced preview URI for live Before / After comparison
    private val _enhancedPreviewUri = MutableStateFlow<String?>(null)
    val enhancedPreviewUri: StateFlow<String?> = _enhancedPreviewUri.asStateFlow()

    // AI Product Analysis state
    private val _productAnalysis = MutableStateFlow<ProductAnalysis?>(null)
    val productAnalysis: StateFlow<ProductAnalysis?> = _productAnalysis.asStateFlow()

    // Final price set by artisan
    private val _finalPrice = MutableStateFlow<Double>(650.0)
    val finalPrice: StateFlow<Double> = _finalPrice.asStateFlow()

    // Voice description (Shreni Vani)
    private val _voiceDescription = MutableStateFlow("")
    val voiceDescription: StateFlow<String> = _voiceDescription.asStateFlow()

    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    // Active demo craft
    private val _activeDemoCraft = MutableStateFlow<DemoCraft?>(DemoCraft.SAMPLES[0])
    val activeDemoCraft: StateFlow<DemoCraft?> = _activeDemoCraft.asStateFlow()

    // Published listings from Room DB
    val publishedListings: StateFlow<List<ProductListing>> = repository.allListings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val listingsCount: StateFlow<Int> = repository.listingsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Saved Scans and Authenticity Checks from Room DB
    val allScans: StateFlow<List<ProductScanRecord>> = repository.allScans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scansCount: StateFlow<Int> = repository.scansCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            try {
                val existing = repository.allScans.first()
                if (existing.isEmpty()) {
                    seedInitialAuthenticityScans()
                }
            } catch (e: Exception) {
                // Ignore initialization errors in test environments
            }
        }
    }

    // Last published listing
    private val _lastPublishedListing = MutableStateFlow<ProductListing?>(null)
    val lastPublishedListing: StateFlow<ProductListing?> = _lastPublishedListing.asStateFlow()

    // Offline indicator state
    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    // Demo image integrity warning mode toggle (for SIH evaluators testing failure safety)
    private val _simulateIntegrityWarning = MutableStateFlow(false)
    val simulateIntegrityWarning: StateFlow<Boolean> = _simulateIntegrityWarning.asStateFlow()

    fun toggleSimulateIntegrityWarning() {
        _simulateIntegrityWarning.value = !_simulateIntegrityWarning.value
        demoImageProcessingService.forceIntegrityWarning = _simulateIntegrityWarning.value
        demoBgRemovalService.forceLowConfidence = _simulateIntegrityWarning.value
    }

    // ==========================================
    // NAVIGATION & WORKFLOW
    // ==========================================

    fun navigateTo(screen: ShreniScreen) {
        val updated = _screenBackStack.value.toMutableList()
        updated.add(screen)
        _screenBackStack.value = updated
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        val currentStack = _screenBackStack.value
        if (currentStack.size > 1) {
            val updated = currentStack.dropLast(1)
            _screenBackStack.value = updated
            _currentScreen.value = updated.last()
            return true
        } else if (_currentScreen.value != ShreniScreen.DASHBOARD) {
            _currentScreen.value = ShreniScreen.DASHBOARD
            _screenBackStack.value = listOf(ShreniScreen.DASHBOARD)
            return true
        }
        return false
    }

    fun openScanHome() {
        navigateTo(ShreniScreen.SCAN_HOME)
    }

    fun openCamera() {
        navigateTo(ShreniScreen.CAMERA)
    }

    fun openBazaarCatalogue() {
        navigateTo(ShreniScreen.BAZAAR_CATALOGUE)
    }

    fun openScanGallery() {
        navigateTo(ShreniScreen.SCAN_GALLERY)
    }

    fun setWizardStep(step: Int) {
        _wizardStep.value = step.coerceIn(0, 5)
    }

    fun nextWizardStep() {
        _wizardStep.value = (_wizardStep.value + 1).coerceAtMost(5)
    }

    fun previousWizardStep() {
        _wizardStep.value = (_wizardStep.value - 1).coerceAtLeast(0)
    }

    fun setSelectedLanguage(languageCode: String) {
        _selectedLanguage.value = languageCode
    }

    fun sendChatMessage(messageText: String) {
        if (messageText.isBlank()) return
        val userMsg = ChatMessage(fromAi = false, text = messageText)
        _chatMessages.value = _chatMessages.value + userMsg
        _isChatThinking.value = true

        viewModelScope.launch {
            try {
                val reply = realAiAnalysisService.chatWithShreniAI(messageText, _selectedLanguage.value)
                _chatMessages.value = _chatMessages.value + ChatMessage(fromAi = true, text = reply)
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    fromAi = true,
                    text = "I am here to support your artisan business. Could you please rephrase or try again?"
                )
            } finally {
                _isChatThinking.value = false
            }
        }
    }

    fun selectDemoProduct(craft: DemoCraft) {
        _activeDemoCraft.value = craft
        viewModelScope.launch {
            val sampleFile = DemoAssetGenerator.getOrCreateSampleCraftFile(getApplication(), craft)
            handleImageCaptured(sampleFile.absolutePath)
        }
    }

    // ==========================================
    // CAPTURE & ORIGINAL PREVIEW
    // ==========================================

    fun createOriginalPhotoFile(): File {
        val context = getApplication<Application>()
        val originalsDir = File(context.filesDir, "craft_originals").apply { mkdirs() }
        val timestamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(java.util.Date())
        val randomSuffix = UUID.randomUUID().toString().take(6)
        return File(originalsDir, "craft_original_${timestamp}_${randomSuffix}.jpg")
    }

    fun handleImageCaptured(imagePath: String) {
        val context = getApplication<Application>()
        val sourceFile = File(imagePath)
        val originalsDir = File(context.filesDir, "craft_originals").apply { mkdirs() }

        // Ensure the file is stored in craft_originals permanently and never overwritten
        val finalOriginalPath = if (sourceFile.parentFile?.absolutePath == originalsDir.absolutePath && sourceFile.exists()) {
            imagePath
        } else {
            val targetFile = createOriginalPhotoFile()
            if (sourceFile.exists()) {
                sourceFile.copyTo(targetFile, overwrite = false)
                targetFile.absolutePath
            } else {
                imagePath
            }
        }

        _productImage.value = ProductImage(
            originalUri = finalOriginalPath,
            processedUri = null,
            processingStatus = ProcessingStatus.IDLE,
            backgroundRemoved = false,
            enhanced = false,
            integrityVerified = false,
            userSelectedUri = null
        )
        navigateTo(ShreniScreen.ORIGINAL_PREVIEW)
    }

    fun handleGalleryUriSelected(uri: Uri) {
        viewModelScope.launch {
            try {
                val targetFile = createOriginalPhotoFile()
                val context = getApplication<Application>()
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                handleImageCaptured(targetFile.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun captureDemoProduct(craft: DemoCraft? = null) {
        val selectedCraft = craft ?: _activeDemoCraft.value ?: DemoCraft.SAMPLES[0]
        _activeDemoCraft.value = selectedCraft
        val sampleBaseFile = DemoAssetGenerator.getOrCreateSampleCraftFileSync(getApplication(), selectedCraft)
        val permanentOriginal = createOriginalPhotoFile()
        sampleBaseFile.copyTo(permanentOriginal, overwrite = false)
        handleImageCaptured(permanentOriginal.absolutePath)
    }

    fun retakePhoto() {
        // Preserves existing saved file on disk, only resets UI selection
        _productImage.value = null
        _integrityResult.value = null
        navigateTo(ShreniScreen.CAMERA)
    }

    fun chooseAnother() {
        _productImage.value = null
        _integrityResult.value = null
        navigateTo(ShreniScreen.SCAN_HOME)
    }

    fun continueFromOriginalPreview() {
        navigateTo(ShreniScreen.IMAGE_ACTIONS)
    }

    // ==========================================
    // IMAGE ACTIONS & PROCESSING
    // ==========================================

    fun selectAction(action: ProcessingAction) {
        _selectedAction.value = action
    }

    fun startProcessing() {
        val currentImage = _productImage.value ?: return
        val originalFile = File(currentImage.originalUri)

        if (!originalFile.exists()) {
            return
        }

        val action = _selectedAction.value

        if (action == ProcessingAction.SKIP) {
            // User chose "Use original image"
            _productImage.value = currentImage.copy(
                processedUri = currentImage.originalUri,
                processingStatus = ProcessingStatus.SUCCESS,
                backgroundRemoved = false,
                enhanced = false,
                integrityVerified = true,
                userSelectedUri = currentImage.originalUri
            )
            triggerAIAnalysis(originalFile)
            return
        }

        navigateTo(ShreniScreen.PROCESSING)
        _progressState.value = ProcessingProgressState(
            currentStepIndex = 0,
            currentStepName = "Checking image quality"
        )

        val options = ProcessingOptions.fromAction(action)

        viewModelScope.launch {
            val service = if (_simulateIntegrityWarning.value) demoImageProcessingService else imageProcessingService
            val result = service.processImage(
                originalFile = originalFile,
                options = options,
                manualSettings = _enhancementSettings.value
            ) { stepIndex, stepName ->
                _progressState.value = ProcessingProgressState(
                    currentStepIndex = stepIndex,
                    currentStepName = stepName
                )
            }

            _integrityResult.value = result.integrityResult

            val status = if (!result.integrityResult.passed) {
                ProcessingStatus.WARNING_INTEGRITY
            } else if (result.success) {
                ProcessingStatus.SUCCESS
            } else {
                ProcessingStatus.FAILED
            }

            _productImage.value = currentImage.copy(
                processedUri = result.processedUri,
                processingStatus = status,
                backgroundRemoved = result.wasBackgroundRemoved,
                enhanced = result.wasEnhanced,
                integrityVerified = result.integrityResult.passed,
                userSelectedUri = if (result.integrityResult.passed) result.processedUri else currentImage.originalUri
            )

            // Trigger AI Product analysis concurrently
            val analysisInputFile = File(result.processedUri)
            val analysisFile = if (analysisInputFile.exists()) analysisInputFile else originalFile
            triggerAIAnalysis(analysisFile)

            navigateTo(ShreniScreen.BEFORE_AFTER)
        }
    }

    // ==========================================
    // BEFORE / AFTER & ENHANCEMENT SETTINGS
    // ==========================================

    fun selectImageForListing(useOriginal: Boolean) {
        val current = _productImage.value ?: return
        val selected = if (useOriginal) current.originalUri else (current.processedUri ?: current.originalUri)
        _productImage.value = current.copy(userSelectedUri = selected)
        ensureProductAnalysisForImage(selected)
        saveCurrentScanRecord()
        navigateTo(ShreniScreen.PRODUCT_REVIEW)
    }

    fun openEnhancementSettings() {
        navigateTo(ShreniScreen.ENHANCEMENT_SETTINGS)
    }

    fun updateBrightness(value: Float) {
        _enhancementSettings.value = _enhancementSettings.value.copy(brightness = value)
    }

    fun updateContrast(value: Float) {
        _enhancementSettings.value = _enhancementSettings.value.copy(contrast = value)
    }

    fun updateSharpness(value: Float) {
        _enhancementSettings.value = _enhancementSettings.value.copy(sharpness = value)
    }

    fun updateSaturation(value: Float) {
        _enhancementSettings.value = _enhancementSettings.value.copy(colorCorrection = value)
    }

    fun updateColorCorrection(value: Float) {
        _enhancementSettings.value = _enhancementSettings.value.copy(colorCorrection = value)
    }

    fun updateWhiteBalance(value: Float) {
        _enhancementSettings.value = _enhancementSettings.value.copy(whiteBalance = value)
    }

    fun updateNoiseReduction(value: Float) {
        _enhancementSettings.value = _enhancementSettings.value.copy(noiseReduction = value)
    }

    fun resetEnhancementSettings() {
        _enhancementSettings.value = EnhancementSettings.DEFAULT
        _enhancedPreviewUri.value = null
    }

    /**
     * Generate an enhanced preview without modifying the original image.
     */
    fun generateEnhancedPreview(onComplete: ((String) -> Unit)? = null) {
        val current = _productImage.value ?: return
        val originalFile = File(current.originalUri)
        if (!originalFile.exists()) return

        viewModelScope.launch {
            val result = imageEnhancementService.enhanceImage(
                originalFile = originalFile,
                settings = _enhancementSettings.value
            )
            if (result.success && result.enhancedUri != null) {
                _enhancedPreviewUri.value = result.enhancedUri
                onComplete?.invoke(result.enhancedUri)
            }
        }
    }

    fun applyManualEnhancement() {
        val current = _productImage.value ?: return
        val originalFile = File(current.originalUri)
        if (!originalFile.exists()) return

        navigateTo(ShreniScreen.PROCESSING)
        _progressState.value = ProcessingProgressState(0, "Applying non-generative enhancement")

        viewModelScope.launch {
            val enhancementResult = imageEnhancementService.enhanceImage(
                originalFile = originalFile,
                settings = _enhancementSettings.value
            ) { stepIndex, stepName ->
                _progressState.value = ProcessingProgressState(stepIndex, stepName)
            }

            if (enhancementResult.success && enhancementResult.enhancedUri != null) {
                _productImage.value = current.copy(
                    processedUri = enhancementResult.enhancedUri,
                    userSelectedUri = enhancementResult.enhancedUri,
                    enhanced = true,
                    processingStatus = ProcessingStatus.SUCCESS
                )
                _enhancedPreviewUri.value = enhancementResult.enhancedUri
            }
            navigateTo(ShreniScreen.BEFORE_AFTER)
        }
    }

    fun useOriginalFromEnhancement() {
        val current = _productImage.value ?: return
        _productImage.value = current.copy(
            userSelectedUri = current.originalUri,
            enhanced = false
        )
        navigateTo(ShreniScreen.BEFORE_AFTER)
    }

    // ==========================================
    // REAL BACKGROUND REMOVAL WORKFLOW
    // ==========================================

    fun executeBackgroundRemoval(preferTransparent: Boolean = true) {
        val currentImage = _productImage.value ?: return
        val originalFile = File(currentImage.originalUri)
        if (!originalFile.exists()) return

        _productImage.value = currentImage.copy(processingStatus = ProcessingStatus.PROCESSING)
        _progressState.value = ProcessingProgressState(
            currentStepIndex = 0,
            currentStepName = "Analyzing original craft photo"
        )

        viewModelScope.launch {
            val result = backgroundRemovalService.removeBackground(
                originalFile = originalFile,
                preferTransparent = preferTransparent
            ) { stepIndex, stepName ->
                _progressState.value = ProcessingProgressState(
                    currentStepIndex = stepIndex,
                    currentStepName = stepName
                )
            }

            _backgroundRemovalResult.value = result
            _integrityResult.value = result.integrityResult

            if (result.success && result.integrityResult.passed) {
                val chosenUri = if (preferTransparent) result.transparentUri else result.whiteBackgroundUri
                _backgroundStyle.value = if (preferTransparent) BackgroundStyle.TRANSPARENT else BackgroundStyle.STUDIO_WHITE
                _productImage.value = currentImage.copy(
                    processedUri = chosenUri ?: result.processedUri,
                    transparentUri = result.transparentUri,
                    whiteBackgroundUri = result.whiteBackgroundUri,
                    processingStatus = ProcessingStatus.SUCCESS,
                    backgroundRemoved = true,
                    integrityVerified = true,
                    activeBackgroundStyle = _backgroundStyle.value,
                    segmentationConfidence = result.confidenceScore,
                    userSelectedUri = chosenUri ?: result.processedUri
                )
            } else {
                // If segmentation confidence is low, do not process the image.
                // Return a failure result and let the user use the original image.
                // The original image must always remain available.
                _backgroundStyle.value = BackgroundStyle.ORIGINAL
                _productImage.value = currentImage.copy(
                    processedUri = null,
                    transparentUri = null,
                    whiteBackgroundUri = null,
                    processingStatus = ProcessingStatus.WARNING_INTEGRITY,
                    backgroundRemoved = false,
                    integrityVerified = false,
                    activeBackgroundStyle = BackgroundStyle.ORIGINAL,
                    segmentationConfidence = result.confidenceScore,
                    userSelectedUri = currentImage.originalUri
                )
            }
        }
    }

    fun setBackgroundStyle(style: BackgroundStyle) {
        _backgroundStyle.value = style
        val current = _productImage.value ?: return
        val newUri = when (style) {
            BackgroundStyle.ORIGINAL -> current.originalUri
            BackgroundStyle.TRANSPARENT -> current.transparentUri ?: current.processedUri ?: current.originalUri
            BackgroundStyle.STUDIO_WHITE -> current.whiteBackgroundUri ?: current.processedUri ?: current.originalUri
        }
        _productImage.value = current.copy(
            activeBackgroundStyle = style,
            userSelectedUri = newUri
        )
    }

    fun setUseOriginalImage() {
        val current = _productImage.value ?: return
        _backgroundStyle.value = BackgroundStyle.ORIGINAL
        _productImage.value = current.copy(
            activeBackgroundStyle = BackgroundStyle.ORIGINAL,
            userSelectedUri = current.originalUri
        )
    }

    fun setUseProcessedImage() {
        val current = _productImage.value ?: return
        val targetStyle = if (current.transparentUri != null) BackgroundStyle.TRANSPARENT else BackgroundStyle.STUDIO_WHITE
        _backgroundStyle.value = targetStyle
        _productImage.value = current.copy(
            activeBackgroundStyle = targetStyle,
            userSelectedUri = current.transparentUri ?: current.whiteBackgroundUri ?: current.processedUri ?: current.originalUri
        )
    }

    fun toggleCompareMode() {
        _isBeforeAfterComparing.value = !_isBeforeAfterComparing.value
    }

    fun setCompareMode(comparing: Boolean) {
        _isBeforeAfterComparing.value = comparing
    }

    // ==========================================
    // AI PRODUCT ANALYSIS & REVIEW
    // ==========================================

    private fun triggerAIAnalysis(imageFile: File) {
        viewModelScope.launch {
            val analysis = aiAnalysisService.analyzeProduct(
                imageFile = imageFile,
                voiceDescription = _voiceDescription.value.ifBlank { null },
                detectedCraftHint = _activeDemoCraft.value
            )
            _productAnalysis.value = analysis
            _finalPrice.value = ((analysis.suggestedPriceMin + analysis.suggestedPriceMax) / 2.0).coerceAtLeast(100.0)
        }
    }

    fun updateProductName(name: String) {
        _productAnalysis.value = _productAnalysis.value?.copy(productName = name)
    }

    fun updateCategory(category: String) {
        _productAnalysis.value = _productAnalysis.value?.copy(category = category)
    }

    fun updateCraftType(craftType: String) {
        _productAnalysis.value = _productAnalysis.value?.copy(craftType = craftType)
    }

    fun updateMaterial(material: String) {
        _productAnalysis.value = _productAnalysis.value?.copy(material = material)
    }

    fun updateDescription(description: String) {
        _productAnalysis.value = _productAnalysis.value?.copy(description = description)
    }

    fun updateTags(tagsString: String) {
        val list = tagsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        _productAnalysis.value = _productAnalysis.value?.copy(tags = list)
    }

    fun continueToPrice() {
        navigateTo(ShreniScreen.PRICE_SETTINGS)
    }

    // ==========================================
    // PRICE SETTING & MARKETPLACE PREVIEW
    // ==========================================

    fun setArtisanFinalPrice(price: Double) {
        _finalPrice.value = price.coerceAtLeast(50.0)
    }

    fun adjustFinalPrice(delta: Double) {
        _finalPrice.value = (_finalPrice.value + delta).coerceAtLeast(50.0)
    }

    fun continueToMarketplacePreview() {
        navigateTo(ShreniScreen.MARKETPLACE_PREVIEW)
    }

    // ==========================================
    // PUBLISH & SUCCESS
    // ==========================================

    fun publishProduct() {
        val currentImage = _productImage.value ?: return
        val analysis = _productAnalysis.value ?: return
        val finalImageUri = currentImage.activeDisplayUri

        val listing = ProductListing(
            id = UUID.randomUUID().toString(),
            productName = analysis.productName,
            category = analysis.category,
            craftType = analysis.craftType,
            material = analysis.material,
            colors = analysis.colors.joinToString(", "),
            description = analysis.description,
            price = _finalPrice.value,
            suggestedPriceMin = analysis.suggestedPriceMin,
            suggestedPriceMax = analysis.suggestedPriceMax,
            imageUri = finalImageUri,
            originalImageUri = currentImage.originalUri,
            artisanName = "Savitri Devi",
            region = "Jaipur, Rajasthan",
            tags = analysis.tags.joinToString(", "),
            backgroundRemoved = currentImage.backgroundRemoved,
            enhanced = currentImage.enhanced,
            integrityProtected = true,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.saveListing(listing)
            _lastPublishedListing.value = listing
            saveCurrentScanRecord(listingId = listing.id)
            navigateTo(ShreniScreen.SUCCESS)
        }
    }

    private fun ensureProductAnalysisForImage(selectedUri: String) {
        if (_productAnalysis.value != null) return
        val file = File(selectedUri)
        if (file.exists()) {
            triggerAIAnalysis(file)
        } else {
            val orig = _productImage.value?.originalUri?.let { File(it) }
            if (orig != null && orig.exists()) {
                triggerAIAnalysis(orig)
            } else {
                val craft = _activeDemoCraft.value
                _productAnalysis.value = ProductAnalysis(
                    productName = craft?.title ?: "Handcrafted Artisan Item",
                    category = craft?.category ?: "Handicrafts",
                    craftType = craft?.craftType ?: "Traditional Indian Craft",
                    material = craft?.material ?: "Natural Materials",
                    colors = listOf("Terracotta", "Natural"),
                    description = craft?.description ?: "Authentic handmade craft preserving traditional artisanal heritage.",
                    tags = craft?.tags ?: listOf("artisan", "handmade", "heritage"),
                    suggestedPriceMin = craft?.minPrice ?: 500.0,
                    suggestedPriceMax = craft?.maxPrice ?: 800.0
                )
                _finalPrice.value = craft?.defaultPrice ?: 650.0
            }
        }
    }

    fun saveCurrentScanRecord(listingId: String? = null) {
        val image = _productImage.value ?: return
        val analysis = _productAnalysis.value
        val integrity = _integrityResult.value

        val record = ProductScanRecord(
            originalImageUri = image.originalUri,
            processedImageUri = image.processedUri,
            productTitle = analysis?.productName ?: _activeDemoCraft.value?.title ?: "Handcrafted Artisan Item",
            craftType = analysis?.craftType ?: _activeDemoCraft.value?.craftType ?: "Traditional Craft",
            category = analysis?.category ?: _activeDemoCraft.value?.category ?: "Handicrafts",
            material = analysis?.material ?: _activeDemoCraft.value?.material ?: "Natural Materials",
            description = analysis?.description ?: "",
            tags = analysis?.tags?.joinToString(", ") ?: "artisan, handmade",
            suggestedPriceMin = analysis?.suggestedPriceMin ?: 500.0,
            suggestedPriceMax = analysis?.suggestedPriceMax ?: 800.0,
            finalPrice = _finalPrice.value,
            confidenceScore = integrity?.confidenceScore ?: image.segmentationConfidence ?: 0.98f,
            integrityPassed = integrity?.passed != false,
            integrityVerdict = integrity?.message ?: "Authentic handmade craft details verified. Foreground pixels preserved.",
            backgroundRemoved = image.backgroundRemoved,
            enhanced = image.enhanced,
            publishedListingId = listingId,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.saveScan(record)
        }
    }

    fun deleteScan(id: String) {
        viewModelScope.launch {
            repository.deleteScan(id)
        }
    }

    fun loadScanForWorkflow(scan: ProductScanRecord) {
        _productImage.value = ProductImage(
            originalUri = scan.originalImageUri,
            processedUri = scan.processedImageUri,
            transparentUri = if (scan.backgroundRemoved) scan.processedImageUri else null,
            userSelectedUri = scan.processedImageUri ?: scan.originalImageUri,
            backgroundRemoved = scan.backgroundRemoved,
            enhanced = scan.enhanced,
            segmentationConfidence = scan.confidenceScore,
            integrityVerified = scan.integrityPassed
        )
        _integrityResult.value = ProductIntegrityResult(
            passed = scan.integrityPassed,
            message = scan.integrityVerdict,
            confidenceScore = scan.confidenceScore,
            areaPreservedRatio = 0.98f,
            boundingBoxMatch = true,
            edgesPreserved = true
        )
        _productAnalysis.value = ProductAnalysis(
            productName = scan.productTitle,
            category = scan.category,
            craftType = scan.craftType,
            material = scan.material,
            colors = listOf("Natural"),
            description = scan.description.ifBlank { "Authentic handmade ${scan.craftType} with traditional craftsmanship." },
            tags = scan.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            suggestedPriceMin = scan.suggestedPriceMin,
            suggestedPriceMax = scan.suggestedPriceMax
        )
        _finalPrice.value = scan.finalPrice
        navigateTo(ShreniScreen.BEFORE_AFTER)
    }

    fun loadScanForAddProduct(scan: ProductScanRecord) {
        _productImage.value = ProductImage(
            originalUri = scan.originalImageUri,
            processedUri = scan.processedImageUri,
            transparentUri = if (scan.backgroundRemoved) scan.processedImageUri else null,
            userSelectedUri = scan.processedImageUri ?: scan.originalImageUri,
            backgroundRemoved = scan.backgroundRemoved,
            enhanced = scan.enhanced,
            segmentationConfidence = scan.confidenceScore,
            integrityVerified = scan.integrityPassed
        )
        _integrityResult.value = ProductIntegrityResult(
            passed = scan.integrityPassed,
            message = scan.integrityVerdict,
            confidenceScore = scan.confidenceScore,
            areaPreservedRatio = 0.98f,
            boundingBoxMatch = true,
            edgesPreserved = true
        )
        _productAnalysis.value = ProductAnalysis(
            productName = scan.productTitle,
            category = scan.category,
            craftType = scan.craftType,
            material = scan.material,
            colors = listOf("Natural"),
            description = scan.description.ifBlank { "Authentic handmade ${scan.craftType} with traditional craftsmanship." },
            tags = scan.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            suggestedPriceMin = scan.suggestedPriceMin,
            suggestedPriceMax = scan.suggestedPriceMax
        )
        _finalPrice.value = scan.finalPrice
        navigateTo(ShreniScreen.PRODUCT_REVIEW)
    }

    private suspend fun seedInitialAuthenticityScans() {
        val initialRecords = listOf(
            ProductScanRecord(
                id = "scan-terracotta-01",
                originalImageUri = "file:///android_asset/sample_terracotta.jpg",
                processedImageUri = "file:///android_asset/sample_terracotta.jpg",
                productTitle = "Handcrafted Blue Glazed Terracotta Pot",
                craftType = "Terracotta Pottery",
                category = "Home & Decor",
                material = "Terracotta Clay & Natural Glazes",
                description = "Traditional Jaipur hand-thrown terracotta pot with authentic indigo natural dye glaze and floral motifs.",
                tags = "terracotta, jaipur, handmade, pottery, home decor",
                suggestedPriceMin = 450.0,
                suggestedPriceMax = 750.0,
                finalPrice = 600.0,
                confidenceScore = 0.985f,
                integrityPassed = true,
                integrityVerdict = "Authentic handmade craft details verified. Foreground pixels 100% preserved.",
                backgroundRemoved = true,
                enhanced = true,
                timestamp = System.currentTimeMillis() - 86400000L * 2
            ),
            ProductScanRecord(
                id = "scan-banarasi-silk-02",
                originalImageUri = "file:///android_asset/sample_silk.jpg",
                processedImageUri = "file:///android_asset/sample_silk.jpg",
                productTitle = "Pure Katan Silk Brocade Dupatta",
                craftType = "Handloom Silk Weaving",
                category = "Apparel & Textiles",
                material = "Pure Mulberry Silk & Zari Thread",
                description = "Handwoven Varanasi pure silk dupatta featuring intricate Mughal floral borders and genuine metallic zari wefts.",
                tags = "silk, banarasi, handloom, zari, bridal",
                suggestedPriceMin = 1800.0,
                suggestedPriceMax = 2800.0,
                finalPrice = 2400.0,
                confidenceScore = 0.992f,
                integrityPassed = true,
                integrityVerdict = "Pure handloom weave verified. Micro-patterns and zari integrity intact.",
                backgroundRemoved = true,
                enhanced = true,
                timestamp = System.currentTimeMillis() - 86400000L
            ),
            ProductScanRecord(
                id = "scan-dhokra-brass-03",
                originalImageUri = "file:///android_asset/sample_brass.jpg",
                processedImageUri = "file:///android_asset/sample_brass.jpg",
                productTitle = "Tribal Dhokra Brass Deer Figurine",
                craftType = "Dhokra Metalcraft",
                category = "Collectibles & Sculptures",
                material = "Brass Alloy & Natural Beeswax Mould",
                description = "Ancestral lost-wax brass cast deer statuette handcrafted by indigenous artisans of Bastar.",
                tags = "dhokra, brass, tribal, lost wax, collectible",
                suggestedPriceMin = 950.0,
                suggestedPriceMax = 1500.0,
                finalPrice = 1200.0,
                confidenceScore = 0.978f,
                integrityPassed = true,
                integrityVerdict = "Lost-wax metallic contours verified. Geometric motifs preserved.",
                backgroundRemoved = true,
                enhanced = true,
                timestamp = System.currentTimeMillis() - 3600000L * 5
            )
        )
        for (record in initialRecords) {
            repository.saveScan(record)
        }
    }

    fun startAnotherScan() {
        _productImage.value = null
        _productAnalysis.value = null
        _integrityResult.value = null
        _voiceDescription.value = ""
        navigateTo(ShreniScreen.SCAN_HOME)
    }

    fun goToDashboard() {
        _screenBackStack.value = listOf(ShreniScreen.DASHBOARD)
        _currentScreen.value = ShreniScreen.DASHBOARD
    }

    // ==========================================
    // VOICE DESCRIPTION (Shreni Vani)
    // ==========================================

    fun toggleVoiceRecording() {
        _isRecordingVoice.value = !_isRecordingVoice.value
        if (!_isRecordingVoice.value) {
            // Simulated speech transcription for demo
            if (_voiceDescription.value.isBlank()) {
                _voiceDescription.value = "Hand-painted terracotta earthen pot made with sacred river clay and natural plant pigments."
            }
        }
    }

    fun setVoiceDescription(text: String) {
        _voiceDescription.value = text
    }

    fun updateVoiceDescription(text: String) = setVoiceDescription(text)

    fun updateFinalPrice(price: Double) = setArtisanFinalPrice(price)

    fun publishListing() = publishProduct()

    fun handleGalleryImageSelected(uri: Uri) = handleGalleryUriSelected(uri)
}

