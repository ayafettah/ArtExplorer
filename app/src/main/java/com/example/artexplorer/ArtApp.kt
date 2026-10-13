package com.example.artexplorer



import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.artexplorer.navigation.Screen
import com.example.artexplorer.ui.*

@Composable
fun ArtApp() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    val tabs = listOf(
        Triple(Screen.Gallery.route, "Galerie", Icons.Default.GridView),
        Triple(Screen.Artists.route, "Artistes", Icons.Default.Brush),
        Triple(Screen.Surprise.route, "Surprise", Icons.Default.AutoAwesome)
    )

    Scaffold(
        bottomBar = {
            if (current != Screen.Detail.route) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    tabs.forEach { (route, label, icon) ->
                        NavigationBarItem(
                            selected = current == route,
                            onClick = {
                                nav.navigate(route) {
                                    popUpTo(Screen.Gallery.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Screen.Gallery.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Gallery.route) {
                GalleryScreen(onArtworkClick = { nav.navigate(Screen.Detail.create(it)) })
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                DetailScreen(onBack = { nav.popBackStack() })
            }
            composable(Screen.Artists.route) { ArtistsScreen() }
            composable(Screen.Surprise.route) {
                SurpriseScreen(onArtworkClick = { nav.navigate(Screen.Detail.create(it)) })
            }
        }
    }
}
