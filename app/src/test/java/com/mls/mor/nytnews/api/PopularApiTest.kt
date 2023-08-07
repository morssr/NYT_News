package com.mls.mor.nytnews.api

import com.mls.mor.nytnews.utilities.server.MockWebServer

import com.mls.mor.nytnews.data.NetworkModule
import com.mls.mor.nytnews.data.popular.api.PopularPeriod
import com.mls.mor.nytnews.data.popular.api.PopularService
import com.mls.mor.nytnews.data.popular.common.PopularType
import com.mls.mor.nytnews.utilities.server.ApiMockResponsesFactory
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class PopularApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: PopularService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer(ApiMockResponsesFactory.appMockResponses)
        mockWebServer.startServer()

        val serverBaseUrl = mockWebServer.getServerBaseUrl()

        service = NetworkModule.provideRetrofit(serverBaseUrl)
            .create(PopularService::class.java)

    }

    @After
    fun tearDown() {
        mockWebServer.stopServer()
    }

    @Test
    fun `test get most emailed success`() {
        runBlocking {
            val response = service.getPopularArticles(PopularType.MOST_EMAILED, PopularPeriod.DAY)
            assert(response.isSuccessful)
            assert(response.body() != null)
            assert(response.body()!!.status == "OK")
        }
    }

    @Test
    fun `test get most shared success`() {
        runBlocking {
            val response = service.getPopularArticles(PopularType.MOST_SHARED, PopularPeriod.DAY)
            assert(response.isSuccessful)
            assert(response.body() != null)
            assert(response.body()!!.status == "OK")
        }
    }

    @Test
    fun `test get most viewed success`() {
        runBlocking {
            val response = service.getPopularArticles(PopularType.MOST_VIEWED, PopularPeriod.DAY)
            assert(response.isSuccessful)
            assert(response.body() != null)
            assert(response.body()!!.status == "OK")
        }
    }
}