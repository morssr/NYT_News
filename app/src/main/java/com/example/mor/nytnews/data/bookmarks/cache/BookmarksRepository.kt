package com.example.mor.nytnews.data.bookmarks.cache

import kotlinx.coroutines.flow.Flow

interface BookmarksRepository {

    suspend fun getBookmarks(): List<BookmarkedStory>

    fun getBookmarksStream(): Flow<List<BookmarkedStory>>

    suspend fun getBookmarkById(id: String): BookmarkedStory

    suspend fun saveBookmarks(bookmarks: List<BookmarkedStory>)

    suspend fun deleteBookmarkById(id: String)
}