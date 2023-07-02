package com.example.mor.nytnews.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mor.nytnews.R

@Composable
fun ItemCommonAsyncImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    contentDescription: String = ""
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl.ifEmpty { EmptyImagePlaceholder(Modifier.fillMaxSize()) })
            .crossfade(true)
            .placeholder(R.drawable.ic_launcher_background)
            .build(),
        contentScale = ContentScale.Crop,
        contentDescription = contentDescription
    )
}