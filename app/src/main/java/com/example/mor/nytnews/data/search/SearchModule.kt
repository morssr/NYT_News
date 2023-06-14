package com.example.mor.nytnews.data.search

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import co.touchlab.kermit.Logger
import com.example.mor.nytnews.MainLogger
import com.example.mor.nytnews.data.AppDatabase
import com.example.mor.nytnews.data.search.api.SearchService
import com.example.mor.nytnews.data.search.cache.SearchDao
import com.example.mor.nytnews.data.search.cache.SearchEntity
import com.example.mor.nytnews.data.search.cache.SearchRemoteKeysDao
import com.example.mor.nytnews.data.search.cache.SearchRemoteMediator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit

@OptIn(ExperimentalPagingApi::class)
@Module
@InstallIn(ActivityRetainedComponent::class)
object SearchModule {

    @Provides
    @ActivityRetainedScoped
    fun provideSearchService(retrofit: Retrofit): SearchService =
        retrofit.create(SearchService::class.java)

    @Provides
    @ActivityRetainedScoped
    fun provideSearchDao(database: AppDatabase): SearchDao =
        database.searchDao()

    @Provides
    @ActivityRetainedScoped
    fun provideSearchRemoteKeysDao(database: AppDatabase): SearchRemoteKeysDao =
        database.searchRemoteKeysDao()

    @Provides
    @ActivityRetainedScoped
    fun provideSearchRepository(
        appDatabase: AppDatabase,
        searchService: SearchService,
        @MainLogger logger: Logger
    ): SearchRepository =
        SearchRepositoryImpl(
            db = appDatabase,
            searchService = searchService,
            logger = logger
        )

    @Provides
    @ActivityRetainedScoped
    fun provideSearchPager(
        appDatabase: AppDatabase,
        searchService: SearchService,
        @MainLogger logger: Logger
    ): Pager<Int, SearchEntity> =
        Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 20),
            remoteMediator = SearchRemoteMediator(
                query = "",
                appDatabase = appDatabase,
                searchService = searchService,
                logger = logger
            ),
            pagingSourceFactory = {
                appDatabase.searchDao().searchPagingSource()
            }
        )
}