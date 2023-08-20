@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.mls.mor.nytnews.ui.bookmarks

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mls.mor.nytnews.R
import com.mls.mor.nytnews.ui.common.ItemCommonAsyncImage
import com.mls.mor.nytnews.ui.common.LottieAnimationElement
import com.mls.mor.nytnews.ui.common.SwipeToDeleteBackground
import com.mls.mor.nytnews.ui.theme.NYTNewsTheme

private const val TAG = "BookmarksScreen"

@Composable
fun BookmarksRoute(
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = hiltViewModel(),
    onStoryClick: (story: BookmarkUi) -> Unit = {},
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        BookmarksScreen(
            stories = uiState.value.bookmarks,
            onStoryClick = onStoryClick,
            onStoryDelete = { viewModel.deleteBookmark(it) },
        )
    }
}

@Composable
fun BookmarksScreen(
    modifier: Modifier = Modifier,
    stories: List<BookmarkUi>,
    lazyListState: LazyListState = rememberLazyListState(),
    onStoryClick: (story: BookmarkUi) -> Unit = {},
    onStoryDelete: (id: String) -> Unit = {},
) {

    if (stories.isEmpty()) {
        BookmarksEmptyScreen()
        return
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Column {
                Text(
                    modifier = Modifier
                        .paddingFromBaseline(top = 32.dp)
                        .padding(horizontal = 8.dp),
                    text = stringResource(id = R.string.bookmarks_title),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

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
    onStoryClick: (story: BookmarkUi) -> Unit = {},
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
                bookmarkedStory = bookmarked,
                onStoryClick = onStoryClick
            )
        },
        background = { SwipeToDeleteBackground(dismissState = dismissState) },
    )
}

@Composable
fun BookmarkItem(
    modifier: Modifier = Modifier,
    bookmarkedStory: BookmarkUi,
    onStoryClick: (story: BookmarkUi) -> Unit = {},
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(50))
            .clickable { onStoryClick(bookmarkedStory) },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column {
            ItemCommonAsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                imageUrl = bookmarkedStory.imageUrl,
                contentDescription = bookmarkedStory.title
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .paddingFromBaseline(top = 16.dp)
                    .alpha(0.8f),
                text = bookmarkedStory.subsection.ifEmpty { bookmarkedStory.topic },
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelSmall
            )

            Text(
                modifier = Modifier
                    .paddingFromBaseline(top = 24.dp)
                    .padding(horizontal = 16.dp),
                text = bookmarkedStory.title,
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
                    text = bookmarkedStory.byline,
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .paddingFromBaseline(16.dp),
                    text = bookmarkedStory.publishedDate,
                    style = MaterialTheme.typography.labelSmall
                )

            }

            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
                    .paddingFromBaseline(24.dp),
                text = bookmarkedStory.abstract
            )
        }
    }
}

@Composable
fun BookmarksEmptyScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        LottieAnimationElement(
            animationPath = R.raw.lottie_bookmarks,
            title = stringResource(R.string.bookmarks_are_empty)
        )
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
            bookmarkedStory = fakeBookmarkUiModel,
            onStoryClick = {}
        )
    }
}