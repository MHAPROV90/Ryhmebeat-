package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF00382E),
    primaryContainer = Color(0xFF005143),
    onPrimaryContainer = Color(0xFF6FFFE4),
    secondary = ElectricMagenta,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF5D0039),
    onSecondaryContainer = Color(0xFFFFD8E7),
    tertiary = NeonViolet,
    background = StudioDarkBg,
    onBackground = TextPrimary,
    surface = StudioCardBg,
    onSurface = TextPrimary,
    surfaceVariant = StudioCardElevated,
    onSurfaceVariant = TextSecondary,
    outline = StudioBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007A68),
    onPrimary = Color.White,
    secondary = Color(0xFFB5179E),
    onSecondary = Color.White,
    background = Color(0xFFF9F7FD),
    onBackground = Color(0xFF191722),
    surface = Color.White,
    onSurface = Color(0xFF191722),
    surfaceVariant = Color(0xFFECE7F5),
    onSurfaceVariant = Color(0xFF4C455A),
    outline = Color(0xFFC7BFD8)
)

@Composable
fun RhymeBeatTheme(
    darkTheme: Boolean = true, // Default to studio dark aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) = RhymeBeatTheme(darkTheme = darkTheme, content = content)
