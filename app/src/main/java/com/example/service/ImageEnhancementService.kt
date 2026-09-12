package com.example.service

import com.example.model.EnhancementSettings
import java.io.File

/**
 * Result of non-generative craft image enhancement.
 * Guarantees original image remains completely unchanged.
 */
data class EnhancementResult(
    val success: Boolean,
    val enhancedFile: File?,
    val enhancedUri: String?,
    val settings: EnhancementSettings,
    val executionTimeMs: Long = 0,
    val message: String = "Artisan enhancement applied cleanly (non-generative)"
)

/**
 * Interface for non-generative craft image enhancement.
 *
 * Allowed:
 * - brightness
 * - contrast
 * - sharpness
 * - color correction
 * - white balance
 * - mild noise reduction
 *
 * Strictly PROHIBITED:
 * - generating pixels representing new product parts
 * - removing product decorations
 * - changing product shape
 * - modifying product geometry
 * - inventing texture
 * - reconstructing missing areas
 *
 * GUARANTEE:
 * The original image file remains completely unchanged.
 */
interface ImageEnhancementService {
    suspend fun enhanceImage(
        originalFile: File,
        settings: EnhancementSettings = EnhancementSettings.DEFAULT,
        onProgress: ((stepIndex: Int, stepName: String) -> Unit)? = null
    ): EnhancementResult
}
