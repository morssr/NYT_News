package com.example.mor.nytnews.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import com.example.mor.nytnews.ui.settings.ThemeConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "SettingsRepositoryImpl"

class SettingsRepositoryImpl @Inject constructor(
    private val settingsPreferences: DataStore<Preferences>,
    logger: Logger
) : SettingsRepository {

    private val log = logger.withTag(TAG)

    override fun getSettings(): Flow<SettingsModel> {
        log.d { "getSettings(): called" }
        return settingsPreferences.data.map { preferences ->
            SettingsModel(
                theme = preferences[stringPreferencesKey(THEME_MODE_PREFERENCES_KEY)]?.let {
                    ThemeConfig.valueOf(
                        it
                    )
                } ?: ThemeConfig.FOLLOW_SYSTEM,
                dynamicColorsEnabled = preferences[booleanPreferencesKey(
                    DYNAMIC_COLORS_ENABLED_PREFERENCES_KEY
                )] ?: false
            )
        }
    }

    override suspend fun getTheme(): ThemeConfig {
        log.d { "getTheme(): called" }
        return settingsPreferences.data.map { preferences ->
            preferences[stringPreferencesKey(THEME_MODE_PREFERENCES_KEY)]?.let {
                ThemeConfig.valueOf(
                    it
                )
            }
                ?: ThemeConfig.FOLLOW_SYSTEM
        }.first()
    }

    override suspend fun setTheme(theme: ThemeConfig) {
        log.d { "setTheme(): called with theme: $theme" }
        settingsPreferences.edit { preferences ->
            preferences[stringPreferencesKey(THEME_MODE_PREFERENCES_KEY)] = theme.name
        }
    }

    override suspend fun getDynamicColorsEnabled(): Boolean {
        log.d { "getDynamicColorsEnabled(): called" }
        return settingsPreferences.data.map { preferences ->
            preferences[booleanPreferencesKey(DYNAMIC_COLORS_ENABLED_PREFERENCES_KEY)] ?: false
        }.first()
    }

    override suspend fun setDynamicColorsEnabled(enabled: Boolean) {
        log.d { "setDynamicColorsEnabled(): called with enabled: $enabled" }
        settingsPreferences.edit { preferences ->
            preferences[booleanPreferencesKey(DYNAMIC_COLORS_ENABLED_PREFERENCES_KEY)] =
                enabled
        }
    }
}