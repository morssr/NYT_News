package com.example.mor.nytnews.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mor.nytnews.data.bookmarks.cache.BookmarkedStoryEntity
import com.example.mor.nytnews.data.bookmarks.cache.BookmarksDao
import com.example.mor.nytnews.data.search.cache.SearchDao
import com.example.mor.nytnews.data.search.cache.SearchEntity
import com.example.mor.nytnews.data.search.cache.SearchRemoteKeysDao
import com.example.mor.nytnews.data.search.cache.SearchRemoteKeysEntity
import com.example.mor.nytnews.data.topics.cache.StoryEntity
import com.example.mor.nytnews.data.topics.cache.TopStoriesDao
import com.example.mor.nytnews.data.utils.db.coverters.DateTypeConverter
import com.example.mor.nytnews.utilities.printThreadInfo

const val FLAG_USE_IN_MEMORY_DB = false

@TypeConverters(DateTypeConverter::class)
@Database(
    entities = [
        StoryEntity::class,
        BookmarkedStoryEntity::class,
        SearchEntity::class,
        SearchRemoteKeysEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun topStoriesDao(): TopStoriesDao
    abstract fun bookmarksDao(): BookmarksDao
    abstract fun searchDao(): SearchDao
    abstract fun searchRemoteKeysDao(): SearchRemoteKeysDao

    companion object {
        private const val TAG = "AppDatabase"
        private const val DATABASE_NAME = "nyt.db"

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(
            context: Context,
            useInMemory: Boolean = FLAG_USE_IN_MEMORY_DB
        ): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, useInMemory).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context, useInMemory: Boolean): AppDatabase {
            Log.d(TAG, "buildDatabase called with useInMemory = $useInMemory")
            printThreadInfo(TAG, "buildDatabase")
            val databaseBuilder = if (useInMemory)
                Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                    .addCallback(
                        object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                Log.d(TAG, "onCreate: Im memory Database created successfully")
                            }
                        }
                    )
            else
                Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .addCallback(
                        object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                Log.d(TAG, "onCreate:Persistent Database created successfully")
                            }
                        }
                    )

            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}