package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = ShreniTealPrimaryDark,
    onPrimary = ShreniTealOnPrimaryDark,
    primaryContainer = ShreniTealContainerDark,
    onPrimaryContainer = ShreniTealOnContainerDark,
    secondary = ShreniTerracottaSecondaryDark,
    onSecondary = ShreniTerracottaOnSecondaryDark,
    secondaryContainer = ShreniTerracottaContainerDark,
    onSecondaryContainer = ShreniTerracottaOnContainerDark,
    background = ShreniBackgroundDark,
    onBackground = ShreniOnBackgroundDark,
    surface = ShreniSurfaceDark,
    onSurface = ShreniOnSurfaceDark,
    surfaceVariant = ShreniSurfaceVariantDark,
    onSurfaceVariant = ShreniOnSurfaceVariantDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ShreniTealPrimary,
    onPrimary = ShreniTealOnPrimary,
    primaryContainer = ShreniTealContainer,
    onPrimaryContainer = ShreniTealOnContainer,
    secondary = ShreniTerracottaSecondary,
    onSecondary = ShreniTerracottaOnSecondary,
    secondaryContainer = ShreniTerracottaContainer,
    onSecondaryContainer = ShreniTerracottaOnContainer,
    tertiary = ShreniIndigoTertiary,
    onTertiary = ShreniIndigoOnTertiary,
    tertiaryContainer = ShreniIndigoContainer,
    onTertiaryContainer = ShreniIndigoOnContainer,
    background = ShreniBackground,
    onBackground = ShreniOnBackground,
    surface = ShreniSurface,
    onSurface = ShreniOnSurface,
    surfaceVariant = ShreniSurfaceVariant,
    onSurfaceVariant = ShreniOnSurfaceVariant
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Prefer consistent artisan branding over device wallpaper palette
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
