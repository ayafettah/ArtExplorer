package com.example.artexplorer.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ArtApi {
    private const val BASE = "https://api.artic.edu/api/v1"
    private const val LIST_FIELDS = "id,title,artist_display,date_display,image_id"
    private const val DETAIL_FIELDS =
        "id,title,artist_display,date_display,medium_display,dimensions,place_of_origin,description,image_id"
    private const val ARTIST_FIELDS = "id,title,birth_date,death_date,description"

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
    }

    suspend fun getArtworks(page: Int = 1, limit: Int = 40): ArtworksResponse =
        client.get("$BASE/artworks") {
            parameter("page", page)
            parameter("limit", limit)
            parameter("fields", LIST_FIELDS)
        }.body()

    suspend fun searchArtworks(query: String, page: Int = 1, limit: Int = 40): ArtworksResponse =
        client.get("$BASE/artworks/search") {
            parameter("q", query)
            parameter("page", page)
            parameter("limit", limit)
            parameter("fields", LIST_FIELDS)
        }.body()

    suspend fun getArtwork(id: Int): Artwork =
        client.get("$BASE/artworks/$id") {
            parameter("fields", DETAIL_FIELDS)
        }.body<ArtworkDetailResponse>().data

    suspend fun getArtists(limit: Int = 40): ArtistsResponse =
        client.get("$BASE/artists") {
            parameter("limit", limit)
            parameter("fields", ARTIST_FIELDS)
        }.body()

    suspend fun searchArtists(query: String, limit: Int = 40): ArtistsResponse =
        client.get("$BASE/artists/search") {
            parameter("q", query)
            parameter("limit", limit)
            parameter("fields", ARTIST_FIELDS)
        }.body()

    suspend fun getRandomArtwork(): Artwork? =
        getArtworks(page = (1..100).random(), limit = 20)
            .data.filter { it.imageId != null }
            .randomOrNull()
}