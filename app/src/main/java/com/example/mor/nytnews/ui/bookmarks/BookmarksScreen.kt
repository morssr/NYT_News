@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.mor.nytnews.ui.bookmarks

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mor.nytnews.R
import com.example.mor.nytnews.ui.common.SwipeToDeleteBackground
import com.example.mor.nytnews.ui.theme.NYTNewsTheme

private const val TAG = "BookmarksScreen"

@Composable
fun BookmarksRoute(
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        BookmarksScreen(
            stories = uiState.value.bookmarks,
            onStoryDelete = { viewModel.deleteBookmark(it) },
        )
    }
}

@Composable
fun BookmarksScreen(
    modifier: Modifier = Modifier,
    stories: List<BookmarkUi>,
    lazyListState: LazyListState = rememberLazyListState(),
    onStoryClick: (id: String) -> Unit = {},
    onStoryDelete: (id: String) -> Unit = {},
) {


    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = stories,
            key = { story -> story.id }
        ) { story ->

            val dismissState = rememberDismissState(
                confirmValueChange = {
                    if (it == DismissValue.DismissedToStart) {
                        onStoryDelete(story.id)
                    }
                    true
                },
                positionalThreshold = { lazyListState.layoutInfo.viewportSize.width / 2f }
            )

            SwipeToDeleteBookmarkItem(
                Modifier.animateItemPlacement(),
                bookmarked = story,
                dismissState = dismissState,
                onStoryClick = onStoryClick
            )
        }
    }
}

@Composable
fun SwipeToDeleteBookmarkItem(
    modifier: Modifier = Modifier,
    bookmarked: BookmarkUi,
    dismissState: DismissState = rememberDismissState(),
    onStoryClick: (id: String) -> Unit = {},
) {
    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        dismissContent = {
            BookmarkItem(
                modifier = modifier
                    .fillMaxWidth()
                    .alpha(if (dismissState.progress <= 0.9) 1f - dismissState.progress else 1f),
                bookmarked = bookmarked,
                onStoryClick = onStoryClick
            )
        },
        background = { SwipeToDeleteBackground(dismissState = dismissState) },
    )
}

@Composable
fun BookmarkItem(
    modifier: Modifier = Modifier,
    bookmarked: BookmarkUi,
    onStoryClick: (id: String) -> Unit = {},
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(50))
            .clickable { onStoryClick(bookmarked.id) },
    ) {
        Column {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(bookmarked.imageUrl)
                    .crossfade(true)
                    .placeholder(R.drawable.ic_launcher_background)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .paddingFromBaseline(top = 16.dp)
                    .alpha(0.8f),
                text = bookmarked.subsection.ifEmpty { bookmarked.topic },
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                modifier = Modifier
                    .paddingFromBaseline(top = 24.dp)
                    .padding(horizontal = 16.dp),
                text = bookmarked.title,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.7f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .paddingFromBaseline(16.dp),
                    text = bookmarked.byline,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .paddingFromBaseline(16.dp),
                    text = bookmarked.publishedDate,
                    style = MaterialTheme.typography.labelSmall
                )

            }

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .paddingFromBaseline(24.dp),
                text = bookmarked.abstract
            )
        }
    }
}


@Preview
@Composable
fun BookmarkScreenPreview() {
    NYTNewsTheme {
        BookmarksScreen(stories = fakeBookmarksUiList, onStoryClick = {})
    }
}

@Preview
@Composable
fun BookmarkItemPreview() {
    NYTNewsTheme() {
        BookmarkItem(
            bookmarked = fakeBookmarkUiModel,
            onStoryClick = {}
        )
    }
}