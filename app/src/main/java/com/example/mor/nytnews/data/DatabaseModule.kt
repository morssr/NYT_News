package com.example.mor.nytnews.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        useInMemory: Boolean = FLAG_USE_IN_MEMORY_DB
    ): AppDatabase {
        return AppDatabase.getInstance(context, useInMemory)
    }
}