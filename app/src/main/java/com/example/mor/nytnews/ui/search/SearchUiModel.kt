package com.example.mor.nytnews.ui.search

import java.util.Date

data class SearchUiModel(
    val id: String,
    val title: String,
    val abstract: String,
    val byline: String,
    val publishedDate: Date,
    val imageUrl: String,
    val bookmarked: Boolean
)