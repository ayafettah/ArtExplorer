package com.example.artexplorer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay


@Composable
fun RetryImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    var failures by remember(url) { mutableIntStateOf(0) }
    var attempt by remember(url) { mutableIntStateOf(0) }

    LaunchedEffect(failures) {
        if (failures in 1..3) {
            delay(1500L * failures)
            attempt = failures
        }
    }

    val request = remember(url, attempt) {
        ImageRequest.Builder(context)
            .data(url)
            .setParameter("attempt", attempt)
            .crossfade(true)
            .build()
    }

    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        onError = { failures++ }
    )
}