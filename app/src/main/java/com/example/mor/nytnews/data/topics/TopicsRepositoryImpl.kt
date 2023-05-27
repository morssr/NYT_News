package com.example.mor.nytnews.data.topics

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
private const val TOPICS_LAST_UPDATE_KEY_SUFFIX = "_time"

//TODO: inject logger and use it instead of default Logger
class TopicsRepositoryImpl @Inject constructor(
    private val api: TopicsService,
    private val dao: TopStoriesDao,
    private val topicsLastUpdatePreferences: DataStore<Preferences>,
    private val topicsPreferences: DataStore<Preferences>,

) : TopicsRepository {

    override suspend fun getStoryById(id: String): Story {
        Log.d(TAG, "getStoryById: called with id: $id")
        return dao.getStoryById(id).toStory()
    }

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

        saveLastSuccessfulRequestTimestamp(topic = topic, timestamp = System.currentTimeMillis())
        saveLastServerUpdateTimestamp(topic, parseDateFromString(topicResponse.last_updated).time)

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

            saveLastSuccessfulRequestTimestamp(topic = topic, timestamp = System.currentTimeMillis())

            // update last update timestamp for the topic
            saveLastServerUpdateTimestamp(
                topic,
                parseDateFromString(topicResponse.last_updated).time
            )

            val storiesFlow = dao.getTopStoriesBySectionStream(topic.topicName)
            emitAll(storiesFlow.map { Response.Success(it.toStoryList()) })
        }
    }

    // check if there is a new update for the topic
    override suspend fun isTopicUpdateAvailable(
        topic: TopicsType,
        minDurationMinute: Int
    ): Boolean {
        Log.d(TAG, "isTopicUpdateAvailable: called with topic: ${topic.topicName}")

        // get the last successful request timestamp
        val lastSuccessfulRequestTimestamp = topicsLastUpdatePreferences.data.map { preferences ->
            preferences[longPreferencesKey(topic.topicName + TOPICS_LAST_UPDATE_KEY_SUFFIX)] ?: 0L
        }.first()

        // get the last server content update timestamp
        val lastServerUpdateTimestamp = topicsLastUpdatePreferences.data.map { preferences ->
            preferences[longPreferencesKey(topic.topicName)] ?: 0L
        }.first()

        // if the last successful request timestamp is 0, it means that the topic was never updated
        if (lastSuccessfulRequestTimestamp == 0L) {
            return true
        }

        // if the last update timestamp is not 0, check if the time passed from the last update is
        // greater than the minimum duration
        val currentTime = System.currentTimeMillis()
        val timePassedFromLastUpdate = currentTime - lastSuccessfulRequestTimestamp
        val timePassedFromLastUpdateInMinutes = timePassedFromLastUpdate / 1000 / 60
        Log.d(TAG, "isTopicUpdateAvailable: timePassedFromLastUpdateInMinutes: $timePassedFromLastUpdateInMinutes")

        if (timePassedFromLastUpdateInMinutes < minDurationMinute) {
            return false
        }

        val response = api.getTopics(topic.topicName)
        if (!response.isSuccessful) {
            return false
        }

        val topicResponse = response.body() ?: return false

        //extract the last server update timestamp from the response
        val lastUpdateTimestampFromResponse = parseDateFromString(topicResponse.last_updated).time

        // check if the last update timestamp from the response is greater than the last server update timestamp
        val isUpdateAvailable = lastUpdateTimestampFromResponse > lastServerUpdateTimestamp

        // if there is no update, save the last successful request timestamp
        if (!isUpdateAvailable) {
            saveLastSuccessfulRequestTimestamp(topic = topic)
            return false
        }

        return true
    }

    /**
     * Updates the last server response timestamp for the topic
     * @param topic the topic to update
     * @param timestamp the new timestamp
     */
    private suspend fun saveLastServerUpdateTimestamp(topic: TopicsType, timestamp: Long) {
        topicsLastUpdatePreferences.edit {
            it[longPreferencesKey(topic.topicName)] = timestamp
        }
    }

    private suspend fun saveLastSuccessfulRequestTimestamp(
        topic: TopicsType,
        keySuffix: String = TOPICS_LAST_UPDATE_KEY_SUFFIX,
        timestamp: Long = System.currentTimeMillis()
    ) {
        Log.d(
            TAG,
            "saveLastSuccessfulRequestTimestamp() called with: topic = $topic, keySuffix = $keySuffix, timestamp = $timestamp"
        )
        topicsLastUpdatePreferences.edit {
            it[longPreferencesKey(topic.topicName + keySuffix)] = timestamp
        }
    }

    // get the list of topics that the user chose as favorite
    override fun getMyTopicsListStream(): Flow<List<TopicsType>> {
        Log.d(TAG, "getMyTopicsList: called")
        return topicsPreferences.data.map { preferences ->
            preferences[stringPreferencesKey(FAVORITE_TOPICS_PREFERENCES_KEY)]
                ?: defaultTopics.joinToString(separator = ",") { it.topicName }
        }.map { topicsList ->
            topicsList.split(",").map { topicName ->
                TopicsType.values().first { it.topicName == topicName }
            }
        }
    }

    // update the list of topics that the user chose as favorite
    override suspend fun updateMyTopicsList(topicsList: List<TopicsType>) {
        Log.d(TAG, "updateMyTopicsList: called with topicsList: $topicsList")
        val topicsString = topicsList.joinToString(separator = ",") { it.topicName }
        topicsPreferences.edit {
            it[stringPreferencesKey(FAVORITE_TOPICS_PREFERENCES_KEY)] = topicsString
        }
    }

}