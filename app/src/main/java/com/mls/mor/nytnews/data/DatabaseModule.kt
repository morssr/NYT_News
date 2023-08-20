package com.mls.mor.nytnews.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        @DatabaseUseInMemoryFlag useInMemory: Boolean = FLAG_USE_IN_MEMORY_DB
    ): AppDatabase {
        return AppDatabase.getInstance(context, useInMemory)
    }

    @Provides
    @Singleton
    @DatabaseUseInMemoryFlag
    fun provideUseInMemoryFlag(): Boolean = FLAG_USE_IN_MEMORY_DB
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DatabaseUseInMemoryFlag