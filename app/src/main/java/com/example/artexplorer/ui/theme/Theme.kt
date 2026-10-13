package com.example.artexplorer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Gold = Color(0xFFC9A227)

private val MuseumColors = darkColorScheme(
    primary = Gold,
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFF2F2F2),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFF2F2F2)
)

@Composable
fun ArtExplorerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = MuseumColors, content = content)
}