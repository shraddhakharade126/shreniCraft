package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.example.model.DemoCraft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object DemoAssetGenerator {

    /**
     * Generates a sample craft bitmap with realistic artisan textures and background
     * for instant testing in the emulator or when capturing demo crafts.
     */
    fun getOrCreateSampleCraftFileSync(context: Context, craft: DemoCraft): File {
        val craftsDir = File(context.filesDir, "sample_crafts").apply { mkdirs() }
        val targetFile = File(craftsDir, "${craft.id}.png")

        if (targetFile.exists() && targetFile.length() > 0) {
            return targetFile
        }

        val width = 800
        val height = 800
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Draw realistic rustic workshop background (wooden workbench or burlap mat)
        val bgPaint = Paint().apply {
            color = Color.rgb(230, 222, 211) // Warm workshop surface
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Wood texture / craft workshop lines in background
        val texturePaint = Paint().apply {
            color = Color.rgb(215, 206, 194)
            strokeWidth = 3f
        }
        for (y in 50..height step 80) {
            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), texturePaint)
        }

        // Now draw the actual craft according to type
        when (craft.category) {
            "Pottery" -> drawTerracottaPot(canvas, width, height)
            "Metal Craft" -> drawBrassDiya(canvas, width, height)
            "Painting" -> drawMadhubaniArt(canvas, width, height)
            else -> drawEmbroideredBag(canvas, width, height)
        }

        FileOutputStream(targetFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
        }
        bitmap.recycle()

        return targetFile
    }

    suspend fun getOrCreateSampleCraftFile(context: Context, craft: DemoCraft): File = withContext(Dispatchers.IO) {
        getOrCreateSampleCraftFileSync(context, craft)
    }

    private fun drawTerracottaPot(canvas: Canvas, w: Int, h: Int) {
        val cx = w / 2f
        val cy = h / 2f + 30f

        // Soft drop shadow
        val shadowPaint = Paint().apply {
            color = Color.argb(60, 50, 40, 30)
            isAntiAlias = true
        }
        canvas.drawOval(RectF(cx - 190f, cy + 220f, cx + 190f, cy + 280f), shadowPaint)

        // Terracotta pot body
        val potPaint = Paint().apply {
            color = Color.rgb(198, 87, 44) // Authentic terracotta clay
            isAntiAlias = true
        }
        val path = Path().apply {
            moveTo(cx - 100f, cy - 170f) // Rim left
            lineTo(cx + 100f, cy - 170f) // Rim right
            cubicTo(cx + 80f, cy - 120f, cx + 220f, cy - 30f, cx + 200f, cy + 120f)
            cubicTo(cx + 180f, cy + 220f, cx + 100f, cy + 240f, cx, cy + 240f)
            cubicTo(cx - 100f, cy + 240f, cx - 180f, cy + 220f, cx - 200f, cy + 120f)
            cubicTo(cx - 220f, cy - 30f, cx - 80f, cy - 120f, cx - 100f, cy - 170f)
            close()
        }
        canvas.drawPath(path, potPaint)

        // Handcrafted neck rim
        val rimPaint = Paint().apply {
            color = Color.rgb(160, 60, 25)
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(cx - 115f, cy - 190f, cx + 115f, cy - 150f), 15f, 15f, rimPaint)

        // Terracotta handles (critical product feature to preserve!)
        val handlePaint = Paint().apply {
            color = Color.rgb(160, 60, 25)
            style = Paint.Style.STROKE
            strokeWidth = 24f
            isAntiAlias = true
        }
        val leftHandle = Path().apply {
            moveTo(cx - 140f, cy - 70f)
            cubicTo(cx - 240f, cy - 60f, cx - 240f, cy + 40f, cx - 180f, cy + 50f)
        }
        val rightHandle = Path().apply {
            moveTo(cx + 140f, cy - 70f)
            cubicTo(cx + 240f, cy - 60f, cx + 240f, cy + 40f, cx + 180f, cy + 50f)
        }
        canvas.drawPath(leftHandle, handlePaint)
        canvas.drawPath(rightHandle, handlePaint)

        // Painted white cultural folk motifs on pot
        val motifPaint = Paint().apply {
            color = Color.rgb(255, 248, 240)
            style = Paint.Style.STROKE
            strokeWidth = 8f
            isAntiAlias = true
        }
        canvas.drawCircle(cx, cy + 40f, 60f, motifPaint)
        motifPaint.style = Paint.Style.FILL
        for (i in 0 until 8) {
            val angle = Math.toRadians((i * 45).toDouble())
            val px = cx + (60f * Math.cos(angle)).toFloat()
            val py = cy + 40f + (60f * Math.sin(angle)).toFloat()
            canvas.drawCircle(px, py, 10f, motifPaint)
        }
    }

    private fun drawBrassDiya(canvas: Canvas, w: Int, h: Int) {
        val cx = w / 2f
        val cy = h / 2f + 40f

        val brassPaint = Paint().apply {
            color = Color.rgb(212, 175, 55) // Rich polished brass gold
            isAntiAlias = true
        }

        // Base pedestal
        canvas.drawRoundRect(RectF(cx - 150f, cy + 180f, cx + 150f, cy + 240f), 20f, 20f, brassPaint)
        // Central column
        canvas.drawRoundRect(RectF(cx - 30f, cy - 40f, cx + 30f, cy + 180f), 15f, 15f, brassPaint)
        // Diya bowl
        canvas.drawArc(RectF(cx - 180f, cy - 100f, cx + 180f, cy + 50f), 0f, 180f, true, brassPaint)

        // Peacock figurine top
        val peacockPaint = Paint().apply {
            color = Color.rgb(180, 140, 30)
            isAntiAlias = true
        }
        canvas.drawCircle(cx, cy - 140f, 35f, peacockPaint)
        canvas.drawOval(RectF(cx - 25f, cy - 120f, cx + 25f, cy - 40f), peacockPaint)

        // Golden warm flame
        val flamePaint = Paint().apply {
            color = Color.rgb(255, 160, 0)
            isAntiAlias = true
        }
        canvas.drawCircle(cx - 140f, cy - 110f, 18f, flamePaint)
        canvas.drawCircle(cx + 140f, cy - 110f, 18f, flamePaint)
    }

    private fun drawMadhubaniArt(canvas: Canvas, w: Int, h: Int) {
        val cx = w / 2f
        val cy = h / 2f

        // Handmade parchment canvas
        val paperPaint = Paint().apply {
            color = Color.rgb(249, 237, 214)
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(cx - 240f, cy - 260f, cx + 240f, cy + 260f), 12f, 12f, paperPaint)

        // Ornate folk double border
        val borderPaint = Paint().apply {
            color = Color.rgb(20, 20, 20)
            style = Paint.Style.STROKE
            strokeWidth = 6f
            isAntiAlias = true
        }
        canvas.drawRect(cx - 220f, cy - 240f, cx + 220f, cy + 240f, borderPaint)
        canvas.drawRect(cx - 200f, cy - 220f, cx + 200f, cy + 220f, borderPaint)

        // Vibrant Peacock figure
        val bluePaint = Paint().apply {
            color = Color.rgb(26, 82, 118)
            isAntiAlias = true
        }
        canvas.drawCircle(cx - 30f, cy - 50f, 50f, bluePaint)
        canvas.drawOval(RectF(cx - 60f, cy - 30f, cx + 60f, cy + 120f), bluePaint)

        // Colorful plumage feathers
        val featherPaint = Paint().apply {
            color = Color.rgb(211, 84, 0)
            isAntiAlias = true
        }
        for (i in 0 until 6) {
            val angle = Math.toRadians((i * 30 + 190).toDouble())
            val fx = cx + (130f * Math.cos(angle)).toFloat()
            val fy = cy + 40f + (130f * Math.sin(angle)).toFloat()
            canvas.drawCircle(fx, fy, 22f, featherPaint)
        }
    }

    private fun drawEmbroideredBag(canvas: Canvas, w: Int, h: Int) {
        val cx = w / 2f
        val cy = h / 2f + 20f

        // Clutch body
        val bagPaint = Paint().apply {
            color = Color.rgb(136, 14, 79) // Deep maroon cloth
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(cx - 210f, cy - 130f, cx + 210f, cy + 150f), 24f, 24f, bagPaint)

        // Envelope flap
        val flapPaint = Paint().apply {
            color = Color.rgb(186, 24, 89)
            isAntiAlias = true
        }
        val flap = Path().apply {
            moveTo(cx - 210f, cy - 130f)
            lineTo(cx + 210f, cy - 130f)
            lineTo(cx, cy + 20f)
            close()
        }
        canvas.drawPath(flap, flapPaint)

        // Real mirrors (Abhla embroidery)
        val mirrorPaint = Paint().apply {
            color = Color.rgb(240, 245, 250)
            isAntiAlias = true
        }
        val mirrorBorder = Paint().apply {
            color = Color.rgb(255, 179, 0)
            style = Paint.Style.STROKE
            strokeWidth = 6f
            isAntiAlias = true
        }
        val mirrorXs = floatArrayOf(cx - 120f, cx - 60f, cx, cx + 60f, cx + 120f)
        for (x in mirrorXs) {
            canvas.drawCircle(x, cy + 90f, 18f, mirrorPaint)
            canvas.drawCircle(x, cy + 90f, 18f, mirrorBorder)
        }
    }
}
