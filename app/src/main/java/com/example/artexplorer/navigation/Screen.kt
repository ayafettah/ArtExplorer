package com.example.artexplorer.navigation

sealed class Screen(val route: String) {
    object Gallery : Screen("gallery")
    object Artists : Screen("artists")
    object Surprise : Screen("surprise")
    object Detail : Screen("detail/{id}") {
        fun create(id: Int) = "detail/$id"
    }
}