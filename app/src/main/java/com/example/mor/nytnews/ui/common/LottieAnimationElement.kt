package com.example.mor.nytnews.ui.common

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.mor.nytnews.R
import com.example.mor.nytnews.ui.theme.NYTNewsTheme

@Composable
fun LottieAnimationElement(
    modifier: Modifier = Modifier,
    title: String = "",
    @RawRes animationPath: Int,
    autoPlay: Boolean = true,
    animationSpeed: Float = 1f,
    extraBottomContent: @Composable () -> Unit = {},
    background: @Composable () -> Unit = {},
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationPath))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = if (autoPlay) LottieConstants.IterateForever else 1,
        speed = animationSpeed,
    )
    Box(
        modifier = modifier
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            background()
        }

        LottieAnimation(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight(),
            composition = composition,
            progress = { progress }
        )

        Box(
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            extraBottomContent()
        }
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun LottieAnimationElementPreviewLight() {
    NYTNewsTheme(darkTheme = false) {
        LottieAnimationElement(
            title = stringResource(R.string.loading),
            animationPath = R.raw.lottie_search_animation,
        )
    }
}