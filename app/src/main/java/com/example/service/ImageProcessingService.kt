package com.example.service

import com.example.model.EnhancementSettings
import com.example.model.ImageProcessingResult
import com.example.model.ProcessingOptions
import java.io.File

interface ImageProcessingService {
    suspend fun processImage(
        originalFile: File,
        options: ProcessingOptions,
        manualSettings: EnhancementSettings? = null,
        onProgressUpdate: ((stepIndex: Int, stepName: String) -> Unit)? = null
    ): ImageProcessingResult
}
