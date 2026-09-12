package com.example.model

/**
 * Non-generative enhancement parameters.
 * Allowed: brightness, contrast, sharpness, color correction, white balance, mild noise reduction.
 * Strictly FORBIDDEN: generative pixels, shape changes, texture hallucination, geometry modification.
 */
data class EnhancementSettings(
    val brightness: Float = 0f,          // -30f to 30f
    val contrast: Float = 1.0f,          // 0.8f to 1.3f
    val sharpness: Float = 0.2f,         // 0f to 0.8f (texture & edge clarity)
    val colorCorrection: Float = 1.05f,  // 0.8f to 1.4f (color vibrancy and accurate tones)
    val whiteBalance: Float = 0f,        // -20f to 20f (cool to warm color temperature)
    val noiseReduction: Float = 0.1f     // 0f to 0.5f (mild edge-preserving sensor noise smoothing)
) {
    // Backward compatibility alias for saturation
    val saturation: Float get() = colorCorrection

    companion object {
        val DEFAULT = EnhancementSettings()
    }
}

