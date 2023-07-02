package com.example.mor.nytnews.ui.topics

import com.example.mor.nytnews.data.popular.common.PopularModel
import com.example.mor.nytnews.data.popular.common.PopularType

data class PopularUi(
    val id: String,
    val type: PopularType,
    val title: String,
    val abstract: String,
    val byline: String,
    val publishedDate: String,
    val imageUrl: String,
)

fun PopularModel.toPopularUi(): PopularUi {
    return PopularUi(
        id = id,
        type = type,
        title = title,
        abstract = abstract,
        byline = byline,
        publishedDate = publishedDate.toString(),
        imageUrl = imageUrl,
    )
}

fun List<PopularModel>.toPopularUiList(): List<PopularUi> {
    return map { it.toPopularUi() }
}