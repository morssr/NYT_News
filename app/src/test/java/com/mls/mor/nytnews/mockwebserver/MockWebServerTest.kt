package com.mls.mor.nytnews.mockwebserver

import com.mls.mor.nytnews.utilities.api.NullToEmptyStringAdapter
import com.mls.mor.nytnews.utilities.server.MockWebServer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.net.HttpURLConnection

private const val SUCCESS_TEST_URL = "/test/url/success"
private const val FAILURE_TEST_URL = "/test/url/failure"
private const val BAD_RESPONSE_TEST_URL = "/test/url/bad-response"

class MockWebServerTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: MockWebServerTestInterface

    private val testResponses = hashMapOf<String, MockResponse>().also {
        it[SUCCESS_TEST_URL] = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""{"status":"OK","message":"test succeed"}""")

        it[FAILURE_TEST_URL] = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody("""{"status":"ERROR","message":"test failed"}""")

        it[BAD_RESPONSE_TEST_URL] = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            .setBody("""{"status":"ERROR","message":"test failed"}""")
    }

    @Before
    fun setUp() {
        mockWebServer = MockWebServer(testResponses)
        mockWebServer.startServer()

        val serverBaseUrl = mockWebServer.getServerBaseUrl()
        println("serverBaseUrl: $serverBaseUrl")

        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()


        service = Retrofit.Builder()
            .baseUrl(serverBaseUrl)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(NullToEmptyStringAdapter())
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .client(client)
            .build()
            .create(MockWebServerTestInterface::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.stopServer()
    }

    @Test
    fun `test mock web server successful response`() = runBlocking {
        val response = service.getSuccessfulResponse()
        assert(response.isSuccessful)
    }

    @Test
    fun `test mock web server unsuccessful response`() = runBlocking {
        val response = service.getBadResponse()
        assert(!response.isSuccessful)
    }


    @Test
    fun `test mock web server response result status OK`() = runBlocking {
        val response = service.getSuccessfulResponse()
        val result = response.body()
        assert(result?.status == "OK")
    }

    @Test
    fun `test mock web server response result status ERROR`() = runBlocking {
        val response = service.getUnsuccessfulResponse()
        val result = response.body()
        assert(result?.status == "ERROR")
    }
}

interface MockWebServerTestInterface {
    @GET(SUCCESS_TEST_URL)
    suspend fun getSuccessfulResponse(): Response<TestResponse>

    @GET(FAILURE_TEST_URL)
    suspend fun getUnsuccessfulResponse(): Response<TestResponse>

    @GET(BAD_RESPONSE_TEST_URL)
    suspend fun getBadResponse(): Response<TestResponse>
}

data class TestResponse(
    val status: String,
    val message: String
)