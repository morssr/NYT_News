package com.example.mor.nytnews.data.topics.api.di

import com.example.mor.nytnews.data.topics.api.TopicsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
object TopicsApiModule {

    @Provides
    @ActivityRetainedScoped
    fun provideTopicsService(retrofit: Retrofit): TopicsService =
        retrofit.create(TopicsService::class.java)
}