package com.mls.mor.nytnews.data.popular.common

import android.util.Log
import com.mls.mor.nytnews.data.popular.api.Media
import com.mls.mor.nytnews.data.popular.api.PopularResponse
import com.mls.mor.nytnews.data.popular.api.Result
import com.mls.mor.nytnews.data.popular.cache.PopularEntity
import com.mls.mor.nytnews.utilities.EMPTY_STRING
import com.mls.mor.nytnews.utilities.api.parseDateFromYearString

private const val TAG = "PopularMappers"

fun PopularEntity.toPopularModel(): PopularModel {
    return PopularModel(
        id = id,
        type = type,
        title = title,
        abstract = abstract,
        byline = byline,
        section = section,
        publishedDate = publishedDate,
        imageUrl = imageUrl,
        storyUrl = storyUrl
    )
}

fun List<PopularEntity>.toPopularModelList(): List<PopularModel> {
    return map { it.toPopularModel() }
}

fun PopularResponse.toPopularEntityList(type: PopularType): List<PopularEntity> {
    return results.toPopularEntityList(type)
}

fun Result.toPopularEntity(type: PopularType): PopularEntity {
    return PopularEntity(
        id = uri,
        type = type,
        title = title,
        abstract = abstract,
        byline = byline,
        section = section,
        publishedDate = parseDateFromYearString(published_date),
        imageUrl = media?.extractPopularImageUrl(PopularImageFormat.MEDIUM_THREE_BY_TWO_440)
            ?: EMPTY_STRING,
        storyUrl = url
    )
}

fun List<Result>.toPopularEntityList(type: PopularType): List<PopularEntity> {
    return map { it.toPopularEntity(type) }
}

private fun List<Media>.extractPopularImageUrl(imageFormat: PopularImageFormat): String {
    return try {
        map { it.media_metadata }
            .map {
                it.find { mediaMetadata -> mediaMetadata.format == imageFormat.format }?.url
                    ?: EMPTY_STRING
            }
            .firstOrNull() ?: EMPTY_STRING
    } catch (e: Exception) {
        Log.w(TAG, "extractPopularImageUrl: extract format $imageFormat failed.", e)
        EMPTY_STRING
    }
}

private enum class PopularImageFormat(val format: String) {
    MEDIUM_THREE_BY_TWO_440("mediumThreeByTwo440"),
    MEDIUM_THREE_BY_TWO_220("mediumThreeByTwo210"),
    STANDARD_THUMBNAIL("Standard Thumbnail");

    override fun toString(): String {
        return format
    }
}