package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ShreniBottomBar
import com.example.ui.screens.AddProductWizardScreen
import com.example.ui.screens.BeforeAfterScreen
import com.example.ui.screens.CameraScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EnhancementSettingsScreen
import com.example.ui.screens.ImageActionsScreen
import com.example.ui.screens.MarketplacePreviewScreen
import com.example.ui.screens.OriginalPreviewScreen
import com.example.ui.screens.PriceScreen
import com.example.ui.screens.ProcessingScreen
import com.example.ui.screens.ProductReviewScreen
import com.example.ui.screens.ScanGalleryScreen
import com.example.ui.screens.ShreniAIScreen
import com.example.ui.screens.ShreniBazaarScreen
import com.example.ui.screens.ShreniPayScreen
import com.example.ui.screens.ShreniScanHomeScreen
import com.example.ui.screens.ShreniSetuScreen
import com.example.ui.screens.ShreniVaniScreen
import com.example.ui.screens.SuccessScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ShreniScanViewModel
import com.example.viewmodel.ShreniScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          ShreniApp()
        }
      }
    }
  }
}

@Composable
fun ShreniApp(viewModel: ShreniScanViewModel = viewModel()) {
  val currentScreen by viewModel.currentScreen.collectAsState()

  BackHandler(enabled = currentScreen != ShreniScreen.DASHBOARD) {
    viewModel.navigateBack()
  }

  val showBottomBar = currentScreen in listOf(
    ShreniScreen.DASHBOARD,
    ShreniScreen.BAZAAR_CATALOGUE,
    ShreniScreen.SHRENI_AI,
    ShreniScreen.SHRENI_VANI,
    ShreniScreen.SHRENI_PAY,
    ShreniScreen.SHRENI_SETU
  )

  Scaffold(
    bottomBar = {
      if (showBottomBar) {
        ShreniBottomBar(
          currentScreen = currentScreen,
          onNavigate = { screen -> viewModel.navigateTo(screen) }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
      ) { screen ->
        when (screen) {
          ShreniScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
          ShreniScreen.SCAN_HOME -> ShreniScanHomeScreen(viewModel = viewModel)
          ShreniScreen.CAMERA -> CameraScreen(viewModel = viewModel)
          ShreniScreen.ORIGINAL_PREVIEW -> OriginalPreviewScreen(viewModel = viewModel)
          ShreniScreen.IMAGE_ACTIONS -> ImageActionsScreen(viewModel = viewModel)
          ShreniScreen.PROCESSING -> ProcessingScreen(viewModel = viewModel)
          ShreniScreen.BEFORE_AFTER -> BeforeAfterScreen(viewModel = viewModel)
          ShreniScreen.ENHANCEMENT_SETTINGS -> EnhancementSettingsScreen(viewModel = viewModel)
          ShreniScreen.PRODUCT_REVIEW -> ProductReviewScreen(viewModel = viewModel)
          ShreniScreen.PRICE_SETTINGS -> PriceScreen(viewModel = viewModel)
          ShreniScreen.MARKETPLACE_PREVIEW -> MarketplacePreviewScreen(viewModel = viewModel)
          ShreniScreen.SUCCESS -> SuccessScreen(viewModel = viewModel)
          ShreniScreen.BAZAAR_CATALOGUE -> ShreniBazaarScreen(viewModel = viewModel)
          ShreniScreen.SHRENI_AI -> ShreniAIScreen(viewModel = viewModel)
          ShreniScreen.SHRENI_VANI -> ShreniVaniScreen(viewModel = viewModel)
          ShreniScreen.SHRENI_PAY -> ShreniPayScreen(viewModel = viewModel)
          ShreniScreen.SHRENI_SETU -> ShreniSetuScreen(viewModel = viewModel)
          ShreniScreen.ADD_PRODUCT_WIZARD -> AddProductWizardScreen(viewModel = viewModel)
          ShreniScreen.SCAN_GALLERY -> ScanGalleryScreen(viewModel = viewModel)
        }
      }
    }
  }
}


