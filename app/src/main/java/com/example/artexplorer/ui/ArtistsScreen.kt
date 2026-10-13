package com.example.artexplorer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.artexplorer.data.Artist
import com.example.artexplorer.data.stripHtml
import com.example.artexplorer.ui.theme.Gold

private val suggestions =
    listOf("Monet", "Van Gogh", "Picasso", "Renoir", "Degas", "Cézanne", "Hopper", "Warhol")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistsScreen(
    vm: ArtistsViewModel = viewModel(),
    onArtistClick: (Int, String) -> Unit
) {
    val query by vm.query.collectAsState()
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        ScreenTitle("Artistes")
        SearchField(query, vm::onQueryChange, "Rechercher un artiste (van gogh...)")
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(suggestions) { name ->
                AssistChip(onClick = { vm.onQueryChange(name) }, label = { Text(name) })
            }
        }
        Spacer(Modifier.height(8.dp))

        when (val s = state) {
            UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(s.message, vm::retry)
            is UiState.Success ->
                if (s.data.isEmpty()) MessageView("Aucun artiste trouvé")
                else LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(s.data, key = { it.id }) { artist ->
                        ArtistCard(artist) { onArtistClick(artist.id, artist.title ?: "Artiste") }
                    }
                }
        }
    }
}

@Composable
fun ArtistCard(artist: Artist, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                artist.title ?: "Inconnu",
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleMedium
            )
            val dates = listOfNotNull(artist.birthDate, artist.deathDate).joinToString(" – ")
            if (dates.isNotBlank()) Text(dates, color = Gold)
            artist.description?.stripHtml()?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    it, maxLines = 4, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("Voir les œuvres ›", color = Gold, style = MaterialTheme.typography.labelLarge)
        }
    }
}