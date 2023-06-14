package com.example.mor.nytnews.data.search.cache

import java.util.Date

data class SearchModel(
    val id: String,
    val abstract: String,
    val byline: String,
    val headline: String,
    val leadParagraph: String,
    val publishedDate: Date,
    val imageUrl: String,
)