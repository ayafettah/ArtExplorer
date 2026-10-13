package com.example.artexplorer.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artexplorer.data.ArtApi
import com.example.artexplorer.data.Artwork
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(handle: SavedStateHandle) : ViewModel() {
    private val id: Int = checkNotNull(handle["id"])

    private val _state = MutableStateFlow<UiState<Artwork>>(UiState.Loading)
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                UiState.Success(ArtApi.getArtwork(id))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error("Impossible de charger l'œuvre")
            }
        }
    }
}