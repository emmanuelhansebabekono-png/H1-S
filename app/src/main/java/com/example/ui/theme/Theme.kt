package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkCyberColorScheme = darkColorScheme(
    primary = CyberGreen,
    onPrimary = Color(0xFF381E72),
    primaryContainer = CyberGreenDark,
    onPrimaryContainer = Color(0xFFE8DEF8),
    secondary = CyberCyan,
    onSecondary = Color(0xFF332D41),
    secondaryContainer = CyberCyanDark,
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = CyberAmber,
    onTertiary = Color(0xFF492532),
    background = CyberBackground,
    onBackground = CyberTextPrimary,
    surface = CyberSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary,
    outline = CyberSurfaceBorder,
    error = CyberRed,
    onError = Color(0xFF601410)
)

@Composable
fun HackGuardTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkCyberColorScheme,
        typography = Typography,
        content = content
    )
}
