package com.example.mor.nytnews.data.topics.api

data class LastTopicUpdateResponse(
    val status: String,
    val section: String,
    val last_updated: String,
    val num_results: Int
)