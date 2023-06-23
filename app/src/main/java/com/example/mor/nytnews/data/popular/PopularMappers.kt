package com.example.mor.nytnews.data.popular

import com.example.mor.nytnews.data.popular.api.PopularResponse
import com.example.mor.nytnews.data.popular.api.Result
import com.example.mor.nytnews.data.popular.cache.PopularEntity
import com.example.mor.nytnews.utilities.api.parseDateFromYearString

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
        imageUrl = media.map { it.media_metadata }.map { it.find { mediaMetadata -> mediaMetadata.height in 300..500 }?.url ?: "" }.first(),
        storyUrl = url
    )
}

fun List<Result>.toPopularEntityList(type: PopularType): List<PopularEntity> {
    return map { it.toPopularEntity(type) }
}