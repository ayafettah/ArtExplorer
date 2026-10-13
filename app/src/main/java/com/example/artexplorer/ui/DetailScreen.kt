package com.example.artexplorer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.artexplorer.data.stripHtml
import com.example.artexplorer.ui.theme.Gold

@Composable
fun DetailScreen(vm: DetailViewModel = viewModel(), onBack: () -> Unit) {
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize()) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
        }
        when (val s = state) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(s.message, vm::load)
            is UiState.Success -> {
                val art = s.data
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    AsyncImage(
                        model = art.imageUrl(843),
                        contentDescription = art.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            art.title ?: "Sans titre",
                            style = MaterialTheme.typography.headlineMedium,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            art.artistDisplay ?: "Artiste inconnu",
                            color = Gold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        HorizontalDivider(Modifier.padding(vertical = 12.dp))
                        InfoRow("Date", art.dateDisplay)
                        InfoRow("Technique", art.mediumDisplay)
                        InfoRow("Dimensions", art.dimensions)
                        InfoRow("Origine", art.placeOfOrigin)
                        InfoRow("Description", art.description?.stripHtml())
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Column(Modifier.padding(vertical = 6.dp)) {
        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Gold)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}