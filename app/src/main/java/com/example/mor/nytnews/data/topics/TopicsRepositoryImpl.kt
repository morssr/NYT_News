package com.example.mor.nytnews.data.topics

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.example.mor.nytnews.data.topics.api.TopicsService
import com.example.mor.nytnews.data.topics.cache.Story
import com.example.mor.nytnews.data.topics.cache.TopStoriesDao
import com.example.mor.nytnews.utilities.Response
import com.example.mor.nytnews.utilities.api.parseDateFromString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "TopicsRepositoryImpl"

//TODO: inject logger and use it instead of default Logger
class TopicsRepositoryImpl @Inject constructor(
    private val api: TopicsService,
    private val dao: TopStoriesDao,
    private val topicsLastUpdatePreferences: DataStore<Preferences>
) : TopicsRepository {

    override suspend fun getStoriesByTopic(
        topic: TopicsType,
        remoteSync: Boolean
    ): Response<List<Story>> {
        Log.d(
            TAG,
            "getStoriesByTopic: called with topic: ${topic.topicName} and remoteSync: $remoteSync"
        )
        if (!remoteSync) {
            return Response.Success(dao.getTopStoriesBySection(topic.topicName).toStoryList())
        }

        val response = api.getTopics(topic.topicName)

        if (!response.isSuccessful) {
            return Response.Failure(Exception(response.message()))
        }

        val topicResponse =
            response.body() ?: return Response.Failure(Exception("Response body is null"))

        // delete all stories from the table
        dao.deleteAllBySection(topic.topicName)
        // insert new stories to the table
        val storiesEntities = topicResponse.toStoryEntityList()
        dao.insertOrReplace(storiesEntities)

        updateLastUpdateTimestamp(topic, parseDateFromString(topicResponse.last_updated).time)

        val stories = dao.getTopStoriesBySection(topic.topicName).toStoryList()
        return Response.Success(stories)
    }

    override fun getStoriesByTopicStream(
        topic: TopicsType,
        remoteSync: Boolean
    ): Flow<Response<List<Story>>> {
        Log.d(TAG, "getStoriesByTopicStream: called with topic: $topic and remoteSync: $remoteSync")
        return flow {
            if (!remoteSync) {
                val cachedStoriesFlow = dao.getTopStoriesBySectionStream(topic.topicName)
                emitAll(cachedStoriesFlow.map { Response.Success(it.toStoryList()) })
                Log.v(TAG, "getStoriesByTopicStream: emitting cached stories")
                return@flow
            }

            val response = api.getTopics(topic.topicName)
            if (!response.isSuccessful) {
                emit(Response.Failure(Exception(response.message())))
                Log.v(TAG, "getStoriesByTopicStream: response is not successful")
                return@flow
            }

            val topicResponse =
                response.body()
                    ?: return@flow emit(Response.Failure(Exception("Response body is null")))

            Log.v(
                TAG,
                "getStoriesByTopicStream: response is successful with results size: ${topicResponse.results.size}"
            )

            // delete all stories from the table
            dao.deleteAllBySection(topic.topicName)
            // insert new stories to the table
            val storiesEntities = topicResponse.toStoryEntityList()
            dao.insertOrReplace(storiesEntities)

            // update last update timestamp for the topic
            updateLastUpdateTimestamp(topic, parseDateFromString(topicResponse.last_updated).time)

            val storiesFlow = dao.getTopStoriesBySectionStream(topic.topicName)
            emitAll(storiesFlow.map { Response.Success(it.toStoryList()) })
        }
    }

    // check if there is a new update for the topic
    override suspend fun isTopicUpdateAvailable(topic: TopicsType): Boolean {
        Log.d(TAG, "isTopicUpdateAvailable: called with topic: ${topic.topicName}")
        val lastUpdateTimestamp = topicsLastUpdatePreferences.data.map { preferences ->
            preferences[longPreferencesKey(topic.topicName)] ?: 0L
        }.first()

        val response = api.getTopics(topic.topicName)
        if (!response.isSuccessful) {
            return false
        }

        val topicResponse =
            response.body() ?: return false

        val lastUpdateTimestampFromResponse =
            parseDateFromString(topicResponse.last_updated).time

        return lastUpdateTimestampFromResponse > lastUpdateTimestamp
    }

    /**
     * Updates the last update timestamp for the topic
     * @param topic the topic to update
     * @param timestamp the new timestamp
     */
    private suspend fun updateLastUpdateTimestamp(topic: TopicsType, timestamp: Long) {
        topicsLastUpdatePreferences.edit {
            it[longPreferencesKey(topic.topicName)] = timestamp
        }
    }
}