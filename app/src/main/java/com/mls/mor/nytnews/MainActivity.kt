package com.mls.mor.nytnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mls.mor.nytnews.ui.NytApp
import com.mls.mor.nytnews.ui.settings.SettingsViewModel
import com.mls.mor.nytnews.ui.settings.ThemeConfig
import com.mls.mor.nytnews.ui.theme.NYTNewsTheme
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO show splash after content is loaded
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsUiStateState =
                settingsViewModel.settingsUiState.collectAsStateWithLifecycle()

            NYTNewsTheme(
                darkTheme = when (settingsUiStateState.value.theme) {
                    ThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                    ThemeConfig.DARK -> true
                    ThemeConfig.LIGHT -> false
                },
                dynamicColor = settingsUiStateState.value.dynamicColorsEnabled,
            ) {
                NytApp(windowSizeClass = calculateWindowSizeClass(activity = this))
            }
        }
    }
}
