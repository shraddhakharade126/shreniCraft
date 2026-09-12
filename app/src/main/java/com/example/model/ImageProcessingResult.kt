package com.example.model

data class ImageProcessingResult(
    val originalUri: String,
    val processedUri: String,
    val wasBackgroundRemoved: Boolean,
    val wasEnhanced: Boolean,
    val integrityResult: ProductIntegrityResult,
    val success: Boolean,
    val errorMessage: String? = null
)
