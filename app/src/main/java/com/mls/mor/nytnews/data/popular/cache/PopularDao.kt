package com.mls.mor.nytnews.data.popular.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mls.mor.nytnews.data.popular.common.PopularType
import kotlinx.coroutines.flow.Flow

@Dao
interface PopularDao {

    @Query("SELECT * FROM popular")
    fun getAllPopularStream(): Flow<List<PopularEntity>>

    @Query("SELECT * FROM popular WHERE type = :type")
    fun getPopularByTypeStream(type: PopularType): Flow<List<PopularEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(popular: List<PopularEntity>)

    @Query("DELETE FROM popular")
    suspend fun clearPopular()

    @Query("DELETE FROM popular WHERE type = :type")
    suspend fun clearByType(type: PopularType)
}