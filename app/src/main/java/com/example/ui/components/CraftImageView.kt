package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import java.io.File

@Composable
fun CraftImageView(
    imageUriOrPath: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    isTransparentCheckerboard: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (isTransparentCheckerboard) {
            // Visual transparency checkerboard pattern
            Canvas(modifier = Modifier.fillMaxSize()) {
                val squareSize = 14.dp.toPx()
                val cols = (size.width / squareSize).toInt() + 1
                val rows = (size.height / squareSize).toInt() + 1
                val lightColor = Color(0xFFF0F0F0)
                val darkColor = Color(0xFFDCDCDC)

                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        val color = if ((r + c) % 2 == 0) lightColor else darkColor
                        drawRect(
                            color = color,
                            topLeft = Offset(c * squareSize, r * squareSize),
                            size = Size(squareSize, squareSize)
                        )
                    }
                }
            }
        }

        if (imageUriOrPath.isNullOrBlank()) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
        } else {
            val context = LocalContext.current
            val model = if (imageUriOrPath.startsWith("/")) File(imageUriOrPath) else imageUriOrPath

            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(model)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                error = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Image load error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            )
        }
    }
}
