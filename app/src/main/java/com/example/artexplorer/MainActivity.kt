package com.example.artexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.artexplorer.ui.SplashScreen
import com.example.artexplorer.ui.theme.ArtExplorerTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArtExplorerTheme {
                var showSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(2000)
                    showSplash = false
                }
                Box(Modifier.fillMaxSize()) {
                    ArtApp()   // se charge déjà pendant le splash
                    AnimatedVisibility(
                        visible = showSplash,
                        exit = fadeOut(tween(600))
                    ) { SplashScreen() }
                }
            }
        }
    }
}