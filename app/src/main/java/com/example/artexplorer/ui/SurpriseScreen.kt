package com.example.artexplorer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SurpriseScreen(
    vm: SurpriseViewModel = viewModel(),
    onArtworkClick: (Int) -> Unit
) {
    val state by vm.state.collectAsState()

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle("Œuvre surprise")

        Box(Modifier.weight(1f).fillMaxWidth()) {
            when (val s = state) {
                UiState.Loading -> LoadingView()
                is UiState.Error -> ErrorView(s.message, vm::next)
                is UiState.Success -> ArtworkCard(s.data) { onArtworkClick(s.data.id) }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = vm::next, modifier = Modifier.fillMaxWidth()) {
            Text("Surprends-moi !")
        }
    }
}