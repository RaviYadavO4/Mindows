package com.dev.mindows.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MindowsColorScheme = lightColorScheme(
    primary = Win95Blue,
    background = Win95DesktopTeal,
    surface = Win95Grey,
    onPrimary = Win95White,
    onBackground = Win95Black,
    onSurface = Win95Black
)

@Composable
fun MindowsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MindowsColorScheme,
        typography = Typography,
        content = content
    )
}
