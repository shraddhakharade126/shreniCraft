package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.ShreniBorder
import com.example.ui.theme.ShreniTerracottaPrimary
import com.example.viewmodel.ShreniScanViewModel
import com.example.viewmodel.ShreniScreen

data class NavTabItem(
    val title: String,
    val icon: ImageVector,
    val screen: ShreniScreen,
    val testTag: String
)

@Composable
fun ShreniBottomBar(
    currentScreen: ShreniScreen,
    onNavigate: (ShreniScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home / Dashboard
            BottomNavItem(
                label = "Home",
                icon = Icons.Default.Home,
                isSelected = currentScreen == ShreniScreen.DASHBOARD,
                onClick = { onNavigate(ShreniScreen.DASHBOARD) },
                testTag = "nav_home"
            )

            // Vani (Voice)
            BottomNavItem(
                label = "Vani",
                icon = Icons.Default.Mic,
                isSelected = currentScreen == ShreniScreen.SHRENI_VANI,
                onClick = { onNavigate(ShreniScreen.SHRENI_VANI) },
                testTag = "nav_vani"
            )

            // Center Prominent Add / Scan Button
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(ShreniTerracottaPrimary)
                    .clickable { onNavigate(ShreniScreen.ADD_PRODUCT_WIZARD) }
                    .testTag("nav_add_product"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Product",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            // AI Assistant
            BottomNavItem(
                label = "Shreni AI",
                icon = Icons.Default.AutoAwesome,
                isSelected = currentScreen == ShreniScreen.SHRENI_AI,
                onClick = { onNavigate(ShreniScreen.SHRENI_AI) },
                testTag = "nav_ai"
            )

            // Bazaar
            BottomNavItem(
                label = "Bazaar",
                icon = Icons.Default.Storefront,
                isSelected = currentScreen == ShreniScreen.BAZAAR_CATALOGUE,
                onClick = { onNavigate(ShreniScreen.BAZAAR_CATALOGUE) },
                testTag = "nav_bazaar"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) ShreniTerracottaPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ShreniTerracottaPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
