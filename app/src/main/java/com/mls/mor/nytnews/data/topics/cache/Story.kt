package com.mls.mor.nytnews.data.topics.cache

import java.util.Date

data class Story(
    val id: String,
    val subsection: String,
    val title: String,
    val abstract: String,
    val byline: String,
    val publishedDate: Date,
    val imageUrl: String,
    val storyUrl: String
)