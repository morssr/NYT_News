package com.mls.mor.nytnews.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import co.touchlab.kermit.Logger
import com.mls.mor.nytnews.MainLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @SettingsPreferences
    @Provides
    @Singleton
    fun provideSettingsPref(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile(SETTINGS_PREFERENCES_FILE_NAME)
        }

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @SettingsPreferences settingsPreferences: DataStore<Preferences>,
        @MainLogger logger: Logger
    ): SettingsRepository = SettingsRepositoryImpl(
        settingsPreferences = settingsPreferences,
        logger = logger
    )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SettingsPreferences