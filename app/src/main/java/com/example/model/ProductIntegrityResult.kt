package com.example.model

data class ProductIntegrityResult(
    val passed: Boolean,
    val message: String,
    val areaPreservedRatio: Float = 1.0f,
    val boundingBoxMatch: Boolean = true,
    val edgesPreserved: Boolean = true,
    val isBorderCutWarning: Boolean = false,
    val confidenceScore: Float = 1.0f
)
