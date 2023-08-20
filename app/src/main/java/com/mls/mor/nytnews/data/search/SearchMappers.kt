package com.mls.mor.nytnews.data.search


import com.mls.mor.nytnews.data.search.api.SearchStoryResponse
import com.mls.mor.nytnews.data.search.cache.SearchEntity
import com.mls.mor.nytnews.data.search.cache.SearchModel
import com.mls.mor.nytnews.data.topics.cache.StoryEntity
import com.mls.mor.nytnews.ui.search.SearchUiModel
import com.mls.mor.nytnews.utilities.api.parseDateFromString

private const val IMAGE_PREFIX = "https://static01.nyt.com/"

fun SearchStoryResponse.toSearchEntity(): SearchEntity {
    return SearchEntity(
        id = _id,
        headline = headline.main,
        abstract = abstract,
        leadParagraph = lead_paragraph,
        subsection = subsection_name,
        byline = byline.original,
        publishedDate = parseDateFromString(pub_date),
        imageUrl = multimedia?.find { it.height in 300..500 }?.url?.let { IMAGE_PREFIX + it } ?: "",
        storyUrl = web_url
    )
}

fun SearchEntity.toSearchUiModel(bookmarked: Boolean = false): SearchUiModel {
    return SearchUiModel(
        id = id,
        title = headline,
        abstract = abstract,
        byline = byline,
        publishedDate = publishedDate,
        bookmarked = bookmarked,
        imageUrl = imageUrl,
        storyUrl = storyUrl
    )
}

fun SearchEntity.toSearchModel(): SearchModel {
    return SearchModel(
        id = id,
        headline = headline,
        abstract = abstract,
        leadParagraph = leadParagraph,
        byline = byline,
        publishedDate = publishedDate,
        imageUrl = imageUrl,
        storyUrl = storyUrl
    )
}

fun List<SearchEntity>.toSearchModel(): List<SearchModel> {
    return map { it.toSearchModel() }
}

fun SearchModel.toSearchUiModel(bookmarked: Boolean): SearchUiModel {
    return SearchUiModel(
        id = id,
        title = headline,
        abstract = abstract,
        byline = byline,
        publishedDate = publishedDate,
        bookmarked = bookmarked,
        imageUrl = imageUrl,
        storyUrl = storyUrl
    )
}

fun List<SearchModel>.toSearchUiModel(bookmarked: List<String> = emptyList()): List<SearchUiModel> {
    return map { it.toSearchUiModel(bookmarked.contains(it.id)) }
}

fun StoryEntity.toSearchModel(): SearchModel {
    return SearchModel(
        id = id,
        headline = title,
        abstract = abstract,
        leadParagraph = "",
        byline = byline,
        publishedDate = publishedDate,
        imageUrl = imageUrl,
        storyUrl = storyUrl
    )
}

fun List<StoryEntity>.toSearchModels(): List<SearchModel> {
    return map { it.toSearchModel() }
}