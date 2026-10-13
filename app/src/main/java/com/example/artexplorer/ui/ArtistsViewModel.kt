package com.example.artexplorer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artexplorer.data.ArtApi
import com.example.artexplorer.data.Artist
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ArtistsViewModel : ViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _state = MutableStateFlow<UiState<List<Artist>>>(UiState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _query.debounce(400).distinctUntilChanged().collectLatest { load(it) }
        }
    }

    private suspend fun load(q: String) {
        _state.value = UiState.Loading
        _state.value = try {
            val res = if (q.isBlank()) ArtApi.getArtists() else ArtApi.searchArtists(q.trim())
            UiState.Success(res.data)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            UiState.Error("Impossible de charger les artistes")
        }
    }

    fun onQueryChange(value: String) { _query.value = value }

    fun retry() { viewModelScope.launch { load(_query.value) } }
}