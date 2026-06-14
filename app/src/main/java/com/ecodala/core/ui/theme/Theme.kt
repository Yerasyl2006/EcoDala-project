package com.ecodala.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = EcoGreen,
    onPrimary = EcoSurface,
    primaryContainer = EcoGreenLight,
    onPrimaryContainer = EcoGreenDark,
    surface = EcoSurface,
    onSurface = EcoText,
    surfaceVariant = Color(0xFFEFF4EA),
    onSurfaceVariant = Color(0xFF5D665F),
    background = EcoSurface,
    onBackground = EcoText
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7DDB8A),
    onPrimary = Color(0xFF063B16),
    primaryContainer = Color(0xFF174B23),
    onPrimaryContainer = Color(0xFFC9F4C9),
    surface = Color(0xFF111711),
    onSurface = Color(0xFFE4EDE2),
    surfaceVariant = Color(0xFF1B251D),
    onSurfaceVariant = Color(0xFFA9B7AA),
    background = Color(0xFF0B120C),
    onBackground = Color(0xFFE4EDE2)
)

@Composable
fun EcoDalaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
