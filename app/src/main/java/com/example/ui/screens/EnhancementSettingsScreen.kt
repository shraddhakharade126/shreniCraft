package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CraftImageView
import com.example.ui.components.ShreniTopBar
import com.example.ui.theme.ShreniSuccessGreen
import com.example.ui.theme.ShreniTealPrimary
import com.example.ui.theme.ShreniTerracottaPrimary
import com.example.ui.theme.ShreniTerracottaSecondary
import com.example.viewmodel.ShreniScanViewModel
import kotlin.math.roundToInt

@Composable
fun EnhancementSettingsScreen(
    viewModel: ShreniScanViewModel
) {
    val productImage by viewModel.productImage.collectAsState()
    val settings by viewModel.enhancementSettings.collectAsState()
    val enhancedPreviewUri by viewModel.enhancedPreviewUri.collectAsState()

    // Preview mode: true = After (Enhanced), false = Before (Original)
    var showAfter by remember { mutableStateOf(true) }

    // Generate initial live preview when entering screen
    LaunchedEffect(settings) {
        viewModel.generateEnhancedPreview()
    }

    Scaffold(
        topBar = {
            ShreniTopBar(
                title = "Enhancement Studio",
                subtitle = "Non-Generative Artisan Tuning",
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

            // Non-Generative Preservation Guarantee Banner
            Surface(
                color = ShreniSuccessGreen.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = ShreniSuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "100% Authentic Non-Generative Pipeline",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ShreniSuccessGreen
                        )
                        Text(
                            text = "Preserves real handmade textures, shapes, and decorations. Zero Generative AI. Original image is never overwritten.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // BEFORE / AFTER TOGGLE SELECTOR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // BEFORE BUTTON
                Surface(
                    color = if (!showAfter) ShreniTerracottaPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clickable { showAfter = false }
                        .testTag("enhancement_before_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📷 Before (Original)",
                            fontWeight = if (!showAfter) FontWeight.Bold else FontWeight.Medium,
                            color = if (!showAfter) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                // AFTER BUTTON
                Surface(
                    color = if (showAfter) ShreniTealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clickable { showAfter = true }
                        .testTag("enhancement_after_button")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "✨ After (Enhanced)",
                            fontWeight = if (showAfter) FontWeight.Bold else FontWeight.Medium,
                            color = if (showAfter) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // LIVE IMAGE PREVIEW CARD
            Card(
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                val activeDisplayUri = if (showAfter) {
                    enhancedPreviewUri ?: productImage?.processedUri ?: productImage?.originalUri
                } else {
                    productImage?.originalUri
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    CraftImageView(
                        imageUriOrPath = activeDisplayUri,
                        contentDescription = if (showAfter) "Enhanced product preview" else "Original photo preview",
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay badge indicating current view state
                    Surface(
                        color = if (showAfter) ShreniTealPrimary else Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = if (showAfter) "AFTER: ENHANCED CRAFT" else "BEFORE: UNTOUCHED PHOTO",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Interactive flip badge
                    Surface(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .clickable { showAfter = !showAfter }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Compare,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (showAfter) "Tap for Before" else "Tap for After",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Allowed Non-Generative Tuning Controls",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 1. BRIGHTNESS SLIDER
            EnhancementSliderCard(
                title = "Brightness",
                subtitle = "Balances studio exposure",
                valueText = "${settings.brightness.roundToInt()}",
                icon = Icons.Default.Brightness6,
                value = settings.brightness,
                valueRange = -30f..30f,
                testTag = "slider_brightness",
                onValueChange = {
                    viewModel.updateBrightness(it)
                    showAfter = true
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 2. CONTRAST SLIDER
            EnhancementSliderCard(
                title = "Contrast",
                subtitle = "Enhances depth and shadows",
                valueText = "%.2f".format(settings.contrast),
                icon = Icons.Default.Contrast,
                value = settings.contrast,
                valueRange = 0.8f..1.3f,
                testTag = "slider_contrast",
                onValueChange = {
                    viewModel.updateContrast(it)
                    showAfter = true
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. SHARPNESS (TEXTURE & EDGE CLARITY)
            EnhancementSliderCard(
                title = "Sharpness (Texture & Carvings)",
                subtitle = "Clarifies natural weave, carvings & grain",
                valueText = "%.2f".format(settings.sharpness),
                icon = Icons.Default.Details,
                value = settings.sharpness,
                valueRange = 0f..0.8f,
                testTag = "slider_sharpness",
                onValueChange = {
                    viewModel.updateSharpness(it)
                    showAfter = true
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 4. COLOR CORRECTION / SATURATION
            EnhancementSliderCard(
                title = "Color Correction",
                subtitle = "Preserves authentic natural dye vibrancy",
                valueText = "%.2f".format(settings.colorCorrection),
                icon = Icons.Default.ColorLens,
                value = settings.colorCorrection,
                valueRange = 0.8f..1.4f,
                testTag = "slider_saturation",
                onValueChange = {
                    viewModel.updateColorCorrection(it)
                    showAfter = true
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 5. WHITE BALANCE (COLOR TEMPERATURE)
            EnhancementSliderCard(
                title = "White Balance (Temperature)",
                subtitle = "Compensates for warm indoor or cool lighting",
                valueText = if (settings.whiteBalance > 0) "+${settings.whiteBalance.roundToInt()} (Warm)" else if (settings.whiteBalance < 0) "${settings.whiteBalance.roundToInt()} (Cool)" else "Neutral",
                icon = Icons.Default.WbSunny,
                value = settings.whiteBalance,
                valueRange = -20f..20f,
                testTag = "slider_white_balance",
                onValueChange = {
                    viewModel.updateWhiteBalance(it)
                    showAfter = true
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 6. MILD NOISE REDUCTION
            EnhancementSliderCard(
                title = "Mild Noise Reduction",
                subtitle = "Smooths sensor noise while preserving all craft details",
                valueText = "%.2f".format(settings.noiseReduction),
                icon = Icons.Default.Tune,
                value = settings.noiseReduction,
                valueRange = 0f..0.5f,
                testTag = "slider_noise_reduction",
                onValueChange = {
                    viewModel.updateNoiseReduction(it)
                    showAfter = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ACTION CONTROLS: Reset, Use Original
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.resetEnhancementSettings()
                        showAfter = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("enhancement_reset_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.useOriginalFromEnhancement() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("enhancement_use_original_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Use Original", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // APPLY BUTTON
            Button(
                onClick = { viewModel.applyManualEnhancement() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("enhancement_apply_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ShreniTealPrimary)
            ) {
                Text(
                    text = "Apply & Save Enhancement",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EnhancementSliderCard(
    title: String,
    subtitle: String,
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
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )
                    }
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
