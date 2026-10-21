package com.example.artexplorer.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Pagination(
    val total: Int = 0,
    @SerialName("total_pages") val totalPages: Int = 0,
    @SerialName("current_page") val currentPage: Int = 1
)

@Serializable
data class Artwork(
    val id: Int,
    val title: String? = null,
    @SerialName("artist_display") val artistDisplay: String? = null,
    @SerialName("date_display") val dateDisplay: String? = null,
    @SerialName("medium_display") val mediumDisplay: String? = null,
    val dimensions: String? = null,
    @SerialName("place_of_origin") val placeOfOrigin: String? = null,
    val description: String? = null,
    @SerialName("image_id") val imageId: String? = null
) {
    fun imageUrl(width: Int = 843): String? =
        imageId?.let { "https://www.artic.edu/iiif/2/$it/full/$width,/0/default.jpg" }
}

@Serializable
data class ArtworksResponse(
    val pagination: Pagination = Pagination(),
    val data: List<Artwork> = emptyList()
)

@Serializable
data class ArtworkDetailResponse(val data: Artwork)

@Serializable
data class Artist(
    val id: Int,
    val title: String? = null,
    @SerialName("birth_date") val birthDate: Int? = null,
    @SerialName("death_date") val deathDate: Int? = null,
    val description: String? = null
)

@Serializable
data class ArtistsResponse(
    val pagination: Pagination = Pagination(),
    val data: List<Artist> = emptyList()
)

fun String.stripHtml(): String =
    replace(Regex("<[^>]*>"), "").replace("&nbsp;", " ").replace("&amp;", "&").trim()