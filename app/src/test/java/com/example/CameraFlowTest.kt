package com.example

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.model.DemoCraft
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ShreniScanViewModel
import com.example.viewmodel.ShreniScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CameraFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTapCaptureProduct_opensCamera() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = ShreniScanViewModel(app)

        composeTestRule.setContent {
            MyApplicationTheme {
                ShreniApp(viewModel = viewModel)
            }
        }

        // Verify initial screen is DASHBOARD
        assertEquals(ShreniScreen.DASHBOARD, viewModel.currentScreen.value)

        // Requirement 1: Tap Capture Product
        composeTestRule.onNodeWithTag("capture_product_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("capture_product_button").performClick()

        // Requirement 2: Open camera preview / camera screen
        assertEquals(ShreniScreen.CAMERA, viewModel.currentScreen.value)
    }

    @Test
    fun testCameraScreen_showsFramingGuideAndDemoButton() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = ShreniScanViewModel(app)
        viewModel.openCamera()

        composeTestRule.setContent {
            MyApplicationTheme {
                ShreniApp(viewModel = viewModel)
            }
        }

        // Requirement 3: Show framing guide
        composeTestRule.onNodeWithTag("camera_framing_guide_box").assertIsDisplayed()
        composeTestRule.onNodeWithTag("framing_guide_instruction").assertIsDisplayed()

        // Requirement: Demo Product button only for AI Studio emulator
        composeTestRule.onNodeWithTag("camera_demo_capture_button").assertIsDisplayed()

        // Shutter button is also available
        composeTestRule.onNodeWithTag("camera_shutter_button").assertIsDisplayed()
    }

    @Test
    fun testCapturePhoto_savesLocallyAndPreviews() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = ShreniScanViewModel(app)
        viewModel.openCamera()

        composeTestRule.setContent {
            MyApplicationTheme {
                ShreniApp(viewModel = viewModel)
            }
        }

        // Requirement 4: Capture photo via Demo button (reliable across all environments)
        composeTestRule.onNodeWithTag("camera_demo_capture_button").performClick()

        // Requirement 5: Preview captured photo
        assertEquals(ShreniScreen.ORIGINAL_PREVIEW, viewModel.currentScreen.value)
        val productImage = viewModel.productImage.value
        assertNotNull(productImage)
        assertNotNull(productImage?.originalUri)

        // Requirement 8: Original photo is saved locally
        val originalFile = File(productImage!!.originalUri)
        assertTrue("Original photo file must exist locally", originalFile.exists())
        assertTrue("Original photo file must have non-zero size", originalFile.length() > 0)
        assertTrue("File should be in app storage", originalFile.absolutePath.contains("craft_originals"))
    }

    @Test
    fun testRetakeAndAcceptPhotoFlow() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = ShreniScanViewModel(app)

        composeTestRule.setContent {
            MyApplicationTheme {
                ShreniApp(viewModel = viewModel)
            }
        }

        // Start by capturing a photo
        viewModel.captureDemoProduct(DemoCraft.SAMPLES[0])
        composeTestRule.waitForIdle()

        assertEquals(ShreniScreen.ORIGINAL_PREVIEW, viewModel.currentScreen.value)
        val firstPhotoPath = viewModel.productImage.value?.originalUri
        assertNotNull(firstPhotoPath)

        // Requirement 6: Retake works
        composeTestRule.onNodeWithTag("retake_photo_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("retake_photo_button").performClick()
        composeTestRule.waitForIdle()

        // Verify retake navigates back to camera
        assertEquals(ShreniScreen.CAMERA, viewModel.currentScreen.value)

        // Requirement 9: Do not overwrite the original - check that the first photo file still exists on disk
        val firstFile = File(firstPhotoPath!!)
        assertTrue("First photo file must still exist after retake", firstFile.exists())

        // Now capture a second photo
        viewModel.captureDemoProduct(DemoCraft.SAMPLES[1])
        composeTestRule.waitForIdle()

        assertEquals(ShreniScreen.ORIGINAL_PREVIEW, viewModel.currentScreen.value)
        val secondPhotoPath = viewModel.productImage.value?.originalUri
        assertNotNull(secondPhotoPath)
        assertNotEquals("Second photo must have a distinct path", firstPhotoPath, secondPhotoPath)

        val secondFile = File(secondPhotoPath!!)
        assertTrue("Second photo file exists", secondFile.exists())
        assertTrue("First photo file was not overwritten", firstFile.exists())

        // Requirement 7: Accept photo works
        composeTestRule.onNodeWithTag("accept_photo_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("accept_photo_button").performClick()
        composeTestRule.waitForIdle()

        // Verify accept transitions to the next step
        assertEquals(ShreniScreen.IMAGE_ACTIONS, viewModel.currentScreen.value)
    }
}
