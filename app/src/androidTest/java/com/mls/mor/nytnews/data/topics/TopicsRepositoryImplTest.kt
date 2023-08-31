package com.mls.mor.nytnews.data.topics

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import co.touchlab.kermit.Logger
import com.mls.mor.nytnews.data.AppDatabase
import com.mls.mor.nytnews.data.NetworkModule
import com.mls.mor.nytnews.data.TestDispatcherRule
import com.mls.mor.nytnews.data.topics.api.TopicsService
import com.mls.mor.nytnews.utilities.ApiResponse
import com.mls.mor.nytnews.utilities.Response
import com.mls.mor.nytnews.utilities.server.ApiMockResponsesFactory
import com.mls.mor.nytnews.utilities.server.MockWebServer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TopicsRepositoryImplTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    private val testDispatcherRule = TestDispatcherRule()
    private val testCoroutineScope = TestScope(testDispatcherRule.testDispatcher)

    private lateinit var db: AppDatabase

    // initialize topics last update preferences
    private val lastUpdatePref = PreferenceDataStoreFactory.create(
        corruptionHandler = null,
        migrations = emptyList(),
        scope = testCoroutineScope,
        produceFile = { context.preferencesDataStoreFile(TOPICS_LAST_UPDATE_PREFERENCES_FILE_NAME) }
    )

    // initialize topics last update preferences
    private val topicsPref = PreferenceDataStoreFactory.create(
        corruptionHandler = null,
        migrations = emptyList(),
        scope = testCoroutineScope,
        produceFile = { context.preferencesDataStoreFile(TOPICS_PREFERENCES_FILE_NAME) }
    )
    private lateinit var mockWebServer: MockWebServer

    private lateinit var topicsRepository: TopicsRepository

    @Before
    fun setUp() {

        // initialize db
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        // initialize mock web server
        mockWebServer = MockWebServer(ApiMockResponsesFactory.appMockResponses)
        mockWebServer.startServer()
        val serverBaseUrl = mockWebServer.getServerBaseUrl()

        // initialize topics retrofit api service
        val api = NetworkModule.provideRetrofit(serverBaseUrl).create(TopicsService::class.java)

        // initialize topics repository
        topicsRepository = TopicsRepositoryImpl(
            api = api,
            dao = db.topStoriesDao(),
            topicsLastUpdatePreferences = lastUpdatePref,
            topicsPreferences = topicsPref,
            logger = Logger
        )
    }

    @After
    fun tearDown() {
        db.close()
        testCoroutineScope.runTest {
            lastUpdatePref.edit { it.clear() }
            topicsPref.edit { it.clear() }
        }
        mockWebServer.stopServer()
        testCoroutineScope.cancel()
    }

    @Test
    fun getStoriesFromRemoteBy_homeTopic_isNotEmpty() = runTest {
        when (val stories = topicsRepository.getStoriesByTopic(TopicsType.HOME, true)) {
            is Response.Success -> {
                println("Response.Success stories.data: ${stories.data.size}")
                Assert.assertTrue(stories.data.isNotEmpty())
            }

            is Response.Failure -> {
                println("Response.Failure stories.error: ${stories.error.message}")
                Assert.fail("Error: ${stories.error.message}")
            }
        }
    }

    @Test
    fun getStoriesFromCacheBy_homeTopic_isEmpty() = runTest {
        when (val stories = topicsRepository.getStoriesByTopic(TopicsType.HOME, false)) {
            is Response.Success -> {
                println("Response.Success stories.data: ${stories.data.size}")
                Assert.assertTrue(stories.data.isEmpty())
            }

            is Response.Failure -> {
                println("Response.Failure stories.error: ${stories.error.message}")
                Assert.fail("Error: ${stories.error.message}")
            }
        }
    }

    @Test
    fun getStoriesFromRemoteBy_artsTopic_isNotEmpty() = runTest {
        when (val stories = topicsRepository.getStoriesByTopic(TopicsType.ARTS, true)) {
            is Response.Success -> {
                println("Response.Success stories.data: ${stories.data.size}")
                Assert.assertTrue(stories.data.isNotEmpty())
            }

            is Response.Failure -> {
                println("Response.Failure stories.error: ${stories.error.message}")
                Assert.fail("Error: ${stories.error.message}")
            }
        }
    }

    @Test
    fun getStoriesStreamFromRemoteBy_homeTopic_isNotEmpty() = runTest {
        val storiesStream = topicsRepository.getStoriesByTopicStream(TopicsType.HOME, true)
        when (val stories = storiesStream.first()) {
            is ApiResponse.Success -> {
                println("Response.Success stories.data: ${stories.data.size}")
                Assert.assertTrue(stories.data.isNotEmpty())
            }

            is ApiResponse.Failure -> {
                println("Response.Failure stories.error: ${stories.error.message}")
                Assert.fail("Error: ${stories.error.message}")
            }
        }
    }

    @Test
    fun getStoriesStreamFromRemoteBy_magazineTopic_isFail() = runTest {
        val storiesStream = topicsRepository.getStoriesByTopicStream(TopicsType.MAGAZINE, true)
        when (val stories = storiesStream.first()) {
            is ApiResponse.Success -> {
                println("Response.Success stories.data: ${stories.data.size}")
                Assert.assertTrue(stories.data.isNotEmpty())
            }

            is ApiResponse.Failure -> {
                println("Response.Failure stories.error: ${stories.error.message}")
                Assert.assertTrue(stories.error.message!!.contains("Client Error"))
            }
        }
    }

    @Test
    fun getIsTopicUpdateAvailable_returnTrue() = runTest {
        when(val isUpdateAvailable = topicsRepository.isTopicUpdateAvailable(TopicsType.HOME)) {
            is ApiResponse.Success -> {
                println("Response.Success isUpdateAvailable.data: ${isUpdateAvailable.data}")
                Assert.assertTrue(isUpdateAvailable.data)
            }

            is ApiResponse.Failure -> {
                println("Response.Failure isUpdateAvailable.error: ${isUpdateAvailable.error.message}")
                Assert.fail("Error: ${isUpdateAvailable.error.message}")
            }
        }
    }

    @Test
    fun requestStories_getIsTopicUpdateAvailable_returnFalse() = runTest {
        val stories = topicsRepository.getStoriesByTopic(TopicsType.HOME, true)
        Assert.assertTrue((stories as Response.Success).data.isNotEmpty())

        // after requesting stories, isUpdateAvailable should return false
        val isUpdateAvailable = topicsRepository.isTopicUpdateAvailable(TopicsType.HOME)
        Assert.assertTrue((isUpdateAvailable as ApiResponse.Success).data.not())
    }
}