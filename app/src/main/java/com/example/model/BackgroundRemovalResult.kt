package com.example.model

data class BackgroundRemovalResult(
    val success: Boolean,
    val originalUri: String,
    val transparentUri: String? = null,
    val whiteBackgroundUri: String? = null,
    val processedUri: String? = null,
    val confidenceScore: Float = 1.0f,
    val integrityResult: ProductIntegrityResult,
    val isFallbackToOriginal: Boolean = false,
    val errorMessage: String? = null
)
