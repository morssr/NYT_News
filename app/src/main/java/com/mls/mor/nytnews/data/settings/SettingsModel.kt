package com.mls.mor.nytnews.data.settings

import com.mls.mor.nytnews.ui.settings.ThemeConfig

data class SettingsModel(
    val theme: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
    val dynamicColorsEnabled: Boolean = false
)