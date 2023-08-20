package com.mls.mor.nytnews.data.search.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<SearchRemoteKeysEntity>)

    @Query("SELECT * FROM search_remote_keys WHERE id = :id")
    suspend fun remoteKeysSearchId(id: String): SearchRemoteKeysEntity?

    @Query("DELETE FROM search_remote_keys")
    suspend fun clearRemoteKeys()
}