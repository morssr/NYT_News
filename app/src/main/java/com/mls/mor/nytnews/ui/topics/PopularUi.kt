package com.mls.mor.nytnews.ui.topics

import com.mls.mor.nytnews.data.popular.common.PopularModel
import com.mls.mor.nytnews.data.popular.common.PopularType

data class PopularUi(
    val id: String,
    val type: PopularType,
    val title: String,
    val abstract: String,
    val byline: String,
    val publishedDate: String,
    val imageUrl: String,
    val storyUrl: String,
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
        storyUrl = storyUrl
    )
}

fun List<PopularModel>.toPopularUiList(): List<PopularUi> {
    return map { it.toPopularUi() }
}