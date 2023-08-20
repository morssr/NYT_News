package com.mls.mor.nytnews.data.popular

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mls.mor.nytnews.data.AppDatabase
import com.mls.mor.nytnews.data.popular.cache.PopularDao
import com.mls.mor.nytnews.data.popular.cache.PopularEntity
import com.mls.mor.nytnews.data.popular.common.PopularType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PopularEntityTest {
    private lateinit var popularDao: PopularDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        popularDao = db.popularDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeOnePopularAndReadInList() = runTest {
        val popular = fakePopularEntity("adfas32hu2h")
        popularDao.insertAll(listOf(popular))
        val populars = popularDao.getAllPopularStream()
        assert(populars.first().size == 1)
    }

    @Test
    @Throws(Exception::class)
    fun writeListPopularAndReadInList_CheckQueryAll_IsBiggerThenOne() = runTest {
        val newPopulars = arrayListOf<PopularEntity>().apply {
            for (i in 0..10) {
                add(fakePopularEntity("adfas32hu2h$i"))
            }
        }
        popularDao.insertAll(newPopulars)
        val populars = popularDao.getAllPopularStream()
        assert(populars.first().size > 1)
    }

    @Test
    @Throws(Exception::class)
    fun writeListPopularsTypeMostEmailed_CheckQueryByType_MostEmailed_IsBiggerThenOne() = runTest {
        val newPopulars = arrayListOf<PopularEntity>().apply {
            for (i in 0..10) {
                add(fakePopularEntity("adfas32hu2h$i", PopularType.MOST_EMAILED))
            }
        }
        popularDao.insertAll(newPopulars)
        val populars = popularDao.getPopularByTypeStream(PopularType.MOST_EMAILED).first()
        println(populars)
        assert(populars.size > 1)
    }

    @Test
    @Throws(Exception::class)
    fun writeListPopularsTypeMostEmailed_CheckQueryByTypeMostViewed_ReturnsEmpty() = runTest {
        val newPopulars = arrayListOf<PopularEntity>().apply {
            for (i in 0..10) {
                add(fakePopularEntity("adfas32hu2h$i", PopularType.MOST_EMAILED))
            }
        }
        popularDao.insertAll(newPopulars)
        val populars = popularDao.getPopularByTypeStream(PopularType.MOST_VIEWED).first()
        println(populars)
        assert(populars.isEmpty())
    }

    private fun fakePopularEntity(
        id: String,
        type: PopularType = PopularType.MOST_EMAILED
    ): PopularEntity =
        PopularEntity(
            id,
            type,
            "title",
            "abstract",
            "byline",
            "section",
            Date(),
            "https://www.google.com",
            "https://www.google.com",
        )


}