package com.example.mor.nytnews.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Light
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mor.nytnews.R
import com.example.mor.nytnews.ui.theme.NYTNewsTheme

@Composable
fun AppSettingsDialog(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onDismiss: () -> Unit = { },
) {

    val uiState = viewModel.settingsUiState.collectAsStateWithLifecycle()

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                SettingsDialogContent(
                    currentTheme = uiState.value.theme,
                    dynamicColorsEnabled = uiState.value.dynamicColorsEnabled,
                    onThemeChanged = { viewModel.onThemeChanged(it) },
                    onDynamicColorsEnabled = { viewModel.onDynamicColorsEnabledChanged(it) },
                )

                TextButton(modifier = Modifier.fillMaxWidth(), onClick = { onDismiss() }) {
                    Text(text = stringResource(R.string.done))
                }
            }
        }
    }
}

@Composable
fun SettingsDialogContent(
    modifier: Modifier = Modifier,
    currentTheme: ThemeConfig = ThemeConfig.FOLLOW_SYSTEM,
    dynamicColorsEnabled: Boolean = false,
    onThemeChanged: (ThemeConfig) -> Unit = { },
    onDynamicColorsEnabled: (Boolean) -> Unit = { },
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(text = stringResource(R.string.theme_modes))

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ThemeConfig.values().forEach { themeConfig ->
                FilledIconToggleButton(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    checked = themeConfig == currentTheme,
                    onCheckedChange = { onThemeChanged(themeConfig) }) {
                    val text = when (themeConfig) {
                        ThemeConfig.FOLLOW_SYSTEM -> stringResource(R.string.follow_system)
                        ThemeConfig.LIGHT -> stringResource(R.string.light)
                        ThemeConfig.DARK -> stringResource(R.string.dark)
                    }

                    val icon = when (themeConfig) {
                        ThemeConfig.FOLLOW_SYSTEM -> Icons.Default.SettingsApplications
                        ThemeConfig.LIGHT -> Icons.Default.Light
                        ThemeConfig.DARK -> Icons.Default.DarkMode
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(text = text)
                        Icon(imageVector = icon, contentDescription = text)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(R.string.dynamic_colors))
            Switch(checked = dynamicColorsEnabled, onCheckedChange = onDynamicColorsEnabled)
        }
    }
}

@Preview
@Composable
fun SettingsDialogPreview() {
    NYTNewsTheme {
        SettingsDialogContent()
    }
}