package com.example.service

import android.content.Context
import com.example.model.EnhancementSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Production-ready implementation of [ImageEnhancementService].
 * Strictly non-generative:
 * - Allowed: brightness, contrast, sharpness, color correction, white balance, mild noise reduction.
 * - Prohibited: pixel fabrication, decoration removal, shape alteration, geometry warping, texture invention.
 * - GUARANTEE: Original image file is never modified or overwritten.
 */
class RealImageEnhancementService(
    private val context: Context
) : ImageEnhancementService {

    override suspend fun enhanceImage(
        originalFile: File,
        settings: EnhancementSettings,
        onProgress: ((stepIndex: Int, stepName: String) -> Unit)?
    ): EnhancementResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        onProgress?.invoke(0, "Verifying original craft image")

        if (!originalFile.exists()) {
            return@withContext EnhancementResult(
                success = false,
                enhancedFile = null,
                enhancedUri = null,
                settings = settings,
                executionTimeMs = 0,
                message = "Original image not found: ${originalFile.absolutePath}"
            )
        }

        try {
            // STEP 1: Safe memory decode
            onProgress?.invoke(1, "Decoding original craft pixels safely")
            val originalBitmap = ImageUtils.decodeSampledBitmap(originalFile)

            // STEP 2: Non-generative radiometric and frequency enhancement
            onProgress?.invoke(2, "Applying non-generative craft enhancements")
            val enhancedBitmap = ImageUtils.enhanceProductImage(originalBitmap, settings)

            // STEP 3: Save to a separate cache file - ORIGINAL FILE IS NEVER OVERWRITTEN
            onProgress?.invoke(3, "Writing enhanced image without modifying original")
            val outputDir = File(context.cacheDir, "enhanced_crafts").apply { mkdirs() }
            val outputFile = File(outputDir, "enhanced_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg")

            val saved = ImageUtils.saveBitmapToFile(enhancedBitmap, outputFile)
            val duration = System.currentTimeMillis() - startTime

            if (saved && outputFile.exists()) {
                EnhancementResult(
                    success = true,
                    enhancedFile = outputFile,
                    enhancedUri = outputFile.absolutePath,
                    settings = settings,
                    executionTimeMs = duration,
                    message = "Non-generative enhancement applied (${duration}ms). Original photo 100% untouched."
                )
            } else {
                EnhancementResult(
                    success = false,
                    enhancedFile = null,
                    enhancedUri = null,
                    settings = settings,
                    executionTimeMs = duration,
                    message = "Failed to save enhanced image file"
                )
            }
        } catch (e: Exception) {
            EnhancementResult(
                success = false,
                enhancedFile = null,
                enhancedUri = null,
                settings = settings,
                executionTimeMs = System.currentTimeMillis() - startTime,
                message = "Enhancement error: ${e.localizedMessage ?: e.message}"
            )
        }
    }
}
