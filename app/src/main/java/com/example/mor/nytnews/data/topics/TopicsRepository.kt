package com.example.mor.nytnews.data.topics

import com.example.mor.nytnews.data.topics.cache.Story
import com.example.mor.nytnews.utilities.Response
import kotlinx.coroutines.flow.Flow

interface TopicsRepository {
    suspend fun getStoryById(id: String): Story

    suspend fun getStoriesByTopic(topic: TopicsType, remoteSync: Boolean): Response<List<Story>>
    fun getStoriesByTopicStream(topic: TopicsType, remoteSync: Boolean): Flow<Response<List<Story>>>

    fun getMyTopicsListStream(): Flow<List<TopicsType>>

    suspend fun updateMyTopicsList(topicsList: List<TopicsType>)

    suspend fun isTopicUpdateAvailable(topic: TopicsType, minDurationMinute: Int = 30): Boolean
}