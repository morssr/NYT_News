package com.example.mor.nytnews.data.topics

import com.example.mor.nytnews.data.topics.cache.Story
import com.example.mor.nytnews.utilities.Response
import kotlinx.coroutines.flow.Flow

interface TopicsRepository {

    suspend fun getStoriesByTopic(topic: TopicsType, remoteSync: Boolean): Response<List<Story>>
    fun getStoriesByTopicStream(topic: TopicsType, remoteSync: Boolean): Flow<Response<List<Story>>>

    suspend fun isTopicUpdateAvailable(topic: TopicsType): Boolean
}