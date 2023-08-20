package com.mls.mor.nytnews.ui.common

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ImageNotSupported
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

private const val TAG = "ItemCommonAsyncImage"

@Composable
fun ItemCommonAsyncImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    contentDescription: String = ""
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
    )

    Box(modifier = modifier) {

        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        when (painter.state) {
            is AsyncImagePainter.State.Error -> ErrorLoadingStateBox()
            is AsyncImagePainter.State.Loading -> LoadingStateBox()
            else -> Unit
        }

    }
}

@Composable
private fun LoadingStateBox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .shimmer(
                customShimmer = rememberShimmer(
                    shimmerBounds = ShimmerBounds.View,
                    theme = LocalShimmerTheme.current.copy(
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                400,
                                easing = LinearEasing,
                                delayMillis = 250,
                            ),
                            repeatMode = RepeatMode.Restart,
                        )
                    )
                )
            )
            .background(Color.LightGray)
    )
}

@Composable
private fun ErrorLoadingStateBox() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.ImageNotSupported,
                contentDescription = "Error loading image",
                modifier = Modifier.fillMaxSize(0.5f)
            )
        }
    }
}