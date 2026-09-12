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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DemoCraft
import com.example.ui.components.CraftImageView
import com.example.ui.components.LiveAiBadge
import com.example.ui.components.MotifBand
import com.example.ui.components.ShreniMark
import com.example.ui.theme.ShreniBorder
import com.example.ui.theme.ShreniGoldAccent
import com.example.ui.theme.ShreniGoldContainer
import com.example.ui.theme.ShreniGoldOnContainer
import com.example.ui.theme.ShreniMaroonSecondary
import com.example.ui.theme.ShreniSuccessGreen
import com.example.ui.theme.ShreniTealPrimary
import com.example.ui.theme.ShreniTerracottaPrimary
import com.example.viewmodel.ShreniScanViewModel
import com.example.viewmodel.ShreniScreen

data class QuickActionItem(
    val title: String,
    val lead: String,
    val body: String,
    val emoji: String,
    val screen: ShreniScreen,
    val containerColor: Color,
    val borderColor: Color,
    val testTag: String
)

@Composable
fun DashboardScreen(
    viewModel: ShreniScanViewModel
) {
    val listingsCount by viewModel.listingsCount.collectAsState()
    val publishedListings by viewModel.publishedListings.collectAsState()
    val scansCount by viewModel.scansCount.collectAsState()

    val quickActions = listOf(
        QuickActionItem(
            title = "Shreni Vani",
            lead = "Speak & Create",
            body = "Describe your craft using your voice.",
            emoji = "🎙️",
            screen = ShreniScreen.SHRENI_VANI,
            containerColor = ShreniTerracottaPrimary.copy(alpha = 0.08f),
            borderColor = ShreniTerracottaPrimary.copy(alpha = 0.25f),
            testTag = "quick_action_vani"
        ),
        QuickActionItem(
            title = "Shreni AI",
            lead = "AI Assistant",
            body = "Get help with products, pricing & buyers.",
            emoji = "✨",
            screen = ShreniScreen.SHRENI_AI,
            containerColor = ShreniGoldAccent.copy(alpha = 0.12f),
            borderColor = ShreniGoldAccent.copy(alpha = 0.35f),
            testTag = "quick_action_ai"
        ),
        QuickActionItem(
            title = "Shreni Bazaar",
            lead = "Marketplace",
            body = "See how your products appear to buyers.",
            emoji = "🛍️",
            screen = ShreniScreen.BAZAAR_CATALOGUE,
            containerColor = ShreniMaroonSecondary.copy(alpha = 0.08f),
            borderColor = ShreniMaroonSecondary.copy(alpha = 0.25f),
            testTag = "quick_action_bazaar"
        ),
        QuickActionItem(
            title = "Shreni Pay",
            lead = "Your Earnings",
            body = "Track sales, orders and direct payouts.",
            emoji = "💰",
            screen = ShreniScreen.SHRENI_PAY,
            containerColor = ShreniSuccessGreen.copy(alpha = 0.10f),
            borderColor = ShreniSuccessGreen.copy(alpha = 0.25f),
            testTag = "quick_action_pay"
        )
    )

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
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ShreniMark(size = 38.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Namaste, Artisan 👋",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Your craft business in one place",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Profile Circle
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ShreniMaroonSecondary)
                                .clickable { viewModel.navigateTo(ShreniScreen.SHRENI_SETU) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VM",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // HERO CARD: Add New Product (matching dashboard.tsx)
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = ShreniTerracottaPrimary.copy(alpha = 0.08f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ShreniTerracottaPrimary.copy(alpha = 0.35f))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ShreniMark(size = 44.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Add New Product",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    LiveAiBadge(label = "Gemini AI")
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Turn your craft into a professional online listing with Shreni Scan non-destructive AI.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openCamera() },
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(50.dp)
                                    .testTag("capture_product_button"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ShreniTerracottaPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Scan Craft",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            OutlinedButton(
                                onClick = { viewModel.navigateTo(ShreniScreen.SCAN_HOME) },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(50.dp)
                                    .testTag("dashboard_add_product_button"),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Options",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            // AUTHENTICITY GALLERY (ROOM DB PERSISTENCE)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ShreniTealPrimary.copy(alpha = 0.08f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ShreniTealPrimary.copy(alpha = 0.35f))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openScanGallery() }
                        .testTag("dashboard_scan_gallery_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(ShreniTealPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Collections,
                                contentDescription = null,
                                tint = ShreniTealPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Authenticity Gallery",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    color = ShreniTealPrimary,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "$scansCount Scans",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Room database saved scans. Review integrity & publish crafts.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // QUICK ACTIONS (matching dashboard.tsx 2x2 grid)
            item {
                Column {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionCard(
                            item = quickActions[0],
                            onClick = { viewModel.navigateTo(quickActions[0].screen) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionCard(
                            item = quickActions[1],
                            onClick = { viewModel.navigateTo(quickActions[1].screen) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionCard(
                            item = quickActions[2],
                            onClick = { viewModel.navigateTo(quickActions[2].screen) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionCard(
                            item = quickActions[3],
                            onClick = { viewModel.navigateTo(quickActions[3].screen) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // AT A GLANCE (matching dashboard.tsx stats)
            item {
                Column {
                    Text(
                        text = "At a Glance",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Products Listed",
                            value = "${listingsCount.coerceAtLeast(3)}",
                            icon = Icons.Default.ShoppingBag,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Orders",
                            value = "14",
                            icon = Icons.Default.ShoppingBag,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Pending Inquiries",
                            value = "3",
                            icon = Icons.AutoMirrored.Filled.Message,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Total Earnings",
                            value = "₹ 1,18,244",
                            icon = Icons.Default.CurrencyRupee,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // POPULAR PICKS (matching dashboard.tsx carousel)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Popular Picks",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "See all",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShreniTerracottaPrimary,
                            modifier = Modifier.clickable { viewModel.navigateTo(ShreniScreen.BAZAAR_CATALOGUE) }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(DemoCraft.SAMPLES) { craft ->
                            Box(
                                modifier = Modifier
                                    .width(160.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(1.dp, ShreniBorder, RoundedCornerShape(16.dp))
                                    .clickable {
                                        viewModel.selectDemoProduct(craft)
                                        viewModel.navigateTo(ShreniScreen.ORIGINAL_PREVIEW)
                                    }
                            ) {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .background(Color(craft.primaryColorHex).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Collections,
                                            contentDescription = craft.title,
                                            tint = Color(craft.primaryColorHex),
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = craft.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = craft.category,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "₹ ${craft.defaultPrice.toInt()}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = ShreniTerracottaPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SHRENI SETU ECOSYSTEM BANNER (matching dashboard.tsx)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(ShreniMaroonSecondary.copy(alpha = 0.08f))
                        .border(1.dp, ShreniMaroonSecondary.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                        .clickable { viewModel.navigateTo(ShreniScreen.SHRENI_SETU) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ShreniMaroonSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Shreni Setu Ecosystem",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "One digital bridge connecting tradition to the world.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ShreniMaroonSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun QuickActionCard(
    item: QuickActionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(item.containerColor)
            .border(1.dp, item.borderColor, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(item.testTag)
    ) {
        Column {
            Text(text = item.emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.lead,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = ShreniTerracottaPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.body,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, ShreniBorder, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ShreniTerracottaPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
