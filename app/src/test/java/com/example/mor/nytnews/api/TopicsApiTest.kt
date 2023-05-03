package com.example.mor.nytnews.api

import com.example.mor.nytnews.data.NetworkModule
import com.example.mor.nytnews.data.topics.api.TopicsService
import com.example.mor.nytnews.utilities.server.ApiMockResponsesFactory.appMockResponses
import com.example.mor.nytnews.utilities.server.MockWebServer
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class TopicsApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: TopicsService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer(appMockResponses)
        mockWebServer.startServer()

        val serverBaseUrl = mockWebServer.getServerBaseUrl()
        println("serverBaseUrl: $serverBaseUrl")

        service = NetworkModule.provideRetrofit(serverBaseUrl)
            .create(TopicsService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.stopServer()
    }

    @Test
    fun `test get home topics success`() {
        runBlocking {
            val response = service.getTopics("home")
            assert(response.isSuccessful)
            assert(response.body() != null)
            assert(response.body()!!.status == "OK")
            assert(response.body()!!.section == "home")
        }
    }
    @Test
    fun `test get science topics success`() {
        runBlocking {
            val response = service.getTopics("science")
            assert(response.isSuccessful)
            assert(response.body() != null)
            assert(response.body()!!.status == "OK")
            assert(response.body()!!.section == "Science")
        }
    }

    @Test
    fun `test get arts topics success`() {
        runBlocking {
            val response = service.getTopics("arts")
            assert(response.isSuccessful)
            assert(response.body() != null)
            assert(response.body()!!.status == "OK")
            assert(response.body()!!.section == "Arts")
        }
    }

    @Test
    fun `test get home topics with wrong path fails`() {
        runBlocking {
            val response = service.getTopics("homee")
            assert(!response.isSuccessful)
        }
    }

    @Test
    fun `test get home topics last update success`() {
        runBlocking {
            val response = service.getTopicLastUpdate("home")
            assert(response.isSuccessful)
            assert(response.body() != null)
            assert(response.body()!!.status == "OK")
            assert(response.body()!!.section == "home")
        }
    }
}