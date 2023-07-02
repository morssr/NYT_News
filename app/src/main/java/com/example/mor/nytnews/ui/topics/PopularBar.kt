@file:OptIn(ExperimentalFoundationApi::class)

package com.example.mor.nytnews.ui.topics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CutCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.mor.nytnews.R
import com.example.mor.nytnews.data.popular.common.PopularType
import com.example.mor.nytnews.ui.common.ItemCommonAsyncImage
import com.example.mor.nytnews.ui.theme.NYTNewsTheme
import java.util.Date
import kotlin.math.absoluteValue

private const val TAG = "PopularBar"

@Composable
fun PopularBarComponent(
    modifier: Modifier = Modifier,
    populars: List<PopularUi> = emptyList(),
    onPopularClick: (id: String) -> Unit = {},
) {
    val pagerState = rememberPagerState()

    BoxWithConstraints(modifier = modifier) {

        // Calculate the item width to be 80% of the maxWidth
        val popularItemWidth = maxWidth * 0.8f

        HorizontalPager(
            pageCount = populars.size,
            state = pagerState,
            pageSize = PageSize.Fixed(popularItemWidth),
            contentPadding = PaddingValues(16.dp),
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

                // Card content
                populars[pageIndex].let {
                    PopularListItem(
                        id = it.id,
                        imageUrl = it.imageUrl,
                        title = it.title,
                        onItemClick = onPopularClick

                    )
                }
            }
        }
    }
}

@Composable
private fun PopularListItem(
    modifier: Modifier = Modifier,
    id: String = "",
    imageUrl: String = "",
    title: String = "",
    onItemClick: (id: String) -> Unit = {}
) {
    Box(modifier = modifier.clickable { onItemClick(id) }) {

        ItemCommonAsyncImage(
            modifier = Modifier.fillMaxSize(),
            imageUrl = imageUrl,
            contentDescription = stringResource(R.string.popular_article_image_content_description)
        )

        Surface(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            CutCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            ),
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
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp, bottom = 8.dp),
                    text = title,
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
            imageUrl = "https://static01.nyt.com/images/2023/06/21/opinion/19Sinykin/19Sinykin-mediumThreeByTwo440-v2.jpg",
            title = "Max Morath, Pianist Who Staged a One-Man Ragtime Revival, Dies at 96"
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
                    "https://static01.nyt.com/images/2023/06/21/opinion/19Sinykin/19Sinykin-mediumThreeByTwo440-v2.jpg"
                ),
                PopularUi(
                    "iioaf",
                    PopularType.MOST_VIEWED,
                    "asdkljfa jkldsjflka",
                    "asdfhajkhsdj",
                    "asdkflja",
                    Date().toString(),
                    "https://static01.nyt.com/images/2023/06/21/opinion/19Sinykin/19Sinykin-mediumThreeByTwo440-v2.jpg"
                ),
            )
        )
    }

}