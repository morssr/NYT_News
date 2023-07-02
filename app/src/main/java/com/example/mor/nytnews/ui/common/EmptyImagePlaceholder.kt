package com.example.mor.nytnews.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NoPhotography
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyImagePlaceholder(modifier: Modifier = Modifier) {
    Surface(modifier = modifier, tonalElevation = 1.dp) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                modifier = Modifier.fillMaxSize(0.33f),
                imageVector = Icons.Outlined.NoPhotography,
                contentDescription = ""
            )
        }
    }
}