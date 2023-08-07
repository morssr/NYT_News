package com.mls.mor.nytnews.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mls.mor.nytnews.data.topics.cache.TopStoriesDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var topStoriesDao: TopStoriesDao
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        topStoriesDao = db.topStoriesDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun queryAllTopStories_IsNotEmpty() = runTest {
        val storiesToInsert = dummyTopStoriesEntities
        val storiesInsertionArraySize = storiesToInsert.size
        topStoriesDao.insertOrReplace(storiesToInsert)
        val topStoriesQuery = topStoriesDao.getTopStories()
        assertTrue(topStoriesQuery.isNotEmpty())
        assertTrue(topStoriesQuery.size == storiesInsertionArraySize)
    }

    @Test
    fun queryAllTopStories_IsEmpty() = runTest {
        val topStoriesQuery = topStoriesDao.getTopStories()
        assertTrue(topStoriesQuery.isEmpty())
    }
}