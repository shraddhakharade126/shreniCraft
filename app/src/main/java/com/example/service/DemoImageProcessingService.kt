package com.example.service

import android.content.Context
import com.example.model.EnhancementSettings
import com.example.model.ImageProcessingResult
import com.example.model.ProcessingOptions
import com.example.model.ProductIntegrityResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

class DemoImageProcessingService(
    private val context: Context,
    private val realService: RealImageProcessingService = RealImageProcessingService(context)
) : ImageProcessingService {

    // Can simulate low-confidence segmentation or normal flow
    var forceIntegrityWarning: Boolean = false

    override suspend fun processImage(
        originalFile: File,
        options: ProcessingOptions,
        manualSettings: EnhancementSettings?,
        onProgressUpdate: ((stepIndex: Int, stepName: String) -> Unit)?
    ): ImageProcessingResult = withContext(Dispatchers.Default) {
        if (forceIntegrityWarning) {
            // Emulate edge warning/integrity check failure
            onProgressUpdate?.invoke(0, "Checking image quality")
            delay(250)
            onProgressUpdate?.invoke(1, "Detecting product")
            delay(250)
            onProgressUpdate?.invoke(2, "Removing background")
            delay(250)
            onProgressUpdate?.invoke(3, "Enhancing image")
            delay(200)
            onProgressUpdate?.invoke(4, "Checking product integrity")
            delay(250)

            return@withContext ImageProcessingResult(
                originalUri = originalFile.absolutePath,
                processedUri = originalFile.absolutePath,
                wasBackgroundRemoved = false,
                wasEnhanced = false,
                integrityResult = ProductIntegrityResult(
                    passed = false,
                    message = "Product boundary is unclear or craft touches the frame edge. Keeping original photo.",
                    areaPreservedRatio = 0.60f,
                    boundingBoxMatch = false,
                    edgesPreserved = false,
                    isBorderCutWarning = true
                ),
                success = true
            )
        }

        // Delegate to the real non-generative pipeline
        realService.processImage(originalFile, options, manualSettings, onProgressUpdate)
    }
}
