package com.example.mor.nytnews.data.popular

import co.touchlab.kermit.Logger
import com.example.mor.nytnews.MainLogger
import com.example.mor.nytnews.data.AppDatabase
import com.example.mor.nytnews.data.popular.api.PopularService
import com.example.mor.nytnews.data.popular.cache.PopularDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit

@Module
@InstallIn(ActivityRetainedComponent::class)
object PopularModule {

    @Provides
    @ActivityRetainedScoped
    fun providePopularService(retrofit: Retrofit): PopularService =
        retrofit.create(PopularService::class.java)

    @Provides
    @ActivityRetainedScoped
    fun providePopularDao(appDatabase: AppDatabase): PopularDao = appDatabase.popularDao()

    @Provides
    @ActivityRetainedScoped
    fun providePopularRepository(
        popularService: PopularService,
        popularDao: PopularDao,
        @MainLogger logger: Logger
    ): PopularRepository =
        PopularRepositoryImpl(
            popularService,
            popularDao,
            logger
        )
}