package com.mls.mor.nytnews.data.topics

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import com.mls.mor.nytnews.data.topics.api.TopicsService
import com.mls.mor.nytnews.data.topics.cache.Story
import com.mls.mor.nytnews.data.topics.cache.TopStoriesDao
import com.mls.mor.nytnews.utilities.ApiResponse
import com.mls.mor.nytnews.utilities.BadRequestException
import com.mls.mor.nytnews.utilities.BadResponseException
import com.mls.mor.nytnews.utilities.Response
import com.mls.mor.nytnews.utilities.api.parseDateFromString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

private const val TAG = "TopicsRepositoryImpl"
private const val TOPICS_LAST_UPDATE_KEY_SUFFIX = "_time"

class TopicsRepositoryImpl @Inject constructor(
    private val api: TopicsService,
    private val dao: TopStoriesDao,
    private val topicsLastUpdatePreferences: DataStore<Preferences>,
    private val topicsPreferences: DataStore<Preferences>,
    logger: Logger
) : TopicsRepository {

    private val log = logger.withTag(TAG)

    override suspend fun getStoryById(id: String): Story {
        log.d { "getStoryById(): called with id: $id" }
        return dao.getStoryById(id).toStory()
    }

    override suspend fun getStoriesByTopic(
        topic: TopicsType,
        remoteSync: Boolean
    ): Response<List<Story>> {
        log.d {
            "getStoriesByTopic(): called with topic: ${topic.topicName} and remoteSync: $remoteSync"
        }
        if (!remoteSync) {
            log.d { "remote sync is false, emit from local cached stories" }
            return Response.Success(dao.getTopStoriesByTopic(topic.topicName).toStoryList())
        }

        val response = api.getTopics(topic.topicName)

        if (!response.isSuccessful) {
            return Response.Failure(Exception(response.message()))
        }

        val topicResponse =
            response.body() ?: return Response.Failure(Exception("Response body is null"))

        // delete all stories from the table
        dao.deleteAllByTopic(topic.topicName)
        // insert new stories to the table
        val storiesEntities = topicResponse.toStoryEntityList(topic)
        dao.insertOrReplace(storiesEntities)

        saveLastSuccessfulRequestTimestamp(topic = topic, timestamp = System.currentTimeMillis())
        saveLastServerUpdateTimestamp(topic, parseDateFromString(topicResponse.last_updated).time)

        val stories = dao.getTopStoriesByTopic(topic.topicName).toStoryList()
        return Response.Success(stories)
    }

    //TODO fix Flow exception transparency violation, see https://stackoverflow.com/questions/75295355/emit-exception-in-kotlin-flow-android
    override fun getStoriesByTopicStream(
        topic: TopicsType,
        remoteSync: Boolean
    ): Flow<ApiResponse<List<Story>>> {
        log.d { "getStoriesByTopicStream(): called with topic: $topic and remoteSync: $remoteSync" }
        return flow {

            if (!remoteSync) {
                log.d { "remote sync is false, emit from local cached stories" }
                val cachedStoriesFlow = dao.getTopStoriesBySectionTopic(topic.topicName)
                emitAll(cachedStoriesFlow.map { ApiResponse.Success(it.toStoryList()) })
                return@flow
            }

            val result = try {
                val response = api.getTopics(topic.topicName)
                if (!response.isSuccessful) {
                    throw BadRequestException(response.message())
                }

                val topicResponse =
                    response.body()
                        ?: throw BadResponseException("Response body is null")

                log.d {
                    "response is successful with results size: ${topicResponse.results.size}"
                }

                // delete all stories from the table
                dao.deleteAllByTopic(topic.topicName)
                log.v { "all $topic stories are deleted" }
                // insert new stories to the table
                val storiesEntities = topicResponse.toStoryEntityList(topic)
                dao.insertOrReplace(storiesEntities)

                saveLastSuccessfulRequestTimestamp(
                    topic = topic,
                    timestamp = System.currentTimeMillis()
                )

                // update last update timestamp for the topic
                saveLastServerUpdateTimestamp(
                    topic,
                    parseDateFromString(topicResponse.last_updated).time
                )

                val entities = dao.getTopStoriesByTopic(topic.topicName)
                ApiResponse.Success(entities.toStoryList())

            } catch (e: Exception) {
                log.e(e) { "getStoriesByTopicStream(): exception occurred" }
                ApiResponse.Failure(
                    error = e,
                    fallbackData = dao.getTopStoriesByTopic(topic.name).toStoryList()
                )
            }
            emit(result)
        }
    }

    override suspend fun refreshStoriesByTopic(topic: TopicsType): ApiResponse<Unit> {
        log.d { "refreshStoriesByTopic(): called with topic: ${topic.topicName}" }
        return try {
            val response = api.getTopics(topic.topicName)
            if (!response.isSuccessful) {
                throw BadRequestException(response.message())
            }

            val topicResponse =
                response.body() ?: throw BadResponseException("Response body is null")

            // delete all stories from the table
            dao.deleteAllByTopic(topic.topicName)
            // insert new stories to the table
            val storiesEntities = topicResponse.toStoryEntityList(topic)
            dao.insertOrReplace(storiesEntities)

            saveLastSuccessfulRequestTimestamp(topic = topic, timestamp = System.currentTimeMillis())
            saveLastServerUpdateTimestamp(topic, parseDateFromString(topicResponse.last_updated).time)

            ApiResponse.Success(Unit)
        } catch (e: Exception) {
            log.e(e) { "refreshStoriesByTopic(): exception occurred" }
            ApiResponse.Failure(error = e)
        }
    }

    // check if there is a new update for the topic
    @Throws
    override suspend fun isTopicUpdateAvailable(
        topic: TopicsType,
        minDurationMinute: Int
    ): ApiResponse<Boolean> {
        log.d { "isTopicUpdateAvailable(): called with topic: ${topic.topicName}" }

        // get the last successful request timestamp
        val lastSuccessfulRequestTimestamp = topicsLastUpdatePreferences.data.map { preferences ->
            preferences[longPreferencesKey(topic.topicName + TOPICS_LAST_UPDATE_KEY_SUFFIX)] ?: 0L
        }.first()

        // if the last successful request timestamp is 0, it means that the topic was never updated
        if (lastSuccessfulRequestTimestamp == 0L) {
            log.d { "last successful request is 0. update is required." }
            return ApiResponse.Success(true)
        }

        log.v { "last topic successful remote request time ${Date(lastSuccessfulRequestTimestamp)}" }

        // if the last update timestamp is not 0, check if the time passed from the last update is
        // greater than the minimum duration
        val currentTime = System.currentTimeMillis()
        val timePassedFromLastUpdate = currentTime - lastSuccessfulRequestTimestamp
        val timePassedFromLastUpdateInMinutes = timePassedFromLastUpdate / 1000 / 60

        if (timePassedFromLastUpdateInMinutes < minDurationMinute) {
            log.d { "time passed from last update is less than the minimum duration" }
            return ApiResponse.Success(false)
        }

        val response = try {
            api.getTopics(topic.topicName)
        } catch (e: Exception) {
            log.e(e) { "request for last topic: $topic update is not successful" }
            return ApiResponse.Failure(false, e)
        }

        if (!response.isSuccessful) {
            log.d { "request for last topic: $topic update is not successful" }
            return ApiResponse.Failure(false, BadResponseException(response.message()))
        }

        val topicResponse = response.body() ?: return ApiResponse.Failure(false, BadResponseException("Response body is null"))

        //extract the last server update timestamp from the response
        val lastUpdateTimestampFromResponse = parseDateFromString(topicResponse.last_updated).time

        log.v { "last topic remote update ${Date(lastUpdateTimestampFromResponse)}" }

        // get the last server content update timestamp
        val lastServerUpdateTimestamp = topicsLastUpdatePreferences.data.map { preferences ->
            preferences[longPreferencesKey(topic.topicName)] ?: 0L
        }.first()

        log.v { "last local topic remote update ${Date(lastServerUpdateTimestamp)}" }

        // check if the last update timestamp from the response is greater than the last server update timestamp
        val isUpdateAvailable = lastUpdateTimestampFromResponse > lastServerUpdateTimestamp

        // if there is no update, save the last successful request timestamp
        if (!isUpdateAvailable) {
            log.d { "no update available for topic: $topic" }
            saveLastSuccessfulRequestTimestamp(topic = topic)
            return ApiResponse.Success(false)
        }

        log.d { "update is available for topic: $topic" }
        return ApiResponse.Success(true)
    }

    /**
     * Updates the last server response timestamp for the topic
     * @param topic the topic to update
     * @param timestamp the new timestamp
     */
    private suspend fun saveLastServerUpdateTimestamp(topic: TopicsType, timestamp: Long) {
        log.d { "saveLastServerUpdateTimestamp() called with: topic = $topic, timestamp = $timestamp | time: ${Date(timestamp)}" }
        topicsLastUpdatePreferences.edit {
            it[longPreferencesKey(topic.topicName)] = timestamp
        }
    }

    private suspend fun saveLastSuccessfulRequestTimestamp(
        topic: TopicsType,
        keySuffix: String = TOPICS_LAST_UPDATE_KEY_SUFFIX,
        timestamp: Long = System.currentTimeMillis()
    ) {
        log.d {
            "saveLastSuccessfulRequestTimestamp() called with: topic = $topic, keySuffix = $keySuffix, timestamp = $timestamp | time: ${Date(timestamp)}"
        }
        topicsLastUpdatePreferences.edit {
            it[longPreferencesKey(topic.topicName + keySuffix)] = timestamp
        }
    }

    // get the list of topics that the user chose as favorite
    override fun getMyTopicsListStream(): Flow<List<TopicsType>> {
        log.d { "getMyTopicsList: called" }
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
        log.d { "updateMyTopicsList: called with topicsList: $topicsList" }
        val topicsString = topicsList.joinToString(separator = ",") { it.topicName }
        topicsPreferences.edit {
            it[stringPreferencesKey(FAVORITE_TOPICS_PREFERENCES_KEY)] = topicsString
        }
    }
}