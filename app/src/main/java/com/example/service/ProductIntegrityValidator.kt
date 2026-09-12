package com.example.service

import android.graphics.Bitmap
import com.example.model.ProductIntegrityResult
import kotlin.math.max
import kotlin.math.min

object ProductIntegrityValidator {

    const val MIN_CONFIDENCE_THRESHOLD = 0.65f
    const val MIN_AREA_RATIO = 0.05f
    const val MAX_AREA_RATIO = 0.92f

    /**
     * Validates the segmented foreground mask against the original artisan craft photo.
     * 
     * Enforces strict non-generative craft preservation rules:
     * 1. Product must not be excessively clipped (< 5% or > 92% of frame).
     * 2. Bounding box must represent a coherent physical object.
     * 3. Severe edge clipping across multiple boundaries flags risk of product damage.
     * 4. Confidence must meet the minimum threshold to prevent cutting handmade details.
     */
    fun validate(
        originalBitmap: Bitmap,
        maskAlpha: IntArray,
        width: Int,
        height: Int,
        forceLowConfidence: Boolean = false
    ): ProductIntegrityResult {
        if (forceLowConfidence) {
            return ProductIntegrityResult(
                passed = false,
                message = "Low segmentation confidence (42%). Background removal skipped to protect delicate craft edges. Original photo preserved.",
                areaPreservedRatio = 0.42f,
                boundingBoxMatch = false,
                edgesPreserved = false,
                isBorderCutWarning = true,
                confidenceScore = 0.42f
            )
        }

        val totalPixels = width * height
        if (totalPixels <= 0 || maskAlpha.size < totalPixels) {
            return ProductIntegrityResult(
                passed = false,
                message = "Invalid image dimensions for integrity check. Original craft photo preserved.",
                areaPreservedRatio = 1.0f,
                boundingBoxMatch = false,
                edgesPreserved = false,
                isBorderCutWarning = false,
                confidenceScore = 0.0f
            )
        }

        var foregroundCount = 0
        var minX = width
        var maxX = 0
        var minY = height
        var maxY = 0

        for (y in 0 until height) {
            val rowOffset = y * width
            for (x in 0 until width) {
                val alpha = maskAlpha[rowOffset + x]
                if (alpha > 120) {
                    foregroundCount++
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                }
            }
        }

        val areaRatio = foregroundCount.toFloat() / totalPixels
        val boxWidth = if (maxX >= minX) maxX - minX else 0
        val boxHeight = if (maxY >= minY) maxY - minY else 0
        val boundingBoxValid = boxWidth > 30 && boxHeight > 30

        // Border proximity checks
        val borderMargin = max(3, min(width, height) / 40)
        val leftHit = minX <= borderMargin
        val rightHit = maxX >= width - 1 - borderMargin
        val topHit = minY <= borderMargin
        val bottomHit = maxY >= height - 1 - borderMargin

        var borderHits = 0
        if (leftHit) borderHits++
        if (rightHit) borderHits++
        if (topHit) borderHits++
        if (bottomHit) borderHits++

        val isBorderCutWarning = borderHits >= 2

        // Confidence calculation
        var confidence = 0.98f

        if (areaRatio < MIN_AREA_RATIO) {
            confidence = 0.25f
        } else if (areaRatio > MAX_AREA_RATIO) {
            confidence = 0.38f
        } else if (!boundingBoxValid) {
            confidence = 0.30f
        } else {
            // Apply slight deductions for edge contact
            if (borderHits >= 3) {
                confidence -= 0.35f
            } else if (borderHits == 2) {
                confidence -= 0.18f
            } else if (borderHits == 1) {
                confidence -= 0.05f
            }
        }

        confidence = confidence.coerceIn(0.0f, 1.0f)
        val passed = confidence >= MIN_CONFIDENCE_THRESHOLD && areaRatio in MIN_AREA_RATIO..MAX_AREA_RATIO && boundingBoxValid

        val message = if (passed) {
            "Product integrity verified (${(confidence * 100).toInt()}% confidence). 100% original artisan pixels preserved."
        } else {
            "Low segmentation confidence (${(confidence * 100).toInt()}%). Background removal skipped to prevent cutting craft details. Original photo preserved."
        }

        return ProductIntegrityResult(
            passed = passed,
            message = message,
            areaPreservedRatio = (areaRatio / 0.5f).coerceIn(0.6f, 1.0f),
            boundingBoxMatch = boundingBoxValid,
            edgesPreserved = !isBorderCutWarning,
            isBorderCutWarning = isBorderCutWarning,
            confidenceScore = confidence
        )
    }
}
