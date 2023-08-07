@file:OptIn(ExperimentalFoundationApi::class)

package com.mls.mor.nytnews.ui.topics

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.mls.mor.nytnews.R
import com.mls.mor.nytnews.data.popular.common.PopularType
import com.mls.mor.nytnews.ui.common.ItemCommonAsyncImage
import com.mls.mor.nytnews.ui.theme.NYTNewsTheme
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import java.util.Date
import kotlin.math.absoluteValue

private const val TAG = "PopularBar"

@Composable
fun PopularBarComponent(
    modifier: Modifier = Modifier,
    populars: List<PopularUi> = emptyList(),
    shimmer: Boolean = false,
    onPopularStoryClick: (item: PopularUi) -> Unit = {},
) {
    val pagerState = rememberPagerState()

    BoxWithConstraints(modifier = modifier) {

        // Calculate the item width to be 80% of the maxWidth
        val popularItemWidth = maxWidth * 0.8f

        Column {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                        )
                    ) {
                        append("Newest")
                    }
                    append(" ")
                    append("Reports")
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .padding(horizontal = 16.dp)
            )
            HorizontalPager(
                pageCount = if (!shimmer) populars.size else 10,
                state = pagerState,
                pageSize = PageSize.Fixed(popularItemWidth),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            ) { pageIndex ->
                Card(Modifier.graphicsLayer {
                    // Calculate the absolute offset for the current page from the
                    // scroll position. We use the absolute value which allows us to mirror
                    // any effects for both directions
                    val pageOffset = (
                            (pagerState.currentPage - pageIndex) + pagerState
                                .currentPageOffsetFraction
                            ).absoluteValue

                    // animate the alpha, between 50% and 100%
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )

                    // animate the scaleX + scaleY, between 85% and 100%
                    val minScale = 0.85f

                    scaleX = lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )

                    scaleY = lerp(
                        start = minScale,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                }
                ) {

                    if (!shimmer) {
                        // Card content
                        populars[pageIndex].let {
                            PopularListItem(
                                popular = it,
                                onItemClick = onPopularStoryClick
                            )
                        }
                    } else {
                        ShimmerPopularListItemScaffold()
                    }
                }
            }
        }
    }
}

@Composable
private fun PopularListItem(
    modifier: Modifier = Modifier,
    popular: PopularUi,
    onItemClick: (item: PopularUi) -> Unit = {}
) {
    Box(modifier = modifier.clickable { onItemClick(popular) }) {

        ItemCommonAsyncImage(
            modifier = Modifier.fillMaxSize(),
            imageUrl = popular.imageUrl,
            contentDescription = stringResource(R.string.popular_article_image_content_description)
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.61f)),
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    text = popular.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ShimmerPopularListItemScaffold(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.shimmer(
            customShimmer = rememberShimmer(
                shimmerBounds = ShimmerBounds.View,
                theme = LocalShimmerTheme.current.copy(
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            800,
                            easing = LinearEasing,
                            delayMillis = 500,
                        ),
                        repeatMode = RepeatMode.Restart,
                    )
                )
            )
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        )

        Surface(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.radialGradient(
                            listOf(
                                MaterialTheme.colorScheme.surface,
                                Color.Transparent
                            ), radius = 3000f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp, bottom = 8.dp),
                    text = "",
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun PopularListItemPreview() {
    NYTNewsTheme() {
        PopularListItem(
            popular = PopularUi(
                "234234",
                PopularType.MOST_VIEWED,
                "Max Morath, Pianist Who Staged a One-Man Ragtime Revival, Dies at 96",
                "asdf",
                "asdf",
                "asdf",
                "https://static01.nyt.com/images/2023/06/21/opinion/19Sinykin/19Sinykin-mediumThreeByTwo440-v2.jpg",
                ""
            )
        )
    }
}

@Preview
@Composable
fun PopularBarPreview() {
    NYTNewsTheme {
        PopularBarComponent(
            populars = listOf(
                PopularUi(
                    "iioaf",
                    PopularType.MOST_VIEWED,
                    "asdkljfa jkldsjflka",
                    "asdfhajkhsdj",
                    "asdkflja",
                    Date().toString(),
                    "https://static01.nyt.com/images/2023/06/21/opinion/19Sinykin/19Sinykin-mediumThreeByTwo440-v2.jpg",
                    ""
                ),
                PopularUi(
                    "iioaf",
                    PopularType.MOST_VIEWED,
                    "asdkljfa jkldsjflka",
                    "asdfhajkhsdj",
                    "asdkflja",
                    Date().toString(),
                    "https://static01.nyt.com/images/2023/06/21/opinion/19Sinykin/19Sinykin-mediumThreeByTwo440-v2.jpg",
                    ""
                ),
            ), shimmer = true
        )
    }
}