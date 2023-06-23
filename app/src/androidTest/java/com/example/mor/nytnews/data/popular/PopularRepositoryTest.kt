package com.example.mor.nytnews.data.popular

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import co.touchlab.kermit.Logger
import com.example.mor.nytnews.data.AppDatabase
import com.example.mor.nytnews.data.NetworkModule
import com.example.mor.nytnews.data.popular.api.PopularPeriod
import com.example.mor.nytnews.data.popular.api.PopularService
import com.example.mor.nytnews.data.popular.common.PopularType
import com.example.mor.nytnews.utilities.Response
import com.example.mor.nytnews.utilities.server.ApiMockResponsesFactory
import com.example.mor.nytnews.utilities.server.MockWebServer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PopularRepositoryTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private lateinit var db: AppDatabase
    private lateinit var mockWebServer: MockWebServer
    private lateinit var popularRepository: PopularRepository

    @Before
    fun setUp() {
        // initialize db
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()

        // initialize mock web server
        mockWebServer = MockWebServer(ApiMockResponsesFactory.appMockResponses)
        mockWebServer.startServer()
        val serverBaseUrl = mockWebServer.getServerBaseUrl()

        // initialize topics retrofit api service
        val api = NetworkModule.provideRetrofit(serverBaseUrl).create(PopularService::class.java)

        // initialize topics repository
        popularRepository = PopularRepositoryImpl(
            api = api,
            dao = db.popularDao(),
            logger = Logger
        )
    }

    @After
    fun tearDown() {
        db.close()
        mockWebServer.stopServer()
    }


    @Test
    fun getMostViewedArticlesFromApi_RemoteSyncOn_CheckIsNotEmpty() = runTest {
        val viewedResponse = popularRepository.getPopularsByTypeStream(
            PopularType.MOST_VIEWED,
            PopularPeriod.DAY,
            true
        )

        when (viewedResponse) {
            is Response.Success -> {
                val viewedArticles = viewedResponse.data.first()
                assert(viewedArticles.isNotEmpty())
            }

            is Response.Failure -> {
                println("getMostViewedArticlesFromApi: ${viewedResponse.error.message}")
                assert(false) { "${viewedResponse.error.message}" }
            }
        }
    }

    @Test
    fun getMostViewedArticlesFromApi_RemoteSyncOff_CheckIsEmpty() = runTest {
        val viewedResponse = popularRepository.getPopularsByTypeStream(
            PopularType.MOST_VIEWED,
            PopularPeriod.DAY,
            false
        )

        when (viewedResponse) {
            is Response.Success -> {
                val viewedArticles = viewedResponse.data.first()
                assert(viewedArticles.isEmpty())
            }

            is Response.Failure -> {
                println("getMostViewedArticlesFromApi: ${viewedResponse.error.message}")
                assert(false) { "${viewedResponse.error.message}" }
            }
        }
    }
}