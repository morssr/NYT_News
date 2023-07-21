package com.example.mor.nytnews.data.settings

import com.example.mor.nytnews.ui.settings.ThemeConfig

data class SettingsModel(
    val theme: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
    val dynamicColorsEnabled: Boolean = false
)