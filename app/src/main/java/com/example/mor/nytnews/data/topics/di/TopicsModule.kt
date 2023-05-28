package com.example.mor.nytnews.data.topics.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import co.touchlab.kermit.Logger
import com.example.mor.nytnews.MainLogger
import com.example.mor.nytnews.data.AppDatabase
import com.example.mor.nytnews.data.topics.TOPICS_LAST_UPDATE_PREFERENCES_FILE_NAME
import com.example.mor.nytnews.data.topics.TOPICS_PREFERENCES_FILE_NAME
import com.example.mor.nytnews.data.topics.TopicsRepository
import com.example.mor.nytnews.data.topics.TopicsRepositoryImpl
import com.example.mor.nytnews.data.topics.api.TopicsService
import com.example.mor.nytnews.data.topics.cache.TopStoriesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit
import javax.inject.Qualifier

@Module
@InstallIn(ActivityRetainedComponent::class)
object TopicsModule {

    @TopicsPreferences
    @Provides
    @ActivityRetainedScoped
    fun provideTopicsPreferences(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(TOPICS_PREFERENCES_FILE_NAME)
        }

    @TopicsLastUpdatePref
    @Provides
    @ActivityRetainedScoped
    fun provideTopicsLastUpdatePref(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(TOPICS_LAST_UPDATE_PREFERENCES_FILE_NAME)
        }

    @Provides
    @ActivityRetainedScoped
    fun provideTopicsService(retrofit: Retrofit): TopicsService =
        retrofit.create(TopicsService::class.java)

    @Provides
    @ActivityRetainedScoped
    fun provideTopicsDao(database: AppDatabase): TopStoriesDao =
        database.topStoriesDao()

    @Provides
    @ActivityRetainedScoped
    fun provideTopicsRepository(
        topicsService: TopicsService,
        topicDao: TopStoriesDao,
        @TopicsLastUpdatePref topicsLastUpdatePref: DataStore<Preferences>,
        @TopicsPreferences topicsPreferences: DataStore<Preferences>,
        @MainLogger logger: Logger
    ): TopicsRepository =
        TopicsRepositoryImpl(
            topicsService,
            topicDao,
            topicsLastUpdatePref,
            topicsPreferences,
            logger
        )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TopicsLastUpdatePref

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TopicsPreferences