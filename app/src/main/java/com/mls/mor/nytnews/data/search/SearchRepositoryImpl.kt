package com.mls.mor.nytnews.data.search

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import co.touchlab.kermit.Logger
import com.mls.mor.nytnews.data.AppDatabase
import com.mls.mor.nytnews.data.search.api.SearchService
import com.mls.mor.nytnews.data.search.cache.SearchEntity
import com.mls.mor.nytnews.data.search.cache.SearchModel
import com.mls.mor.nytnews.data.search.cache.SearchRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "SearchRepositoryImpl"

@OptIn(ExperimentalPagingApi::class)
class SearchRepositoryImpl(
    private val db: AppDatabase,
    private val searchService: SearchService,
    private val logger: Logger
) : SearchRepository {

    val log = logger.withTag(TAG)

    override fun searchStoriesPagingSource(query: String): Pager<Int, SearchEntity> {
        log.d { "searchStoriesPagingSource called with query: $query" }
        return Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 20),
            remoteMediator = SearchRemoteMediator(
                query = query,
                appDatabase = db,
                searchService = searchService,
                logger = logger
            ),
            pagingSourceFactory = {
                db.searchDao().searchPagingSource()
            }
        )
    }

    override fun getLastStoriesSearch(): Flow<List<SearchModel>> =
        db.searchDao().getSearchResultsStream()
            .map { it.toSearchModel() }

    override fun getInterestsList(): Flow<List<SearchModel>> = db.topStoriesDao()
        .getTopStoriesStream()
        .map { it.distinctBy { storyEntity -> storyEntity.topic } }
        .map { it.toSearchModels() }

    override fun getRecommendedList(): Flow<List<SearchModel>> = db.topStoriesDao()
        .getTopStoriesStream()
        .map { it.shuffled().take(10) }
        .map { it.toSearchModels() }

    override suspend fun getStoryById(id: String): SearchModel =
        db.searchDao().getStoryById(id).toSearchModel()
}
