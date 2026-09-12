package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ShreniGoldAccent
import com.example.ui.theme.ShreniGoldContainer
import com.example.ui.theme.ShreniGoldOnContainer
import com.example.ui.theme.ShreniMaroonSecondary
import com.example.ui.theme.ShreniTerracottaPrimary

/**
 * Traditional Indian craft motif mark matching ShreniKart brand.
 */
@Composable
fun ShreniMark(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.linearGradient(
                    listOf(ShreniTerracottaPrimary, Color(0xFFDF6B41))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Traditional Indian floral artisan motif
        Text(
            text = "𑁍",
            color = Color.White,
            fontSize = (size.value * 0.55f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Brand Lockup matching ShreniKart: Mark + Title + Tagline.
 */
@Composable
fun BrandLockup(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ShreniMark(size = if (compact) 36.dp else 46.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "ShreniKart",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = if (compact) 18.sp else 22.sp
            )
            if (!compact) {
                Text(
                    text = "Empowering Artisans, Connecting Traditions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * Decorative motif band accent line for screens.
 */
@Composable
fun MotifBand(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        ShreniTerracottaPrimary,
                        ShreniGoldAccent,
                        ShreniMaroonSecondary,
                        ShreniGoldAccent,
                        ShreniTerracottaPrimary
                    )
                )
            )
    )
}

/**
 * Golden Live AI Badge indicating real Gemini & remove.bg AI capabilities.
 */
@Composable
fun LiveAiBadge(
    modifier: Modifier = Modifier,
    label: String = "Live Gemini AI"
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ShreniGoldContainer)
            .border(1.dp, ShreniGoldAccent.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(ShreniGoldAccent)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ShreniGoldOnContainer
        )
    }
}
