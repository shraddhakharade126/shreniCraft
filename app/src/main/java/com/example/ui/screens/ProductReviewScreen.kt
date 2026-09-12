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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CraftImageView
import com.example.ui.components.ShreniTopBar
import com.example.ui.theme.ShreniTealPrimary
import com.example.ui.theme.ShreniTerracottaSecondary
import com.example.viewmodel.ShreniScanViewModel

@Composable
fun ProductReviewScreen(
    viewModel: ShreniScanViewModel
) {
    val productImage by viewModel.productImage.collectAsState()
    val analysis by viewModel.productAnalysis.collectAsState()

    val currentAnalysis = analysis ?: return

    Scaffold(
        topBar = {
            ShreniTopBar(
                title = "Product Details",
                subtitle = "Step 4 of 5 • Review & Edit",
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

            // Hero Header Card with thumbnail
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(76.dp)
                    ) {
                        CraftImageView(
                            imageUriOrPath = productImage?.activeDisplayUri,
                            contentDescription = "Selected product photo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ShreniTealPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI Craft Analysis",
                                style = MaterialTheme.typography.labelSmall,
                                color = ShreniTealPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentAnalysis.productName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${currentAnalysis.craftType} • ${currentAnalysis.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // EDITABLE FIELD: PRODUCT NAME
            OutlinedTextField(
                value = currentAnalysis.productName,
                onValueChange = { viewModel.updateProductName(it) },
                label = { Text("Product Name *") },
                leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_product_name"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // EDITABLE FIELD: CATEGORY
            OutlinedTextField(
                value = currentAnalysis.category,
                onValueChange = { viewModel.updateCategory(it) },
                label = { Text("Category *") },
                leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_category"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // EDITABLE FIELD: CRAFT TYPE
            OutlinedTextField(
                value = currentAnalysis.craftType,
                onValueChange = { viewModel.updateCraftType(it) },
                label = { Text("Craft Type / Tradition") },
                leadingIcon = { Icon(Icons.Default.Handyman, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_craft_type"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // EDITABLE FIELD: MATERIAL
            OutlinedTextField(
                value = currentAnalysis.material,
                onValueChange = { viewModel.updateMaterial(it) },
                label = { Text("Material Used") },
                leadingIcon = { Icon(Icons.Default.ColorLens, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_material"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // EDITABLE FIELD: DESCRIPTION
            OutlinedTextField(
                value = currentAnalysis.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Artisan Story & Description") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("input_description"),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(12.dp))

            // EDITABLE FIELD: TAGS
            OutlinedTextField(
                value = currentAnalysis.tags.joinToString(", "),
                onValueChange = { viewModel.updateTags(it) },
                label = { Text("Marketplace Tags (comma-separated)") },
                leadingIcon = { Icon(Icons.Default.Label, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_tags"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation and Choice buttons: Retake, Use Original, Continue to Pricing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.retakePhoto() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("review_retake_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Retake", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { viewModel.selectImageForListing(useOriginal = true) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("review_use_original_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Original", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { viewModel.continueToPrice() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("review_continue_pricing_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ShreniTealPrimary)
            ) {
                Text(
                    text = "Continue to Price Setting",
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
