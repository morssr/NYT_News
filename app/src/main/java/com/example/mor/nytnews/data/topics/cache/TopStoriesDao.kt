package com.example.mor.nytnews.data.topics.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TopStoriesDao {

    @Query("SELECT * FROM stories")
    fun getTopStories(): List<StoryEntity>

    @Query("SELECT * FROM stories")
    fun getTopStoriesStream(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE section = :section")
    fun getTopStoriesBySection(section: String): List<StoryEntity>

    @Query("SELECT * FROM stories WHERE section = :section")
    fun getTopStoriesBySectionStream(section: String): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(stories: List<StoryEntity>)

    @Query("DELETE FROM stories WHERE section = :section")
    suspend fun deleteAllBySection(section: String)

    @Query("DELETE FROM stories")
    suspend fun deleteAllTopStories()
}
