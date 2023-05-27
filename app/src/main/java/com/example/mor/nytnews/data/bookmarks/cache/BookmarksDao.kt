package com.example.mor.nytnews.data.bookmarks.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarksDao {

    @Query("SELECT * FROM bookmarks")
    suspend fun getBookmarks(): List<BookmarkedStoryEntity>

    @Query("SELECT * FROM bookmarks")
    fun getBookmarksStream(): Flow<List<BookmarkedStoryEntity>>

    @Query("SELECT * FROM bookmarks WHERE id = :id")
    suspend fun getBookmarkById(id: String): BookmarkedStoryEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(bookmarks: List<BookmarkedStoryEntity>)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: String)
}