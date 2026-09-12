package com.example.model

data class EnhancementSettings(
    val brightness: Float = 0f,    // -50f to 50f (moderate range to avoid unnatural look)
    val contrast: Float = 1.0f,    // 0.8f to 1.3f
    val sharpness: Float = 0.2f,   // 0f to 1.0f
    val saturation: Float = 1.05f  // 0.8f to 1.3f
) {
    companion object {
        val DEFAULT = EnhancementSettings()
    }
}
