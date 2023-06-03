package com.example.mor.nytnews.ui.bookmarks

import com.example.mor.nytnews.data.bookmarks.cache.BookmarkedStory
import com.example.mor.nytnews.utilities.api.parseDateToShortString

data class BookmarkUi(
    val id: String,
    val topic: String,
    val subsection: String,
    val title: String,
    val imageUrl: String,
    val abstract: String,
    val byline: String,
    val publishedDate: String,
    val storyUrl: String
)

fun BookmarkedStory.toBookmarkUi() = BookmarkUi(
    id = id,
    topic = topic,
    subsection = subsection,
    title = title,
    imageUrl = imageUrl,
    abstract = abstract,
    byline = byline,
    publishedDate = parseDateToShortString(publishedDate),
    storyUrl = storyUrl
)