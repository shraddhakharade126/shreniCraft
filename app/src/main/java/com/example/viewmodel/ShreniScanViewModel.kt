package com.example.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ProductRepository
import com.example.data.ShreniDatabase
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
import com.example.service.AIProductAnalysisService
import com.example.service.DemoAIProductAnalysisService
import com.example.service.DemoAssetGenerator
import com.example.service.DemoImageProcessingService
import com.example.service.ImageProcessingService
import com.example.service.RealImageProcessingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    BAZAAR_CATALOGUE
}

data class ProcessingProgressState(
    val currentStepIndex: Int = 0,
    val currentStepName: String = "Checking image quality",
    val isComplete: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
)

class ShreniScanViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ShreniDatabase.getDatabase(application)
    private val repository = ProductRepository(database.productListingDao())

    private val imageProcessingService: ImageProcessingService = RealImageProcessingService(application)
    private val demoImageProcessingService = DemoImageProcessingService(application)
    private val aiAnalysisService: AIProductAnalysisService = DemoAIProductAnalysisService()

    // Navigation state
    private val _currentScreen = MutableStateFlow(ShreniScreen.DASHBOARD)
    val currentScreen: StateFlow<ShreniScreen> = _currentScreen.asStateFlow()

    private val _screenBackStack = MutableStateFlow<List<ShreniScreen>>(listOf(ShreniScreen.DASHBOARD))

    // Image state
    private val _productImage = MutableStateFlow<ProductImage?>(null)
    val productImage: StateFlow<ProductImage?> = _productImage.asStateFlow()

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

    fun handleImageCaptured(imagePath: String) {
        _productImage.value = ProductImage(
            originalUri = imagePath,
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
                val context = getApplication<Application>()
                val cacheDir = File(context.cacheDir, "gallery_uploads").apply { mkdirs() }
                val targetFile = File(cacheDir, "gallery_${System.currentTimeMillis()}.jpg")

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

    fun retakePhoto() {
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
        _enhancementSettings.value = _enhancementSettings.value.copy(saturation = value)
    }

    fun resetEnhancementSettings() {
        _enhancementSettings.value = EnhancementSettings.DEFAULT
    }

    fun applyManualEnhancement() {
        val current = _productImage.value ?: return
        val originalFile = File(current.originalUri)
        if (!originalFile.exists()) return

        navigateTo(ShreniScreen.PROCESSING)
        _progressState.value = ProcessingProgressState(0, "Applying artisan enhancement")

        viewModelScope.launch {
            val result = imageProcessingService.processImage(
                originalFile = originalFile,
                options = ProcessingOptions(removeBackground = current.backgroundRemoved, enhanceImage = true),
                manualSettings = _enhancementSettings.value
            )
            _productImage.value = current.copy(
                processedUri = result.processedUri,
                userSelectedUri = result.processedUri,
                enhanced = true
            )
            navigateTo(ShreniScreen.BEFORE_AFTER)
        }
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
            navigateTo(ShreniScreen.SUCCESS)
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
}
