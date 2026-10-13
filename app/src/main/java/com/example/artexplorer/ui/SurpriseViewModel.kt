package com.example.artexplorer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artexplorer.data.ArtApi
import com.example.artexplorer.data.Artwork
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SurpriseViewModel : ViewModel() {
    private val _state = MutableStateFlow<UiState<Artwork>>(UiState.Loading)
    val state = _state.asStateFlow()

    init { next() }

    fun next() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                val art = ArtApi.getRandomArtwork()
                if (art != null) UiState.Success(art) else UiState.Error("Aucune œuvre trouvée")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error("Impossible de charger une œuvre")
            }
        }
    }
}