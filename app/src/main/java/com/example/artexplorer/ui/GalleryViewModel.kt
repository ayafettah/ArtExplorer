package com.example.artexplorer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artexplorer.data.ArtApi
import com.example.artexplorer.data.Artwork
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class GalleryViewModel : ViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _state = MutableStateFlow<UiState<List<Artwork>>>(UiState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _query.debounce(400).distinctUntilChanged().collectLatest { load(it) }
        }
    }

    private suspend fun load(q: String) {
        _state.value = UiState.Loading
        _state.value = try {
            val res = if (q.isBlank()) ArtApi.getArtworks()
            else ArtApi.searchArtworks(q.trim())
            UiState.Success(res.data.filter { it.imageId != null })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error("Impossible de charger les œuvres")
        }
    }

    fun onQueryChange(value: String) { _query.value = value }

    fun retry() { viewModelScope.launch { load(_query.value) } }
}