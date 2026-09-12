package com.example.model

enum class ProcessingStatus {
    IDLE,
    PROCESSING,
    SUCCESS,
    WARNING_INTEGRITY,
    FAILED
}

data class ProductImage(
    val originalUri: String,
    val processedUri: String? = null,
    val processingStatus: ProcessingStatus = ProcessingStatus.IDLE,
    val backgroundRemoved: Boolean = false,
    val enhanced: Boolean = false,
    val integrityVerified: Boolean = false,
    val userSelectedUri: String? = null // Points to either originalUri or processedUri
) {
    val activeDisplayUri: String
        get() = userSelectedUri ?: processedUri ?: originalUri
}
