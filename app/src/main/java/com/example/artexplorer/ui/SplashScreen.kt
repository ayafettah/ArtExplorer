package com.example.artexplorer.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.artexplorer.ui.theme.Gold

@Composable
fun SplashScreen() {
    var started by remember { mutableStateOf(false) }
    val fade by animateFloatAsState(if (started) 1f else 0f, tween(900), label = "fade")
    val grow by animateFloatAsState(
        if (started) 1f else 0.7f,
        tween(900, easing = FastOutSlowInEasing),
        label = "grow"
    )
    LaunchedEffect(Unit) { started = true }

    Box(
        Modifier.fillMaxSize().background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                alpha = fade
                scaleX = grow
                scaleY = grow
            }
        ) {
            Icon(
                Icons.Default.Brush,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(72.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "ArtExplorer",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = FontFamily.Serif,
                color = Gold
            )
            Text(
                "Art Institute of Chicago",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFBBBBBB)
            )
        }
    }
}