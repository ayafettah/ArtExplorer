package com.example.artexplorer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artexplorer.data.ArtApi
import com.example.artexplorer.data.Artwork
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GalleryState(
    val items: List<Artwork> = emptyList(),
    val loading: Boolean = true,
    val loadingMore: Boolean = false,
    val error: String? = null,
    val endReached: Boolean = false
)

@OptIn(FlowPreview::class)
class GalleryViewModel : ViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _state = MutableStateFlow(GalleryState())
    val state = _state.asStateFlow()

    private var page = 1
    private var loadJob: Job? = null

    init {
        reload()
        viewModelScope.launch {
            _query.drop(1).debounce(400).distinctUntilChanged().collect { reload() }
        }
    }

    fun onQueryChange(value: String) { _query.value = value }

    fun retry() = reload()

    private fun reload() {
        loadJob?.cancel()
        page = 1
        _state.value = GalleryState(loading = true)
        loadJob = viewModelScope.launch { fetch() }
    }

    fun loadMore() {
        val s = _state.value
        if (s.loading || s.loadingMore || s.endReached) return
        _state.value = s.copy(loadingMore = true)
        loadJob = viewModelScope.launch { fetch() }
    }

    private suspend fun fetch() {
        val q = _query.value.trim()
        try {
            val res = if (q.isBlank()) ArtApi.getArtworks(page)
            else ArtApi.searchArtworks(q, page)
            val newItems = res.data.filter { it.imageId != null }
            page++
            _state.value = _state.value.copy(
                items = (_state.value.items + newItems).distinctBy { it.id },
                loading = false,
                loadingMore = false,
                error = null,
                endReached = res.data.isEmpty() ||
                        res.pagination.currentPage >= res.pagination.totalPages
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val empty = _state.value.items.isEmpty()
            _state.value = _state.value.copy(
                loading = false,
                loadingMore = false,
                error = if (empty) "Impossible de charger les œuvres" else null
            )
        }
    }
}