package com.example.artexplorer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GalleryScreen(
    vm: GalleryViewModel = viewModel(),
    onArtworkClick: (Int) -> Unit
) {
    val query by vm.query.collectAsState()
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        ScreenTitle("ArtExplorer")
        SearchField(query, vm::onQueryChange, "Rechercher (monet, portrait, chat...)")
        Spacer(Modifier.height(8.dp))

        when (val s = state) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(s.message, vm::retry)
            is UiState.Success ->
                if (s.data.isEmpty()) MessageView("Aucun résultat")
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