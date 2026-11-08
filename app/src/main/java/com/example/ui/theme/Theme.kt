package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val StarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = StarkBackground,
    primaryContainer = StarkSurfaceElevated,
    onPrimaryContainer = StarkCyan,
    secondary = StarkCyan,
    onSecondary = StarkBackground,
    secondaryContainer = StarkBorder,
    onSecondaryContainer = StarkTextPrimary,
    tertiary = StarkGold,
    onTertiary = StarkBackground,
    background = StarkBackground,
    onBackground = StarkTextPrimary,
    surface = StarkSurface,
    onSurface = StarkTextPrimary,
    surfaceVariant = StarkSurfaceElevated,
    onSurfaceVariant = StarkTextSecondary,
    outline = StarkBorder,
    outlineVariant = ElectricBlueDim,
    error = StarkError,
    onError = Color.White
)

@Composable
fun VisionAITheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StarkColorScheme,
        typography = Typography,
        content = content
    )
}
