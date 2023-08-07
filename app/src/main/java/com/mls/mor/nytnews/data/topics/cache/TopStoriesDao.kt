package com.mls.mor.nytnews.data.topics.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TopStoriesDao {

    @Query("SELECT * FROM stories WHERE id = :id")
    fun getStoryById(id: String): StoryEntity

    @Query("SELECT * FROM stories")
    fun getTopStories(): List<StoryEntity>

    @Query("SELECT * FROM stories")
    fun getTopStoriesStream(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM stories WHERE topic = :topic")
    fun getTopStoriesByTopic(topic: String): List<StoryEntity>

    @Query("SELECT * FROM stories WHERE topic = :topic")
    fun getTopStoriesBySectionTopic(topic: String): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(stories: List<StoryEntity>)

    @Query("DELETE FROM stories WHERE topic = :topic")
    suspend fun deleteAllByTopic(topic: String)

    @Query("DELETE FROM stories")
    suspend fun deleteAllTopStories()
}
