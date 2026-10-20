package com.example.artexplorer.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.artexplorer.data.ArtApi
import com.example.artexplorer.data.Artwork
import com.example.artexplorer.data.local.AppDatabase
import com.example.artexplorer.data.local.FavoriteArtwork
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailViewModel(app: Application, handle: SavedStateHandle) : AndroidViewModel(app) {
    private val id: Int = checkNotNull(handle["id"])
    private val dao = AppDatabase.get(app).favoriteDao()

    private val _state = MutableStateFlow<UiState<Artwork>>(UiState.Loading)
    val state = _state.asStateFlow()

    val isFavorite: StateFlow<Boolean> = dao.isFavorite(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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

    fun toggleFavorite(art: Artwork) {
        viewModelScope.launch {
            if (isFavorite.value) {
                dao.delete(art.id)
            } else {
                dao.insert(
                    FavoriteArtwork(
                        id = art.id,
                        title = art.title,
                        artist = art.artistDisplay?.lines()?.firstOrNull(),
                        imageId = art.imageId
                    )
                )
            }
        }
    }
}