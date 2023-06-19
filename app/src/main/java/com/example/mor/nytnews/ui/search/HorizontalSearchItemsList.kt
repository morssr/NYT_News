package com.example.mor.nytnews.ui.search

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mor.nytnews.R

@Composable
fun HorizontalSearchItemsListElement(
    modifier: Modifier = Modifier,
    title: String,
    searchUiItems: List<SearchUiModel> = emptyList(),
    lazyListState: LazyListState = rememberLazyListState(),
    onSearchItemClick: (String) -> Unit = {},
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = title,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {

            items(searchUiItems) { it ->
                SmallSearchStoryItem(
                    story = it,
                    onStoryClick = { onSearchItemClick(it.id) }
                )
            }
        }
    }
}

@Composable
fun SmallSearchStoryItem(
    modifier: Modifier = Modifier,
    story: SearchUiModel,
    onStoryClick: (SearchUiModel) -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier
            .width(300.dp)
            .animateContentSize(animationSpec = tween(50))
            .clickable {
                onStoryClick(story)
            },
    ) {
        Column(modifier = Modifier) {
            AsyncImage(
                modifier = Modifier.aspectRatio(16f / 9f),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(story.imageUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.ic_launcher_background)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

            Text(
                modifier = Modifier
                    .paddingFromBaseline(top = 24.dp, bottom = 8.dp)
                    .padding(horizontal = 16.dp),
                text = story.title,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}