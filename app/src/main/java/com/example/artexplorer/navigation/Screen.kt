package com.example.artexplorer.navigation

import android.net.Uri
sealed class Screen(val route: String) {

    object Favorites : Screen("favorites")
    object Gallery : Screen("gallery")

    object ArtistWorks : Screen("artist/{id}/{name}") {
        fun create(id: Int, name: String) = "artist/$id/${Uri.encode(name)}"
    }
    object Artists : Screen("artists")

    object Detail : Screen("detail/{id}") {
        fun create(id: Int) = "detail/$id"
    }
}