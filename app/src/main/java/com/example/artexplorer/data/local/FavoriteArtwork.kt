package com.example.artexplorer.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteArtwork(
    @PrimaryKey val id: Int,
    val title: String?,
    val artist: String?,
    val imageId: String?,
    val addedAt: Long = System.currentTimeMillis()
)