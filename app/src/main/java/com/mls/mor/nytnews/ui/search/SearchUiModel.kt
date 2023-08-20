package com.mls.mor.nytnews.ui.search

import java.util.Date

data class SearchUiModel(
    val id: String,
    val title: String,
    val abstract: String,
    val byline: String,
    val publishedDate: Date,
    val bookmarked: Boolean,
    val imageUrl: String,
    val storyUrl: String
)