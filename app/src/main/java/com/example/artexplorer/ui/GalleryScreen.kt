package com.example.artexplorer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.artexplorer.ui.theme.Gold

// Libellé affiché  ->  valeur exacte attendue par l'API
private val categories: List<Pair<String, String?>> = listOf(
    "Tous" to null,
    "Peintures" to "Painting",
    "Sculptures" to "Sculpture",
    "Photographies" to "Photograph",
    "Estampes" to "Print",
    "Dessins" to "Drawing and Watercolor",
    "Textiles" to "Textile"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    vm: GalleryViewModel = viewModel(),
    onArtworkClick: (Int) -> Unit
) {
    val query by vm.query.collectAsState()
    val category by vm.category.collectAsState()
    val state by vm.state.collectAsState()
    val gridState = rememberLazyGridState()

    val nearEnd by remember {
        derivedStateOf {
            val last = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= gridState.layoutInfo.totalItemsCount - 4
        }
    }
    LaunchedEffect(nearEnd, state.items.size) {
        if (nearEnd && state.items.isNotEmpty()) vm.loadMore()
    }
    LaunchedEffect(category) { gridState.scrollToItem(0) }

    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        ScreenTitle("ArtExplorer")
        SearchField(query, vm::onQueryChange, "Rechercher (monet, portrait, chat...)")
        Spacer(Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { (label, value) ->
                FilterChip(
                    selected = category == value,
                    onClick = { vm.onCategoryChange(value) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Gold,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        when {
            state.loading -> LoadingView()
            state.error != null -> ErrorView(state.error ?: "", vm::retry)
            state.items.isEmpty() -> MessageView("Aucun résultat")
            else -> LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.items, key = { it.id }) { art ->
                    ArtworkCard(art) { onArtworkClick(art.id) }
                }
                if (state.loadingMore) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                            CircularProgressIndicator(color = Gold)
                        }
                    }
                }
            }
        }
    }
}