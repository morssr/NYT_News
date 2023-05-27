package com.example.mor.nytnews.data.bookmarks.cache

import java.util.Date

data class BookmarkedStory(
    val id: String,
    val subsection: String,
    val title: String,
    val abstract: String,
    val byline: String,
    val publishedDate: Date,
    val bookmarkedDate: Date,
    val imageUrl: String,
    val storyUrl: String
)