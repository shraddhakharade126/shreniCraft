package com.example.model

enum class ProcessingStatus {
    IDLE,
    PROCESSING,
    SUCCESS,
    WARNING_INTEGRITY,
    FAILED
}

enum class BackgroundStyle {
    TRANSPARENT,
    STUDIO_WHITE,
    ORIGINAL
}

data class ProductImage(
    val originalUri: String,
    val processedUri: String? = null,
    val transparentUri: String? = null,
    val whiteBackgroundUri: String? = null,
    val processingStatus: ProcessingStatus = ProcessingStatus.IDLE,
    val backgroundRemoved: Boolean = false,
    val enhanced: Boolean = false,
    val integrityVerified: Boolean = false,
    val userSelectedUri: String? = null, // Points to either originalUri or processedUri
    val activeBackgroundStyle: BackgroundStyle = BackgroundStyle.TRANSPARENT,
    val segmentationConfidence: Float = 1.0f
) {
    val activeDisplayUri: String
        get() = userSelectedUri ?: when (activeBackgroundStyle) {
            BackgroundStyle.ORIGINAL -> originalUri
            BackgroundStyle.TRANSPARENT -> transparentUri ?: processedUri ?: originalUri
            BackgroundStyle.STUDIO_WHITE -> whiteBackgroundUri ?: processedUri ?: originalUri
        }
}
