package com.example.mor.nytnews.utilities.server

import com.example.mor.nytnews.data.API_KEY
import com.example.mor.nytnews.data.TOPICS_REQUEST_BASE_URL
import okhttp3.mockwebserver.MockResponse
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

object ApiMockResponsesFactory {

    private const val PAGE_LOAD_DELAY = 1000L

    private const val topHomeFileName = "topic_home.json"
    private const val topArtsFileName = "topic_arts.json"
    private const val topScienceFileName = "topic_science.json"

    @JvmStatic
    val appMockResponses = hashMapOf<String, MockResponse>().also {
        it["/${TOPICS_REQUEST_BASE_URL}home.json?api-key=$API_KEY"] = createSuccessMockResponse(
            topHomeFileName
        )

        it["/${TOPICS_REQUEST_BASE_URL}arts.json?api-key=$API_KEY"] = createSuccessMockResponse(
            topArtsFileName
        )

        it["/${TOPICS_REQUEST_BASE_URL}science.json?api-key=$API_KEY"] = createSuccessMockResponse(
            topScienceFileName
        )
    }

    private fun createSuccessMockResponse(jsonFilePath: String, delayMillis: Long = 0) =
        MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBodyDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setBody(MockResponseFileReader(jsonFilePath).content)
}