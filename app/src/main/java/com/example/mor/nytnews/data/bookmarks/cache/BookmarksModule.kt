package com.example.mor.nytnews.data.bookmarks.cache

import com.example.mor.nytnews.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object BookmarksModule {

    @Provides
    @ActivityRetainedScoped
    fun provideBookmarksDao(database: AppDatabase): BookmarksDao =
        database.bookmarksDao()

    @Provides
    @ActivityRetainedScoped
    fun provideBookmarksRepository(
        bookmarksDao: BookmarksDao
    ): BookmarksRepository =
        BookmarksRepositoryImpl(bookmarksDao)
}
