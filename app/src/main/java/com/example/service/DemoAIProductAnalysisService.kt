package com.example.service

import com.example.model.DemoCraft
import com.example.model.ProductAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

class DemoAIProductAnalysisService : AIProductAnalysisService {

    override suspend fun analyzeProduct(
        imageFile: File,
        voiceDescription: String?,
        detectedCraftHint: DemoCraft?
    ): ProductAnalysis = withContext(Dispatchers.Default) {
        // Simulate remote inference latency cleanly
        delay(600)

        val hint = detectedCraftHint ?: DemoCraft.SAMPLES[0]

        // Enhance description if voice description was provided
        val combinedDesc = if (!voiceDescription.isNullOrBlank()) {
            "${hint.description} Artisan's note: \"$voiceDescription\""
        } else {
            hint.description
        }

        ProductAnalysis(
            productName = hint.title,
            category = hint.category,
            craftType = hint.craftType,
            material = hint.material,
            colors = hint.colors,
            description = combinedDesc,
            tags = hint.tags,
            suggestedPriceMin = hint.minPrice,
            suggestedPriceMax = hint.maxPrice,
            confidence = 0.96f
        )
    }
}
