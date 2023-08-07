package com.mls.mor.nytnews.ui.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mls.mor.nytnews.IoDispatcher
import com.mls.mor.nytnews.MainLogger
import com.mls.mor.nytnews.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SettingsViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    savedStateHandle: SavedStateHandle,
    @MainLogger logger: Logger,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    private val log = logger.withTag(TAG)

    val settingsUiState = settingsRepository.getSettings()
        .map { it.toUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SettingsUiState()
        )

    fun onThemeChanged(theme: ThemeConfig) {
        log.d { "onThemeChanged(): called with theme: $theme" }
        viewModelScope.launch(dispatcher) {
            settingsRepository.setTheme(theme)
        }
    }

    fun onDynamicColorsEnabledChanged(enabled: Boolean) {
        log.d { "onDynamicColorsEnabledChanged(): called with enabled: $enabled" }
        viewModelScope.launch(dispatcher) {
            settingsRepository.setDynamicColorsEnabled(enabled)
        }
    }
}