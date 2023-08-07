package com.mls.mor.nytnews.data.search

import androidx.paging.Pager
import com.mls.mor.nytnews.data.search.cache.SearchEntity
import com.mls.mor.nytnews.data.search.cache.SearchModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchStoriesPagingSource(query: String): Pager<Int, SearchEntity>
    fun getLastStoriesSearch(): Flow<List<SearchModel>>
    fun getInterestsList(): Flow<List<SearchModel>>
    fun getRecommendedList(): Flow<List<SearchModel>>
    suspend fun getStoryById(id: String): SearchModel
}