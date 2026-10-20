package com.example.artexplorer.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.artexplorer.data.local.AppDatabase
import com.example.artexplorer.data.local.FavoriteArtwork
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).favoriteDao()

    val favorites: StateFlow<List<FavoriteArtwork>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}