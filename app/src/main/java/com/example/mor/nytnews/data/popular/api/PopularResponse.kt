package com.example.mor.nytnews.data.popular.api

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class PopularResponse(
    val num_results: Int,
    val results: List<Result>,
    val status: String
)

@Keep
data class Result(
    val id: Long,
    val title: String,
    val abstract: String,
    val byline: String,
    val media: List<Media>?,
    val published_date: String,
    val section: String,
    val subsection: String?,
    val uri: String,
    val url: String
)

@Keep
data class Media(
    @Json(name = "media-metadata")
    val media_metadata: List<MediaMetadata>
)

@Keep
data class MediaMetadata(
    val format: String,
    val height: Int,
    val url: String,
    val width: Int
)
