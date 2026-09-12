package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BrandLockup
import com.example.ui.components.MotifBand
import com.example.ui.theme.ShreniBorder
import com.example.ui.theme.ShreniGoldAccent
import com.example.ui.theme.ShreniMaroonSecondary
import com.example.ui.theme.ShreniSuccessGreen
import com.example.ui.theme.ShreniTerracottaPrimary
import com.example.viewmodel.ShreniScanViewModel
import com.example.viewmodel.ShreniScreen

data class SetuPillar(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val iconBg: Color
)

val SETU_PILLARS = listOf(
    SetuPillar(
        title = "Shreni Scan & AI Enhancement",
        subtitle = "Zero-Distortion Craft Photography",
        description = "Intelligent non-destructive background removal powered by remove.bg and local edge protection. Preserves intricate handloom threads, terracotta rims, and brass contours.",
        icon = Icons.Default.Security,
        iconBg = ShreniTerracottaPrimary
    ),
    SetuPillar(
        title = "Shreni Vani & Gemini Cataloging",
        subtitle = "Voice-First Multilingual AI",
        description = "Artisans speak in Marathi, Hindi, or English. Gemini 3.6 Flash understands regional craft terminology and generates authentic marketplace listings with fair price insights.",
        icon = Icons.Default.AutoAwesome,
        iconBg = ShreniGoldAccent
    ),
    SetuPillar(
        title = "Shreni Bazaar Marketplace",
        subtitle = "Direct-to-Consumer Platform",
        description = "Eliminates exploitative middlemen. Authentic handmade crafts reach urban collectors with artisan certificates of authenticity and origin verification.",
        icon = Icons.Default.Storefront,
        iconBg = ShreniMaroonSecondary
    ),
    SetuPillar(
        title = "Shreni Pay & Direct DBT",
        subtitle = "Zero-Commission Fair Payments",
        description = "Payments are routed directly to artisan bank accounts or UPI without deductions, establishing transparent financial independence for rural clusters.",
        icon = Icons.Default.Payments,
        iconBg = ShreniSuccessGreen
    )
)

@Composable
fun ShreniSetuScreen(
    viewModel: ShreniScanViewModel
) {
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column {
                    MotifBand()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.testTag("setu_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Shreni Setu",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "One ecosystem connecting your craft to the world",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Brand Lockup Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, ShreniBorder, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    BrandLockup()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Bridging traditional Indian craftsmanship with modern digital commerce through edge-safe AI, vernacular voice interfaces, and fair price governance.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }

            // Ecosystem Pillars
            Text(
                text = "The 4 Pillars of Shreni Setu",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            SETU_PILLARS.forEach { pillar ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, ShreniBorder, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(pillar.iconBg.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = pillar.icon,
                                contentDescription = null,
                                tint = pillar.iconBg,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pillar.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = pillar.subtitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ShreniTerracottaPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = pillar.description,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.navigateTo(ShreniScreen.SCAN_HOME) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("setu_start_scan_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ShreniTerracottaPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Launch Shreni Scan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
