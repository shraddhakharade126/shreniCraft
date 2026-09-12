package com.example.service

import android.util.Log
import com.example.BuildConfig
import com.example.model.DemoCraft
import com.example.model.ProductAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class RealAIProductAnalysisService(
    private val fallbackService: DemoAIProductAnalysisService = DemoAIProductAnalysisService()
) : AIProductAnalysisService {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .build()

    override suspend fun analyzeProduct(
        imageFile: File,
        voiceDescription: String?,
        detectedCraftHint: DemoCraft?
    ): ProductAnalysis = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && !apiKey.contains("MY_")) {
            try {
                val promptText = buildString {
                    append("You are Shreni AI, an expert Indian artisan business and handicraft cataloging assistant. ")
                    append("Analyze this craft item and generate a structured JSON listing for the marketplace. ")
                    if (!voiceDescription.isNullOrBlank()) {
                        append("Artisan's notes: \"").append(voiceDescription).append("\". ")
                    }
                    if (detectedCraftHint != null) {
                        append("Craft hint: ").append(detectedCraftHint.title).append(" (").append(detectedCraftHint.category).append("). ")
                    }
                    append("Return a valid JSON object ONLY with no markdown or formatting wrapping. Required JSON format:\n")
                    append("{\n")
                    append("  \"productName\": \"Handcrafted Festive Terracotta Clay Diya Set\",\n")
                    append("  \"category\": \"Festive Decor & Home Accents\",\n")
                    append("  \"craftType\": \"Terracotta Pottery\",\n")
                    append("  \"material\": \"Natural River Clay\",\n")
                    append("  \"colors\": [\"Earthy Brown\", \"Terracotta Red\", \"Gold\"],\n")
                    append("  \"description\": \"A culturally rich storytelling description of the handmade craft...\",\n")
                    append("  \"tags\": [\"Handmade\", \"Terracotta\", \"Diwali\", \"Eco Friendly\"],\n")
                    append("  \"suggestedPriceMin\": 450.0,\n")
                    append("  \"suggestedPriceMax\": 750.0\n")
                    append("}")
                }

                val requestJson = JSONObject().apply {
                    val contentsArray = JSONArray()
                    val contentObj = JSONObject()
                    val partsArray = JSONArray()
                    partsArray.put(JSONObject().put("text", promptText))
                    contentObj.put("parts", partsArray)
                    contentsArray.put(contentObj)
                    put("contents", contentsArray)
                }

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=$apiKey")
                    .post(requestJson.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    if (!bodyString.isNullOrBlank()) {
                        val root = JSONObject(bodyString)
                        val candidates = root.optJSONArray("candidates")
                        if (candidates != null && candidates.length() > 0) {
                            val candidate = candidates.getJSONObject(0)
                            val content = candidate.getJSONObject("content")
                            val parts = content.getJSONArray("parts")
                            val responseText = parts.getJSONObject(0).getString("text")

                            val parsed = parseGeminiJson(responseText)
                            if (parsed != null) {
                                return@withContext parsed
                            }
                        }
                    }
                } else {
                    Log.w("RealAIProductAnalysis", "Gemini API failed with code ${response.code}: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("RealAIProductAnalysis", "Gemini API call failed, falling back to local domain model", e)
            }
        }

        // Offline or fallback
        fallbackService.analyzeProduct(imageFile, voiceDescription, detectedCraftHint)
    }

    suspend fun chatWithShreniAI(prompt: String, currentLanguage: String = "en"): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNotBlank() && !apiKey.contains("MY_")) {
            try {
                val systemPrompt = "You are Shreni AI, a compassionate and expert business mentor for Indian rural and traditional artisans. " +
                        "Language preference: $currentLanguage. " +
                        "Provide practical, culturally respectful advice on product descriptions, fair pricing in ₹ INR, marketplace tips, and customer communication. Keep answers concise, warm, and actionable."

                val requestJson = JSONObject().apply {
                    val contentsArray = JSONArray()
                    val contentObj = JSONObject()
                    val partsArray = JSONArray()
                    partsArray.put(JSONObject().put("text", "$systemPrompt\n\nUser Question: $prompt"))
                    contentObj.put("parts", partsArray)
                    contentsArray.put(contentObj)
                    put("contents", contentsArray)
                }

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key=$apiKey")
                    .post(requestJson.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    if (!bodyString.isNullOrBlank()) {
                        val root = JSONObject(bodyString)
                        val candidates = root.optJSONArray("candidates")
                        if (candidates != null && candidates.length() > 0) {
                            val candidate = candidates.getJSONObject(0)
                            val content = candidate.getJSONObject("content")
                            val parts = content.getJSONArray("parts")
                            return@withContext parts.getJSONObject(0).getString("text").trim()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("RealAIProductAnalysis", "Gemini chat failed", e)
            }
        }
        // Fallback simulated artisan chat answers
        getSimulatedChatReply(prompt)
    }

    private fun parseGeminiJson(rawText: String): ProductAnalysis? {
        return try {
            val jsonStart = rawText.indexOf('{')
            val jsonEnd = rawText.lastIndexOf('}')
            if (jsonStart != -1 && jsonEnd > jsonStart) {
                val jsonSubstring = rawText.substring(jsonStart, jsonEnd + 1)
                val obj = JSONObject(jsonSubstring)

                val colors = mutableListOf<String>()
                obj.optJSONArray("colors")?.let { arr ->
                    for (i in 0 until arr.length()) colors.add(arr.getString(i))
                }

                val tags = mutableListOf<String>()
                obj.optJSONArray("tags")?.let { arr ->
                    for (i in 0 until arr.length()) tags.add(arr.getString(i))
                }

                ProductAnalysis(
                    productName = obj.optString("productName", "Handcrafted Traditional Craft"),
                    category = obj.optString("category", "Traditional Handicrafts"),
                    craftType = obj.optString("craftType", "Authentic Artisan Craft"),
                    material = obj.optString("material", "Natural Eco-Friendly Materials"),
                    colors = if (colors.isNotEmpty()) colors else listOf("Natural", "Terracotta"),
                    description = obj.optString("description", "Handmade with traditional heritage techniques."),
                    tags = if (tags.isNotEmpty()) tags else listOf("Handmade", "Indian Heritage"),
                    suggestedPriceMin = obj.optDouble("suggestedPriceMin", 450.0),
                    suggestedPriceMax = obj.optDouble("suggestedPriceMax", 750.0),
                    confidence = 0.98f
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun getSimulatedChatReply(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("price") ->
                "For a handcrafted authentic piece, I suggest ₹2,499 with a fair range of ₹2,200–₹2,800. Buyers appreciate verified artisan quality, so avoid undervaluing your hours of work."
            lower.contains("translate") ->
                "I can translate your listing into English, हिंदी and मराठी simultaneously so buyers across India can understand your craft story in their native language."
            lower.contains("sell more") ->
                "Three key strategies:\n1. Provide 2-3 clear photos showing texture and scale.\n2. Respond promptly to customer inquiries.\n3. Offer festive bundles before major festivals."
            lower.contains("title") ->
                "A powerful title formula: [Artisan Craft Name] + [Material] + [Region/Motif]. Example: \"Handcrafted Paithani Silk Saree with Gold Zari Peacock Border\"."
            lower.contains("customer") || lower.contains("reply") ->
                "Here is a warm reply:\n\"Namaste! Thank you for appreciating our handmade craft. Each piece is crafted by hand in our family workshop. We will be delighted to fulfill your order.\""
            else ->
                "Namaste! I am Shreni AI. I can help you write compelling product descriptions, calculate fair pricing in ₹ INR, and answer buyer questions."
        }
    }
}

