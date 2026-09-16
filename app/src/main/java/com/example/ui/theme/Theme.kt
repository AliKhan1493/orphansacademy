package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = DeepForestTealLight,
    onPrimary = Color.White,
    primaryContainer = DeepForestTealDark,
    onPrimaryContainer = PaleSageOffWhite,
    secondary = SoftAmberPeach,
    onSecondary = DarkMossGray,
    tertiary = PastelSeafoam,
    background = DarkMossGray,
    surface = Color(0xFF283B3B),
    onBackground = PaleSageOffWhite,
    onSurface = PaleSageOffWhite
  )

private val LightColorScheme =
  lightColorScheme(
    primary = DeepForestTeal,
    onPrimary = Color.White,
    primaryContainer = PastelSeafoamLight,
    onPrimaryContainer = DarkMossGray,
    secondary = SoftAmberPeach,
    onSecondary = Color.White,
    secondaryContainer = SoftAmberPeachLight,
    onSecondaryContainer = DarkMossGray,
    tertiary = PastelSeafoamDark,
    background = PaleSageOffWhite,
    surface = Color.White,
    surfaceVariant = PastelSeafoamLight,
    onSurfaceVariant = DarkMossGrayMuted,
    outline = PastelSeafoamDark,
    onBackground = DarkMossGray,
    onSurface = DarkMossGray
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
