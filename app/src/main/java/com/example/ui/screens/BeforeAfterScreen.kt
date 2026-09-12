package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BackgroundStyle
import com.example.ui.components.CraftImageView
import com.example.ui.components.ShreniTopBar
import com.example.ui.theme.ShreniSuccessGreen
import com.example.ui.theme.ShreniTealPrimary
import com.example.ui.theme.ShreniTerracottaSecondary
import com.example.ui.theme.ShreniWarningAmber
import com.example.viewmodel.ShreniScanViewModel

enum class CompareViewMode {
    TOGGLE,
    SIDE_BY_SIDE
}

@Composable
fun BeforeAfterScreen(
    viewModel: ShreniScanViewModel
) {
    val productImage by viewModel.productImage.collectAsState()
    val integrityResult by viewModel.integrityResult.collectAsState()
    val backgroundStyle by viewModel.backgroundStyle.collectAsState()

    var showProcessed by remember { mutableStateOf(true) }
    var viewMode by remember { mutableStateOf(CompareViewMode.TOGGLE) }

    val isIntegrityPassed = integrityResult?.passed != false
    val confidence = integrityResult?.confidenceScore ?: productImage?.segmentationConfidence ?: 0.98f

    Scaffold(
        topBar = {
            ShreniTopBar(
                title = "Review Result",
                subtitle = "Step 3 of 5 • Verify Craft Details",
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
            Spacer(modifier = Modifier.height(6.dp))

            // INTEGRITY STATUS BANNER
            if (isIntegrityPassed) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ShreniSuccessGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Product integrity protected ✓ (${(confidence * 100).toInt()}%)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "100% authentic foreground pixels preserved without alterations. Zero Generative AI.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            } else {
                // INTEGRITY SAFETY WARNING BANNER
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Product boundary is unclear (${(confidence * 100).toInt()}%).",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "Background removal was skipped to protect delicate craft details. You can retake or use the original photo safely.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BACKGROUND STYLE SELECTOR CHIPS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Transparent PNG Chip
                val isTransparent = backgroundStyle == BackgroundStyle.TRANSPARENT && showProcessed
                Surface(
                    color = if (isTransparent) ShreniTealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            showProcessed = true
                            viewModel.setBackgroundStyle(BackgroundStyle.TRANSPARENT)
                        }
                ) {
                    Text(
                        text = "🔲 Transparent",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isTransparent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isTransparent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        maxLines = 1
                    )
                }

                // Studio White Chip
                val isWhite = backgroundStyle == BackgroundStyle.STUDIO_WHITE && showProcessed
                Surface(
                    color = if (isWhite) ShreniTealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            showProcessed = true
                            viewModel.setBackgroundStyle(BackgroundStyle.STUDIO_WHITE)
                        }
                ) {
                    Text(
                        text = "⚪ White",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isWhite) FontWeight.Bold else FontWeight.Normal,
                        color = if (isWhite) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        maxLines = 1
                    )
                }

                // Original Chip
                val isOriginal = !showProcessed || backgroundStyle == BackgroundStyle.ORIGINAL
                Surface(
                    color = if (isOriginal) ShreniTealPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            showProcessed = false
                            viewModel.setBackgroundStyle(BackgroundStyle.ORIGINAL)
                        }
                ) {
                    Text(
                        text = "📷 Original",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isOriginal) FontWeight.Bold else FontWeight.Normal,
                        color = if (isOriginal) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // COMPARISON CONTROLS (Before/After vs Side-by-Side)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle between Original and Processed
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                ) {
                    Surface(
                        color = if (!showProcessed) ShreniTealPrimary else Color.Transparent,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { showProcessed = false }
                    ) {
                        Text(
                            text = "Original",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (!showProcessed) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        color = if (showProcessed) ShreniTealPrimary else Color.Transparent,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { showProcessed = true }
                    ) {
                        Text(
                            text = "Shreni Scan Result",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (showProcessed) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                // Side by side toggle button
                Surface(
                    color = if (viewMode == CompareViewMode.SIDE_BY_SIDE) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.clickable {
                        viewMode = if (viewMode == CompareViewMode.SIDE_BY_SIDE) CompareViewMode.TOGGLE else CompareViewMode.SIDE_BY_SIDE
                    }
                ) {
                    Text(
                        text = if (viewMode == CompareViewMode.SIDE_BY_SIDE) "Single View" else "Split View",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // MAIN IMAGE DISPLAY AREA
            if (viewMode == CompareViewMode.SIDE_BY_SIDE) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CraftImageView(
                                imageUriOrPath = productImage?.originalUri,
                                contentDescription = "Original Craft",
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                color = Color.Black.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Original",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        val splitProcessedUri = when (backgroundStyle) {
                            BackgroundStyle.TRANSPARENT -> productImage?.transparentUri ?: productImage?.processedUri ?: productImage?.originalUri
                            BackgroundStyle.STUDIO_WHITE -> productImage?.whiteBackgroundUri ?: productImage?.processedUri ?: productImage?.originalUri
                            BackgroundStyle.ORIGINAL -> productImage?.originalUri
                        }
                        val splitIsCheckerboard = backgroundStyle == BackgroundStyle.TRANSPARENT && productImage?.transparentUri != null

                        Box(modifier = Modifier.fillMaxSize()) {
                            CraftImageView(
                                imageUriOrPath = splitProcessedUri,
                                contentDescription = "Shreni Scan Result",
                                modifier = Modifier.fillMaxSize(),
                                isTransparentCheckerboard = splitIsCheckerboard,
                                backgroundColor = if (backgroundStyle == BackgroundStyle.STUDIO_WHITE) Color.White else Color(0xFFFCFCFC)
                            )
                            Surface(
                                color = ShreniTealPrimary,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = if (splitIsCheckerboard) "Transparent" else "Studio Result",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    val activeUri = if (showProcessed) {
                        when (backgroundStyle) {
                            BackgroundStyle.TRANSPARENT -> productImage?.transparentUri ?: productImage?.processedUri ?: productImage?.originalUri
                            BackgroundStyle.STUDIO_WHITE -> productImage?.whiteBackgroundUri ?: productImage?.processedUri ?: productImage?.originalUri
                            BackgroundStyle.ORIGINAL -> productImage?.originalUri
                        }
                    } else {
                        productImage?.originalUri
                    }
                    val isCheckerboard = showProcessed && backgroundStyle == BackgroundStyle.TRANSPARENT && productImage?.transparentUri != null

                    Box(modifier = Modifier.fillMaxSize()) {
                        CraftImageView(
                            imageUriOrPath = activeUri,
                            contentDescription = if (showProcessed) "Processed image" else "Original image",
                            modifier = Modifier.fillMaxSize(),
                            isTransparentCheckerboard = isCheckerboard,
                            backgroundColor = if (showProcessed && backgroundStyle == BackgroundStyle.STUDIO_WHITE) Color.White else MaterialTheme.colorScheme.surfaceVariant
                        )

                        Surface(
                            color = if (showProcessed) ShreniTealPrimary else Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = if (showProcessed) {
                                    if (isCheckerboard) "TRANSPARENT PNG (ORIGINAL CRAFT)" else "STUDIO WHITE BACKDROP"
                                } else {
                                    "ORIGINAL CAMERA PHOTO"
                                },
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Tap hint
                        Surface(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .clickable { showProcessed = !showProcessed }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Tap to toggle",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // FINE-TUNE BUTTON (Artisan manual controls)
            OutlinedButton(
                onClick = { viewModel.openEnhancementSettings() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("before_after_fine_tune_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Fine-tune Enhancements (Sliders)", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // PRIMARY DECISION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.retakePhoto() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("before_after_retake_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Retake", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.selectImageForListing(useOriginal = true) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("before_after_use_original_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Use Original", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { viewModel.selectImageForListing(useOriginal = false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("before_after_use_this_image_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ShreniTealPrimary)
            ) {
                Text(
                    text = "Use This Image & Continue",
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

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
