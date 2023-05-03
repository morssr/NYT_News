package com.example.mor.nytnews.data.topics.api

import com.example.mor.nytnews.data.API_KEY
import com.example.mor.nytnews.data.TOPICS_REQUEST_BASE_URL
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TopicsService {

    @GET("$TOPICS_REQUEST_BASE_URL{topic}.json?api-key=$API_KEY")
    suspend fun getTopics(@Path(value = "topic") topic: String): Response<TopicsResponse>

    @GET("$TOPICS_REQUEST_BASE_URL{topic}.json?api-key=$API_KEY")
    suspend fun getTopicLastUpdate(@Path(value = "topic") topic: String): Response<LastTopicUpdateResponse>
}