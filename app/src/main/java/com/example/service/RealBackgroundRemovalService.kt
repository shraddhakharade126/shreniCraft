package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import com.example.BuildConfig
import com.example.model.BackgroundRemovalResult
import com.example.model.ProductIntegrityResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class RealBackgroundRemovalService(private val context: Context) : BackgroundRemovalService {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    override suspend fun removeBackground(
        originalFile: File,
        preferTransparent: Boolean,
        onProgressUpdate: ((stepIndex: Int, stepName: String) -> Unit)?
    ): BackgroundRemovalResult = withContext(Dispatchers.Default) {
        try {
            // STEP 1: Decode and inspect original image
            onProgressUpdate?.invoke(0, "Analyzing original craft photo")
            delay(150)
            val originalBitmap = ImageUtils.decodeSampledBitmap(originalFile)
            val width = originalBitmap.width
            val height = originalBitmap.height

            // STEP 2: Segmentation (remove.bg API or fallback local segmentation)
            onProgressUpdate?.invoke(1, "Performing craft segmentation")
            var maskAlpha: IntArray? = null
            val apiKey = BuildConfig.REMOVE_BG_API_KEY

            if (apiKey.isNotBlank() && !apiKey.contains("MY_")) {
                try {
                    val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("size", "auto")
                        .addFormDataPart("format", "png")
                        .addFormDataPart("channels", "rgba")
                        .addFormDataPart(
                            "image_file",
                            originalFile.name,
                            originalFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        )
                        .build()

                    val request = Request.Builder()
                        .url("https://api.remove.bg/v1.0/removebg")
                        .addHeader("X-Api-Key", apiKey)
                        .post(requestBody)
                        .build()

                    val remoteAlpha = withContext(Dispatchers.IO) {
                        val response = httpClient.newCall(request).execute()
                        if (response.isSuccessful) {
                            val bytes = response.body?.bytes()
                            if (bytes != null && bytes.isNotEmpty()) {
                                val segmentedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                if (segmentedBitmap != null) {
                                    val scaledMask = if (segmentedBitmap.width != width || segmentedBitmap.height != height) {
                                        Bitmap.createScaledBitmap(segmentedBitmap, width, height, true)
                                    } else {
                                        segmentedBitmap
                                    }

                                    val pixels = IntArray(width * height)
                                    scaledMask.getPixels(pixels, 0, width, 0, 0, width, height)
                                    val alphas = IntArray(width * height)
                                    for (i in pixels.indices) {
                                        alphas[i] = Color.alpha(pixels[i])
                                    }
                                    if (scaledMask !== segmentedBitmap) {
                                        scaledMask.recycle()
                                    }
                                    segmentedBitmap.recycle()
                                    alphas
                                } else null
                            } else null
                        } else {
                            Log.w("RealBackgroundRemoval", "remove.bg returned ${response.code}: ${response.message}")
                            null
                        }
                    }
                    maskAlpha = remoteAlpha
                } catch (e: Exception) {
                    Log.e("RealBackgroundRemoval", "remove.bg API call failed, falling back to local segmentation engine", e)
                }
            }

            // If remote segmentation did not execute or was unavailable, generate local non-generative mask
            if (maskAlpha == null) {
                maskAlpha = generateLocalSegmentationMask(originalBitmap)
            }

            // STEP 3: Mask Validation with ProductIntegrityValidator
            onProgressUpdate?.invoke(2, "Validating mask with ProductIntegrityValidator")
            delay(150)
            val integrityResult = ProductIntegrityValidator.validate(
                originalBitmap = originalBitmap,
                maskAlpha = maskAlpha,
                width = width,
                height = height
            )

            // CRITICAL REQUIREMENT:
            // If segmentation confidence is low, do not process the image.
            // Return a failure result and let the user use the original image.
            // The original image must always remain available.
            if (!integrityResult.passed || integrityResult.confidenceScore < ProductIntegrityValidator.MIN_CONFIDENCE_THRESHOLD) {
                originalBitmap.recycle()
                return@withContext BackgroundRemovalResult(
                    success = false,
                    originalUri = originalFile.absolutePath,
                    processedUri = originalFile.absolutePath,
                    transparentUri = null,
                    whiteBackgroundUri = null,
                    confidenceScore = integrityResult.confidenceScore,
                    integrityResult = integrityResult,
                    isFallbackToOriginal = true,
                    errorMessage = integrityResult.message
                )
            }

            // STEP 4: Preserve original foreground & remove background
            // CRITICAL REQUIREMENT:
            // NEVER regenerate the artisan's product.
            // NEVER use generative AI to recreate the product.
            // The original foreground pixels must be preserved.
            onProgressUpdate?.invoke(3, "Preserving 100% of authentic foreground craft pixels")
            delay(150)

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

                // Transparent PNG: Exact original pixel values with alpha mask
                transparentPixels[i] = Color.argb(alpha, r, g, b)

                // White studio backdrop: Exact original pixels blended over studio white
                val factor = alpha / 255.0f
                val wr = (r * factor + 252 * (1.0f - factor)).toInt().coerceIn(0, 255)
                val wg = (g * factor + 252 * (1.0f - factor)).toInt().coerceIn(0, 255)
                val wb = (b * factor + 252 * (1.0f - factor)).toInt().coerceIn(0, 255)
                whitePixels[i] = Color.rgb(wr, wg, wb)
            }

            // STEP 5: Create and save transparent PNG and white background studio image
            onProgressUpdate?.invoke(4, "Creating studio transparent & white outputs")
            delay(150)

            val transparentBitmap = Bitmap.createBitmap(transparentPixels, width, height, Bitmap.Config.ARGB_8888)
            val whiteBitmap = Bitmap.createBitmap(whitePixels, width, height, Bitmap.Config.ARGB_8888)

            val outputDir = File(context.cacheDir, "processed_crafts").apply { mkdirs() }
            val timestamp = System.currentTimeMillis()
            val transparentFile = File(outputDir, "shreni_transparent_$timestamp.png")
            val whiteFile = File(outputDir, "shreni_white_$timestamp.jpg")

            withContext(Dispatchers.IO) {
                FileOutputStream(transparentFile).use { out ->
                    transparentBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                FileOutputStream(whiteFile).use { out ->
                    whiteBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }
            }

            // Cleanup memory
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
        } catch (e: Exception) {
            Log.e("RealBackgroundRemoval", "Background removal failed", e)
            BackgroundRemovalResult(
                success = false,
                originalUri = originalFile.absolutePath,
                processedUri = originalFile.absolutePath,
                transparentUri = null,
                whiteBackgroundUri = null,
                confidenceScore = 0.0f,
                integrityResult = ProductIntegrityResult(
                    passed = false,
                    message = "Could not safely remove background. Original craft photo preserved.",
                    confidenceScore = 0.0f
                ),
                isFallbackToOriginal = true,
                errorMessage = e.localizedMessage
            )
        }
    }

    /**
     * Local deterministic foreground mask generator.
     * ZERO GENERATIVE AI: Analyzes background clusters from periphery and centers protection
     * on the artisan craft.
     */
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
