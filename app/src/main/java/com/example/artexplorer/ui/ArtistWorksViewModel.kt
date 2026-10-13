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

class ArtistWorksViewModel(handle: SavedStateHandle) : ViewModel() {
    private val id: Int = checkNotNull(handle["id"])
    val name: String = handle.get<String>("name") ?: ""

    private val _state = MutableStateFlow<UiState<List<Artwork>>>(UiState.Loading)
    val state = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = try {
                var res = ArtApi.getArtworksByArtist(id)
                if (res.data.none { it.imageId != null }) res = ArtApi.searchArtworks(name)
                UiState.Success(res.data.filter { it.imageId != null })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error("Impossible de charger les œuvres")
            }
        }
    }
}