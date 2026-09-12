package com.example.service

import com.example.model.DemoCraft
import com.example.model.ProductAnalysis
import java.io.File

interface AIProductAnalysisService {
    suspend fun analyzeProduct(
        imageFile: File,
        voiceDescription: String? = null,
        detectedCraftHint: DemoCraft? = null
    ): ProductAnalysis
}
