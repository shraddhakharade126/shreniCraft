package com.example

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.model.EnhancementSettings
import com.example.service.DemoImageEnhancementService
import com.example.service.ImageEnhancementService
import com.example.service.ImageUtils
import com.example.service.RealImageEnhancementService
import com.example.ui.screens.EnhancementSettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ShreniScanViewModel
import com.example.viewmodel.ShreniScreen
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ImageEnhancementTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Helper to generate distinct test craft images of different craft material types.
     */
    private fun createTestCraftImage(type: CraftType, targetFile: File) {
        val width = 200
        val height = 200
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        when (type) {
            CraftType.TERRACOTTA_POTTERY -> {
                // Background: neutral studio
                canvas.drawColor(Color.rgb(240, 240, 240))
                // Terracotta vase: warm earthen clay tones
                paint.color = Color.rgb(184, 75, 41) // Terracotta red
                canvas.drawCircle(100f, 100f, 60f, paint)
                // Decorative carving groove
                paint.color = Color.rgb(120, 45, 20)
                paint.strokeWidth = 4f
                canvas.drawLine(60f, 100f, 140f, 100f, paint)
            }
            CraftType.SILK_TEXTILE -> {
                // Indigo textile with golden Zari embroidery thread
                canvas.drawColor(Color.rgb(20, 35, 80))
                paint.color = Color.rgb(220, 180, 50)
                paint.strokeWidth = 3f
                for (i in 20..180 step 20) {
                    canvas.drawLine(i.toFloat(), 20f, i.toFloat(), 180f, paint)
                    canvas.drawLine(20f, i.toFloat(), 180f, i.toFloat(), paint)
                }
            }
            CraftType.BRASS_DHOKRA_METAL -> {
                // Brass sculpture: warm metallic golden-yellow with specular highlight
                canvas.drawColor(Color.rgb(235, 235, 235))
                paint.color = Color.rgb(205, 155, 30) // Brass gold
                canvas.drawRect(50f, 50f, 150f, 150f, paint)
                paint.color = Color.rgb(255, 240, 180) // Specular highlight
                canvas.drawCircle(80f, 80f, 20f, paint)
            }
            CraftType.WOOD_CARVING -> {
                // Natural teak wood with deep brown relief grains
                canvas.drawColor(Color.rgb(139, 90, 43))
                paint.color = Color.rgb(80, 50, 20)
                for (y in 30..170 step 15) {
                    canvas.drawLine(30f, y.toFloat(), 170f, (y + 10).toFloat(), paint)
                }
            }
        }

        targetFile.parentFile?.mkdirs()
        FileOutputStream(targetFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }
    }

    enum class CraftType {
        TERRACOTTA_POTTERY,
        SILK_TEXTILE,
        BRASS_DHOKRA_METAL,
        WOOD_CARVING
    }

    @Test
    fun testOriginalImageRemainsUnchangedAcrossAllCraftTypes() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val realService = RealImageEnhancementService(app)
        val demoService = DemoImageEnhancementService(app)

        val craftTypes = listOf(
            CraftType.TERRACOTTA_POTTERY,
            CraftType.SILK_TEXTILE,
            CraftType.BRASS_DHOKRA_METAL,
            CraftType.WOOD_CARVING
        )

        for (craftType in craftTypes) {
            val originalFile = File(app.cacheDir, "test_orig_${craftType.name}.jpg")
            createTestCraftImage(craftType, originalFile)
            val originalBytesBefore = originalFile.readBytes()
            val originalLength = originalFile.length()

            val customSettings = EnhancementSettings(
                brightness = 15f,
                contrast = 1.15f,
                sharpness = 0.4f,
                colorCorrection = 1.2f,
                whiteBalance = 8f,
                noiseReduction = 0.2f
            )

            // Test Real Service
            val realResult = realService.enhanceImage(originalFile, customSettings)
            assertTrue("Real service should succeed for $craftType", realResult.success)
            assertNotNull("Enhanced file must exist", realResult.enhancedFile)
            assertTrue("Enhanced file must exist on disk", realResult.enhancedFile!!.exists())
            assertNotEquals("Enhanced file must have a different path", originalFile.absolutePath, realResult.enhancedFile!!.absolutePath)

            // CRITICAL TEST: Original file must remain byte-for-byte unchanged!
            val originalBytesAfterReal = originalFile.readBytes()
            assertEquals("Original file size must remain untouched", originalLength, originalFile.length())
            assertTrue(
                "Original file bytes must be strictly unchanged after Real enhancement for $craftType",
                originalBytesBefore.contentEquals(originalBytesAfterReal)
            )

            // Test Demo Service
            val demoResult = demoService.enhanceImage(originalFile, customSettings)
            assertTrue("Demo service should succeed for $craftType", demoResult.success)
            assertNotNull("Demo enhanced file must exist", demoResult.enhancedFile)
            assertNotEquals("Demo enhanced file path must differ from original", originalFile.absolutePath, demoResult.enhancedFile!!.absolutePath)

            val originalBytesAfterDemo = originalFile.readBytes()
            assertTrue(
                "Original file bytes must be strictly unchanged after Demo enhancement for $craftType",
                originalBytesBefore.contentEquals(originalBytesAfterDemo)
            )
        }
    }

    @Test
    fun testNonGenerativeEnhancementPreservesDimensionsAndGeometry() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val testFile = File(app.cacheDir, "geometry_test_craft.jpg")
        createTestCraftImage(CraftType.TERRACOTTA_POTTERY, testFile)

        val originalBitmap = ImageUtils.decodeSampledBitmap(testFile)
        val origW = originalBitmap.width
        val origH = originalBitmap.height

        val settings = EnhancementSettings(
            brightness = -10f,
            contrast = 1.25f,
            sharpness = 0.5f,
            colorCorrection = 1.1f,
            whiteBalance = -12f,
            noiseReduction = 0.3f
        )

        val enhancedBitmap = ImageUtils.enhanceProductImage(originalBitmap, settings)

        // Strict Non-Generative Guarantee: Dimensions must be 100% identical (no warping or geometric scaling)
        assertEquals("Width must remain identical", origW, enhancedBitmap.width)
        assertEquals("Height must remain identical", origH, enhancedBitmap.height)
    }

    @Test
    fun testReplaceableServiceArchitectureInViewModel() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = ShreniScanViewModel(app)

        // Verify default service is RealImageEnhancementService
        assertTrue(viewModel.imageEnhancementService is RealImageEnhancementService)

        // Test swapping with DemoImageEnhancementService
        val customDemoService = DemoImageEnhancementService(app)
        viewModel.imageEnhancementService = customDemoService
        assertEquals(customDemoService, viewModel.imageEnhancementService)

        // Test swapping with a custom mock ImageEnhancementService
        var mockInvoked = false
        val mockService = object : ImageEnhancementService {
            override suspend fun enhanceImage(
                originalFile: File,
                settings: EnhancementSettings,
                onProgress: ((stepIndex: Int, stepName: String) -> Unit)?
            ) = com.example.service.EnhancementResult(
                success = true,
                enhancedFile = originalFile,
                enhancedUri = originalFile.absolutePath,
                settings = settings,
                message = "Mock enhancement"
            ).also { mockInvoked = true }
        }

        viewModel.imageEnhancementService = mockService
        assertEquals(mockService, viewModel.imageEnhancementService)

        val testFile = File(app.cacheDir, "mock_test.jpg")
        createTestCraftImage(CraftType.SILK_TEXTILE, testFile)
        viewModel.imageEnhancementService.enhanceImage(testFile, EnhancementSettings.DEFAULT)
        assertTrue("Replaceable service must be callable", mockInvoked)
    }

    @Test
    fun testUIControls_Before_After_Reset_Apply_UseOriginal() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = ShreniScanViewModel(app)

        val testFile = File(app.cacheDir, "ui_test_craft.jpg")
        createTestCraftImage(CraftType.BRASS_DHOKRA_METAL, testFile)

        // Set an active product image
        viewModel.handleImageCaptured(testFile.absolutePath)

        composeTestRule.setContent {
            MyApplicationTheme {
                EnhancementSettingsScreen(viewModel = viewModel)
            }
        }

        // Verify required UI controls exist:
        // 1. Before
        composeTestRule.onNodeWithTag("enhancement_before_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("enhancement_before_button").performClick()

        // 2. After
        composeTestRule.onNodeWithTag("enhancement_after_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("enhancement_after_button").performClick()

        // 3. Sliders for allowed controls
        composeTestRule.onNodeWithTag("slider_brightness").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("slider_contrast").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("slider_sharpness").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("slider_saturation").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("slider_white_balance").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("slider_noise_reduction").performScrollTo().assertIsDisplayed()

        // 4. Reset
        viewModel.updateBrightness(20f)
        viewModel.updateContrast(1.2f)
        assertEquals(20f, viewModel.enhancementSettings.value.brightness)
        composeTestRule.onNodeWithTag("enhancement_reset_button").performScrollTo().performClick()
        assertEquals(EnhancementSettings.DEFAULT.brightness, viewModel.enhancementSettings.value.brightness)

        // 5. Use Original
        composeTestRule.onNodeWithTag("enhancement_use_original_button").performScrollTo().performClick()
        val originalUri = viewModel.productImage.value?.originalUri
        assertNotNull(originalUri)
        assertEquals(originalUri, viewModel.productImage.value?.userSelectedUri)
        assertFalse(viewModel.productImage.value?.enhanced ?: true)

        // 6. Apply
        composeTestRule.onNodeWithTag("enhancement_apply_button").performScrollTo().performClick()
    }
}
