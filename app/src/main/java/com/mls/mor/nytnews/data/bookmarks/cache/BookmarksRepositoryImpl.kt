package com.mls.mor.nytnews.data.bookmarks.cache

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "BookmarksRepositoryImpl"

class BookmarksRepositoryImpl @Inject constructor(
    private val bookmarksDao: BookmarksDao,
    logger: Logger
) : BookmarksRepository {

    private val log = logger.withTag(TAG)

    override suspend fun getBookmarks(): List<BookmarkedStory> {
        log.d { "getBookmarks(): called" }
        return bookmarksDao.getBookmarks().map { it.toBookmarkedStory() }
    }

    override fun getBookmarksStream(): Flow<List<BookmarkedStory>> {
        log.d { "getBookmarksStream(): called" }
        return bookmarksDao.getBookmarksStream().map { it.toBookmarkedStoryList() }
    }

    override suspend fun getBookmarkById(id: String): BookmarkedStory {
        log.d { "getBookmarkById(): called with id: $id" }
        return bookmarksDao.getBookmarkById(id).toBookmarkedStory()
    }

    override suspend fun saveBookmarks(bookmarks: List<BookmarkedStory>) {
        log.d { "saveBookmarks(): called with bookmarks: $bookmarks" }
        bookmarksDao.insertOrReplace(bookmarks.toBookmarkedStoryEntityList())
    }

    override suspend fun deleteBookmarkById(id: String) {
        log.d { "deleteBookmarkById(): called with id: $id" }
        bookmarksDao.deleteBookmarkById(id)
    }
}