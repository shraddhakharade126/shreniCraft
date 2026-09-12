package com.example.model

data class ProductAnalysis(
    val productName: String,
    val category: String,
    val craftType: String,
    val material: String,
    val colors: List<String>,
    val description: String,
    val tags: List<String>,
    val suggestedPriceMin: Double,
    val suggestedPriceMax: Double,
    val confidence: Float = 0.95f
)
