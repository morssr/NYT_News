package com.mls.mor.nytnews.ui.settings

import com.mls.mor.nytnews.data.settings.SettingsModel

data class SettingsUiState(
    val theme: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
    val dynamicColorsEnabled: Boolean = false
)

fun SettingsModel.toUiState(): SettingsUiState {
    return SettingsUiState(
        theme = theme,
        dynamicColorsEnabled = dynamicColorsEnabled
    )
}