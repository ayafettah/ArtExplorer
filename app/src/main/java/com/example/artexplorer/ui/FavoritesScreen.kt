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
import com.example.artexplorer.data.Artwork

@Composable
fun FavoritesScreen(
    vm: FavoritesViewModel = viewModel(),
    onArtworkClick: (Int) -> Unit
) {
    val favorites by vm.favorites.collectAsState()

    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        ScreenTitle("Mes favoris (${favorites.size})")
        if (favorites.isEmpty()) {
            MessageView("Aucun favori pour l'instant.\nTouchez ♥ sur une œuvre pour l'ajouter.")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites, key = { it.id }) { fav ->
                    ArtworkCard(
                        Artwork(
                            id = fav.id,
                            title = fav.title,
                            artistDisplay = fav.artist,
                            imageId = fav.imageId
                        )
                    ) { onArtworkClick(fav.id) }
                }
            }
        }
    }
}