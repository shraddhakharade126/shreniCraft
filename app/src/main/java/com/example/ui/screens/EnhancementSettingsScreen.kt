package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CraftImageView
import com.example.ui.components.ShreniTopBar
import com.example.ui.theme.ShreniTealPrimary
import com.example.ui.theme.ShreniTerracottaSecondary
import com.example.viewmodel.ShreniScanViewModel
import kotlin.math.roundToInt

@Composable
fun EnhancementSettingsScreen(
    viewModel: ShreniScanViewModel
) {
    val productImage by viewModel.productImage.collectAsState()
    val settings by viewModel.enhancementSettings.collectAsState()

    Scaffold(
        topBar = {
            ShreniTopBar(
                title = "Enhancement Sliders",
                subtitle = "Manual Artisan Tuning",
                showBack = true,
                onBackClick = { viewModel.navigateBack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Guidance
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Handmade crafts should retain natural organic textures. Adjust gently to keep real craft look.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Live Image Preview Card
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                CraftImageView(
                    imageUriOrPath = productImage?.processedUri ?: productImage?.originalUri,
                    contentDescription = "Live preview",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SLIDER 1: BRIGHTNESS
            EnhancementSliderCard(
                title = "Brightness",
                valueText = "${settings.brightness.roundToInt()}",
                icon = Icons.Default.Brightness6,
                value = settings.brightness,
                valueRange = -30f..30f,
                testTag = "slider_brightness",
                onValueChange = { viewModel.updateBrightness(it) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // SLIDER 2: CONTRAST
            EnhancementSliderCard(
                title = "Contrast",
                valueText = "%.2f".format(settings.contrast),
                icon = Icons.Default.Contrast,
                value = settings.contrast,
                valueRange = 0.8f..1.3f,
                testTag = "slider_contrast",
                onValueChange = { viewModel.updateContrast(it) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // SLIDER 3: SHARPNESS
            EnhancementSliderCard(
                title = "Sharpness (Texture Clarity)",
                valueText = "%.2f".format(settings.sharpness),
                icon = Icons.Default.Details,
                value = settings.sharpness,
                valueRange = 0f..0.8f,
                testTag = "slider_sharpness",
                onValueChange = { viewModel.updateSharpness(it) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // SLIDER 4: COLOR SATURATION
            EnhancementSliderCard(
                title = "Color Saturation",
                valueText = "%.2f".format(settings.saturation),
                icon = Icons.Default.ColorLens,
                value = settings.saturation,
                valueRange = 0.8f..1.4f,
                testTag = "slider_saturation",
                onValueChange = { viewModel.updateSaturation(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Reset, Use Original, Apply
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.resetEnhancementSettings() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("enhancement_reset_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.selectImageForListing(useOriginal = true) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("enhancement_use_original_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Use Original", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { viewModel.applyManualEnhancement() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("enhancement_apply_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ShreniTealPrimary)
            ) {
                Text("Apply & Update Preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun EnhancementSliderCard(
    title: String,
    valueText: String,
    icon: ImageVector,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    testTag: String,
    onValueChange: (Float) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ShreniTealPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = valueText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ShreniTerracottaSecondary
                )
            }
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag),
                colors = SliderDefaults.colors(
                    thumbColor = ShreniTealPrimary,
                    activeTrackColor = ShreniTealPrimary
                )
            )
        }
    }
}
