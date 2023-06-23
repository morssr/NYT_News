package com.example.mor.nytnews.data.popular.common

import java.util.Date

data class PopularModel(
    val id: String,
    val type: PopularType,
    val title: String,
    val abstract: String,
    val byline: String,
    val section: String,
    val publishedDate: Date,
    val imageUrl: String,
    val storyUrl: String
)