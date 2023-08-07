package com.mls.mor.nytnews.data.settings

import com.mls.mor.nytnews.ui.settings.ThemeConfig
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<SettingsModel>
    suspend fun getTheme(): ThemeConfig
    suspend fun setTheme(theme: ThemeConfig)
    suspend fun getDynamicColorsEnabled(): Boolean
    suspend fun setDynamicColorsEnabled(enabled: Boolean)
}