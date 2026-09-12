package com.example.service

import com.example.model.BackgroundRemovalResult
import java.io.File

interface BackgroundRemovalService {
    /**
     * Executes the Shreni Scan background removal architecture:
     * Original Image
     * → segmentation
     * → foreground mask
     * → mask validation (ProductIntegrityValidator)
     * → preserve original foreground
     * → remove background
     * → transparent/white background
     *
     * CRITICAL: NEVER regenerates or uses generative AI to fabricate the artisan's craft.
     * The original foreground camera pixels are 100% preserved.
     */
    suspend fun removeBackground(
        originalFile: File,
        preferTransparent: Boolean = true,
        onProgressUpdate: ((stepIndex: Int, stepName: String) -> Unit)? = null
    ): BackgroundRemovalResult
}
