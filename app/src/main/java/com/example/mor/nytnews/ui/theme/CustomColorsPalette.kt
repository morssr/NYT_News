package com.example.mor.nytnews.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColorsPalette(
    val secondaryBackground: Color = Color.Unspecified,
)

val LocalCustomColorsPalette = staticCompositionLocalOf { CustomColorsPalette() }

val MaterialTheme.customColorsPalette: CustomColorsPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColorsPalette.current

