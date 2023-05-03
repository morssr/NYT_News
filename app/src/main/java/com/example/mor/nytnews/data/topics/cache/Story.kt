package com.example.mor.nytnews.data.topics.cache

import java.util.Date

data class Story(
    val subsection: String,
    val title: String,
    val abstract: String,
    val byline: String,
    val publishedDate: Date,
    val imageUrl: String,
    val storyUrl: String
)