package com.example.artexplorer.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.timeout
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap


object ArtApi {
    private const val BASE = "https://api.artic.edu/api/v1"
    private const val LIST_FIELDS = "id,title,artist_display,date_display,image_id"
    private const val DETAIL_FIELDS =
        "id,title,artist_display,date_display,medium_display,dimensions,place_of_origin,description,image_id"
    private const val ARTIST_FIELDS = "id,title,birth_date,death_date,description"


    private val cache = ConcurrentHashMap<Int, Artwork>()

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }

        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }


    }


    private suspend inline fun <reified T> fetch(
        path: String,
        crossinline config: HttpRequestBuilder.() -> Unit
    ): T {
        var lastError: Exception? = null
        repeat(3) { attempt ->
            try {
                val res = client.get("$BASE/$path") { config() }
                if (res.status.isSuccess()) return res.body<T>()
                lastError = IOException("HTTP ${res.status.value}")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                lastError = e
            }
            if (attempt < 2) delay(800L * (attempt + 1))
        }
        throw lastError ?: IOException("Erreur réseau")
    }

    private fun ArtworksResponse.remember(): ArtworksResponse {
        data.forEach { cache[it.id] = it }
        return this
    }

    suspend fun getArtworks(page: Int = 1, limit: Int = 20): ArtworksResponse =
        fetch<ArtworksResponse>("artworks") {
            parameter("page", page)
            parameter("limit", limit)
            parameter("fields", LIST_FIELDS)
        }.remember()

    suspend fun searchArtworks(query: String, page: Int = 1, limit: Int = 40): ArtworksResponse =
        fetch<ArtworksResponse>("artworks/search") {
            parameter("q", query)
            parameter("page", page)
            parameter("limit", limit)
            parameter("fields", LIST_FIELDS)
        }.remember()

    suspend fun getArtworksByType(type: String, page: Int = 1, limit: Int = 20): ArtworksResponse {
        val res = fetch<ArtworksResponse>("artworks/search") {
            parameter("query[match_phrase][artwork_type_title]", type)
            parameter("page", page)
            parameter("limit", limit)
            parameter("fields", LIST_FIELDS)
        }.remember()

        return if (res.data.isEmpty() && page == 1) searchArtworks(type, page, limit) else res
    }

    suspend fun getArtworksByArtist(artistId: Int, page: Int = 1, limit: Int = 40): ArtworksResponse =
        fetch<ArtworksResponse>("artworks/search") {
            parameter("query[term][artist_id]", artistId)
            parameter("page", page)
            parameter("limit", limit)
            parameter("fields", LIST_FIELDS)
        }.remember()

    suspend fun getArtwork(id: Int): Artwork {
        // 1) route de détail
        repeat(2) { attempt ->
            try {
                val res = client.get("$BASE/artworks/$id") {
                    parameter("fields", DETAIL_FIELDS)
                    timeout { requestTimeoutMillis = 10_000 }
                }
                if (res.status.isSuccess()) {
                    val art = res.body<ArtworkDetailResponse>().data
                    cache[id] = art
                    return art
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {

            }
            if (attempt == 0) delay(500)
        }


        try {
            val res = client.get("$BASE/artworks/search") {
                parameter("query[term][id]", id)
                parameter("limit", 1)
                parameter("fields", DETAIL_FIELDS)
                timeout { requestTimeoutMillis = 10_000 }
            }
            if (res.status.isSuccess()) {
                val art = res.body<ArtworksResponse>().data.firstOrNull()
                if (art != null) {
                    cache[id] = art
                    return art
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {

        }


        cache[id]?.let { return it }

        throw IOException("Œuvre indisponible pour le moment")
    }

    suspend fun getArtists(limit: Int = 40): ArtistsResponse =
        fetch("artists") {
            parameter("limit", limit)
            parameter("fields", ARTIST_FIELDS)
        }

    suspend fun searchArtists(query: String, limit: Int = 40): ArtistsResponse =
        fetch("artists/search") {
            parameter("q", query)
            parameter("limit", limit)
            parameter("fields", ARTIST_FIELDS)
        }
}