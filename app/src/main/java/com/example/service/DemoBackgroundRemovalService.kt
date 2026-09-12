package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.example.model.BackgroundRemovalResult
import com.example.model.ProductIntegrityResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min
import kotlin.math.sqrt

class DemoBackgroundRemovalService(private val context: Context) : BackgroundRemovalService {

    var forceLowConfidence: Boolean = false

    override suspend fun removeBackground(
        originalFile: File,
        preferTransparent: Boolean,
        onProgressUpdate: ((stepIndex: Int, stepName: String) -> Unit)?
    ): BackgroundRemovalResult = withContext(Dispatchers.Default) {
        onProgressUpdate?.invoke(0, "Analyzing original craft photo")
        delay(200)

        onProgressUpdate?.invoke(1, "Performing craft segmentation")
        delay(250)

        val originalBitmap = ImageUtils.decodeSampledBitmap(originalFile)
        val width = originalBitmap.width
        val height = originalBitmap.height

        // If forceLowConfidence is enabled, validate and fail early to protect product
        if (forceLowConfidence) {
            onProgressUpdate?.invoke(2, "Validating mask with ProductIntegrityValidator")
            delay(200)

            val lowConfidenceResult = ProductIntegrityValidator.validate(
                originalBitmap = originalBitmap,
                maskAlpha = IntArray(0),
                width = width,
                height = height,
                forceLowConfidence = true
            )
            originalBitmap.recycle()

            return@withContext BackgroundRemovalResult(
                success = false,
                originalUri = originalFile.absolutePath,
                processedUri = originalFile.absolutePath,
                transparentUri = null,
                whiteBackgroundUri = null,
                confidenceScore = lowConfidenceResult.confidenceScore,
                integrityResult = lowConfidenceResult,
                isFallbackToOriginal = true,
                errorMessage = lowConfidenceResult.message
            )
        }

        // Normal Demo flow: Generate local non-generative segmentation mask
        val maskAlpha = generateLocalSegmentationMask(originalBitmap)

        onProgressUpdate?.invoke(2, "Validating mask with ProductIntegrityValidator")
        delay(200)

        val integrityResult = ProductIntegrityValidator.validate(
            originalBitmap = originalBitmap,
            maskAlpha = maskAlpha,
            width = width,
            height = height
        )

        onProgressUpdate?.invoke(3, "Preserving 100% of authentic foreground craft pixels")
        delay(200)

        // Strict non-generative pixel transfer
        val originalPixels = IntArray(width * height)
        originalBitmap.getPixels(originalPixels, 0, width, 0, 0, width, height)

        val transparentPixels = IntArray(width * height)
        val whitePixels = IntArray(width * height)

        for (i in 0 until (width * height)) {
            val origPixel = originalPixels[i]
            val alpha = maskAlpha[i]
            val r = Color.red(origPixel)
            val g = Color.green(origPixel)
            val b = Color.blue(origPixel)

            transparentPixels[i] = Color.argb(alpha, r, g, b)

            val factor = alpha / 255.0f
            val wr = (r * factor + 252 * (1.0f - factor)).toInt().coerceIn(0, 255)
            val wg = (g * factor + 252 * (1.0f - factor)).toInt().coerceIn(0, 255)
            val wb = (b * factor + 252 * (1.0f - factor)).toInt().coerceIn(0, 255)
            whitePixels[i] = Color.rgb(wr, wg, wb)
        }

        onProgressUpdate?.invoke(4, "Creating studio transparent & white outputs")
        delay(150)

        val transparentBitmap = Bitmap.createBitmap(transparentPixels, width, height, Bitmap.Config.ARGB_8888)
        val whiteBitmap = Bitmap.createBitmap(whitePixels, width, height, Bitmap.Config.ARGB_8888)

        val outputDir = File(context.cacheDir, "processed_crafts").apply { mkdirs() }
        val timestamp = System.currentTimeMillis()
        val transparentFile = File(outputDir, "shreni_demo_trans_$timestamp.png")
        val whiteFile = File(outputDir, "shreni_demo_white_$timestamp.jpg")

        withContext(Dispatchers.IO) {
            FileOutputStream(transparentFile).use { out ->
                transparentBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            FileOutputStream(whiteFile).use { out ->
                whiteBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
            }
        }

        originalBitmap.recycle()
        transparentBitmap.recycle()
        whiteBitmap.recycle()

        BackgroundRemovalResult(
            success = true,
            originalUri = originalFile.absolutePath,
            transparentUri = transparentFile.absolutePath,
            whiteBackgroundUri = whiteFile.absolutePath,
            processedUri = if (preferTransparent) transparentFile.absolutePath else whiteFile.absolutePath,
            confidenceScore = integrityResult.confidenceScore,
            integrityResult = integrityResult,
            isFallbackToOriginal = false
        )
    }

    private fun generateLocalSegmentationMask(bitmap: Bitmap): IntArray {
        val w = bitmap.width
        val h = bitmap.height
        val alphas = IntArray(w * h)

        val cornerColors = intArrayOf(
            bitmap.getPixel(0, 0),
            bitmap.getPixel(w - 1, 0),
            bitmap.getPixel(0, h - 1),
            bitmap.getPixel(w - 1, h - 1),
            bitmap.getPixel(w / 2, 0),
            bitmap.getPixel(w / 2, h - 1),
            bitmap.getPixel(0, h / 2),
            bitmap.getPixel(w - 1, h / 2)
        )

        var avgR = 0; var avgG = 0; var avgB = 0
        for (c in cornerColors) {
            avgR += Color.red(c)
            avgG += Color.green(c)
            avgB += Color.blue(c)
        }
        avgR /= cornerColors.size
        avgG /= cornerColors.size
        avgB /= cornerColors.size

        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        val centerX = w / 2
        val centerY = h / 2
        val maxDist = sqrt((centerX * centerX + centerY * centerY).toDouble())
        val baseThreshold = 42.0

        for (y in 0 until h) {
            val rowOffset = y * w
            for (x in 0 until w) {
                val idx = rowOffset + x
                val p = pixels[idx]
                val pr = Color.red(p)
                val pg = Color.green(p)
                val pb = Color.blue(p)

                val colorDist = sqrt(
                    ((pr - avgR) * (pr - avgR) +
                     (pg - avgG) * (pg - avgG) +
                     (pb - avgB) * (pb - avgB)).toDouble()
                )

                val dx = x - centerX
                val dy = y - centerY
                val distFromCenter = sqrt((dx * dx + dy * dy).toDouble())
                val centerFactor = (1.0 - (distFromCenter / maxDist)).coerceIn(0.1, 1.0)

                val isForeground = colorDist > (baseThreshold * (1.2 - (centerFactor * 0.45))) || (distFromCenter < (min(w, h) * 0.28))
                alphas[idx] = if (isForeground) 255 else 0
            }
        }

        return alphas
    }
}
