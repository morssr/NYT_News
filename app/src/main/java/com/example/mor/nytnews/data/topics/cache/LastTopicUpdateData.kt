package com.example.mor.nytnews.data.topics.cache

import java.util.Date

data class LastTopicUpdateData(
    val topic: String,
    val lastUpdated: Date,
    val numResults: Int
)