package com.example.artexplorer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.artexplorer.ui.theme.Gold

@Composable
fun ArtistWorksScreen(
    vm: ArtistWorksViewModel = viewModel(),
    onBack: () -> Unit,
    onArtworkClick: (Int) -> Unit
) {
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
            }
            Text(
                vm.name,
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily.Serif,
                color = Gold,
                maxLines = 2
            )
        }
        when (val s = state) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(s.message, vm::load)
            is UiState.Success ->
                if (s.data.isEmpty()) MessageView("Aucune œuvre trouvée")
                else LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(s.data, key = { it.id }) { art ->
                        ArtworkCard(art) { onArtworkClick(art.id) }
                    }
                }
        }
    }
}