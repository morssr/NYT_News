package com.example.mor.nytnews.data.bookmarks.cache

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarksRepositoryImpl @Inject constructor(
    private val bookmarksDao: BookmarksDao
): BookmarksRepository {

        override suspend fun getBookmarks(): List<BookmarkedStory> {
            return bookmarksDao.getBookmarks().map { it.toBookmarkedStory() }
        }

        override fun getBookmarksStream(): Flow<List<BookmarkedStory>> {
            return bookmarksDao.getBookmarksStream().map { it.toBookmarkedStoryList() }
        }

        override suspend fun getBookmarkById(id: String): BookmarkedStory {
            return bookmarksDao.getBookmarkById(id).toBookmarkedStory()
        }

        override suspend fun saveBookmarks(bookmarks: List<BookmarkedStory>) {
            bookmarksDao.insertOrReplace(bookmarks.toBookmarkedStoryEntityList())
        }

        override suspend fun deleteBookmarkById(id: String) {
            bookmarksDao.deleteBookmarkById(id)
        }
}