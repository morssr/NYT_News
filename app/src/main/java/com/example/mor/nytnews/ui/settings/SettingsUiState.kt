package com.example.mor.nytnews.ui.settings

import com.example.mor.nytnews.data.settings.SettingsModel

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