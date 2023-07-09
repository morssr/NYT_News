package com.example.mor.nytnews.data.search.cache

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Query("SELECT * FROM Search")
    suspend fun getSearchResults(): List<SearchEntity>

    @Query("SELECT * FROM Search WHERE id = :id")
    suspend fun getStoryById(id: String): SearchEntity

    @Query("SELECT * FROM Search")
    fun getSearchResultsStream(): Flow<List<SearchEntity>>

    @Query("SELECT * FROM Search")
    fun searchPagingSource(): PagingSource<Int, SearchEntity>

    @Upsert
    suspend fun upsert(searchResults: List<SearchEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(searchResults: List<SearchEntity>)

    @Query("DELETE FROM Search")
    suspend fun clearSearchResults()
}