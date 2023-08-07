package com.mls.mor.nytnews.data

import com.mls.mor.nytnews.utilities.api.NullToEmptyStringAdapter
import com.mls.mor.nytnews.utilities.server.ApiMockResponsesFactory
import com.mls.mor.nytnews.utilities.server.MockWebServer
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @RetrofitRemoteBaseUrl
    @Singleton
    @Provides
    fun provideBaseUrlForRetrofitRemote(): String = BASE_URL

    @OfflineTestMockServerBaseUrl
    @Singleton
    @Provides
    fun provideBaseUrlForOfflineMockServer(mockWebServer: MockWebServer): String = mockWebServer.getServerBaseUrl()

    @Provides
    @Singleton
    fun provideMockWebServer(): MockWebServer =
        MockWebServer(ApiMockResponsesFactory.appMockResponses)

    @Singleton
    @Provides
    fun provideRetrofit(@RetrofitRemoteBaseUrl baseUrl: String = BASE_URL): Retrofit {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC

        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        return Retrofit.Builder().addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .add(NullToEmptyStringAdapter())
                    .add(KotlinJsonAdapterFactory())
                    .build()
            )
        )
            .client(client)
            .baseUrl(baseUrl)
            .build()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitRemoteBaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OfflineTestMockServerBaseUrl