package com.example.service

import android.content.Context
import com.example.model.EnhancementSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Demo and testing implementation of [ImageEnhancementService].
 * Fully functional, deterministic, non-generative enhancement for demo crafts and unit tests.
 * Guaranteed: Original image file is never modified.
 */
class DemoImageEnhancementService(
    private val context: Context
) : ImageEnhancementService {

    override suspend fun enhanceImage(
        originalFile: File,
        settings: EnhancementSettings,
        onProgress: ((stepIndex: Int, stepName: String) -> Unit)?
    ): EnhancementResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        onProgress?.invoke(0, "Demo: Reading authentic craft image")

        if (!originalFile.exists()) {
            return@withContext EnhancementResult(
                success = false,
                enhancedFile = null,
                enhancedUri = null,
                settings = settings,
                executionTimeMs = 0,
                message = "Demo: File not found at ${originalFile.absolutePath}"
            )
        }

        try {
            onProgress?.invoke(1, "Demo: Loading craft bitmap")
            val originalBitmap = ImageUtils.decodeSampledBitmap(originalFile)

            onProgress?.invoke(2, "Demo: Applying non-generative craft enhancement")
            val enhancedBitmap = ImageUtils.enhanceProductImage(originalBitmap, settings)

            onProgress?.invoke(3, "Demo: Preserving original file byte-for-byte")
            val outputDir = File(context.cacheDir, "demo_enhanced").apply { mkdirs() }
            val outputFile = File(outputDir, "demo_enhanced_${UUID.randomUUID().toString().take(6)}.jpg")

            val saved = ImageUtils.saveBitmapToFile(enhancedBitmap, outputFile)
            val duration = System.currentTimeMillis() - startTime

            EnhancementResult(
                success = saved && outputFile.exists(),
                enhancedFile = if (saved) outputFile else null,
                enhancedUri = if (saved) outputFile.absolutePath else null,
                settings = settings,
                executionTimeMs = duration,
                message = "Demo enhancement complete (${duration}ms). Zero Generative AI. Original untouched."
            )
        } catch (e: Exception) {
            EnhancementResult(
                success = false,
                enhancedFile = null,
                enhancedUri = null,
                settings = settings,
                executionTimeMs = System.currentTimeMillis() - startTime,
                message = "Demo enhancement error: ${e.message}"
            )
        }
    }
}
