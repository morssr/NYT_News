@file:OptIn(ExperimentalFoundationApi::class)

package com.mls.mor.nytnews.ui.topics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.rounded.BookmarkRemove
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.mls.mor.nytnews.ui.common.ExpandableText
import com.mls.mor.nytnews.ui.common.ItemCommonAsyncImage
import com.mls.mor.nytnews.ui.common.NetworkUnavailableElement
import com.mls.mor.nytnews.ui.common.StateProductionError
import com.mls.mor.nytnews.ui.common.UnknownErrorElement
import com.mls.mor.nytnews.ui.theme.NYTNewsTheme


private const val TAG = "StoriesComponent"

@Composable
fun StoriesComponent(
    modifier: Modifier = Modifier,
    stories: List<StoryUI> = emptyList(),
    feedUpdateState: FeedUpdateState = FeedUpdateState.Idle,
    onUpdateClick: () -> Unit = {},
    onStoryClick: (StoryUI) -> Unit = {},
    onBookmarkClick: (String, Boolean) -> Unit = { _, _ -> },
    onTryAgainClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .animateContentSize()
    ) {

        when (feedUpdateState) {
            FeedUpdateState.Available -> AvailableUpdateBar(onUpdateClick = onUpdateClick)
            FeedUpdateState.InProgress -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            is FeedUpdateState.Error -> {
                when (feedUpdateState.error) {
                    StateProductionError.NoInternet,
                    StateProductionError.EndpointError -> NetworkUnavailableElement(
                        onActionButtonClick = onTryAgainClick
                    )

                    else -> UnknownErrorElement(onActionButtonClick = onTryAgainClick)
                }
            }

            else -> {}
        }

        StoriesList(
            stories = stories,
            onStoryClick = onStoryClick,
            onBookmarkClick = onBookmarkClick
        )
    }
}

@Composable
private fun StoriesList(
    modifier: Modifier = Modifier,
    stories: List<StoryUI>,
    onStoryClick: (StoryUI) -> Unit,
    onBookmarkClick: (String, Boolean) -> Unit
) {

    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
    ) {
        items(stories) { story ->
            StoryItem(story = story, onStoryClick = onStoryClick, onBookmarkClick = onBookmarkClick)
        }
    }
}

@Composable
fun StoryItem(
    modifier: Modifier = Modifier,
    story: StoryUI,
    onBookmarkClick: (String, Boolean) -> Unit = { _, _ -> },
    onStoryClick: (StoryUI) -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(50))
            .clickable {
                onStoryClick(story)
            },

        elevation = CardDefaults.cardElevation(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (image, title, abstract, favorite) = createRefs()

            ItemCommonAsyncImage(
                modifier = Modifier
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.value(200.dp)
                    },
                imageUrl = story.imageUrl,
                contentDescription = story.title,
            )

            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp)
                    .constrainAs(title) {
                        top.linkTo(image.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(favorite.start, margin = 4.dp)
                        width = Dimension.fillToConstraints
                    },
                text = story.title,
                style = MaterialTheme.typography.titleMedium
            )

            IconToggleButton(
                modifier = Modifier
                    .padding(end = 4.dp, top = 4.dp)
                    .constrainAs(favorite) {
                        start.linkTo(title.end)
                        top.linkTo(image.bottom)
                        end.linkTo(parent.end)
                    },
                checked = story.favorite,
                onCheckedChange = { onBookmarkClick(story.id, story.favorite) }
            ) {
                if (story.favorite) {
                    Icon(Icons.Rounded.BookmarkRemove, contentDescription = "add to favorites")
                } else {
                    Icon(
                        Icons.Outlined.BookmarkAdd,
                        contentDescription = "remove from favorites"
                    )
                }
            }

            ExpandableText(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .paddingFromBaseline(24.dp)
                    .constrainAs(abstract) {
                        top.linkTo(title.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                    },
                collapsedMaxLine = 3,
                text = story.abstract,
                onUnAnnotatedTextClick = { onStoryClick(story) }
            )
        }
    }
}

@Composable
fun ExpandableToggleArea(
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {

        AnimatedVisibility(
            visible = !expanded,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            startY = 30f,
                            colors = listOf(
                                Color.Transparent,
                                Color.LightGray
                            )
                        ),
                        alpha = 0.5f
                    )
            )
        }

        val arrowRotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (!expanded) {
                Text(text = "more", style = MaterialTheme.typography.labelSmall)
            }

            Icon(
                modifier = Modifier
                    .rotate(arrowRotation),
                imageVector = Icons.Default.ArrowDropDown,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Expand"
            )
        }
    }
}

@Composable
fun AvailableUpdateBar(onUpdateClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        Color.Transparent
                    )
                )
            )
            .clickable(onClick = onUpdateClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Updates available",
            style = MaterialTheme.typography.labelLarge
        )
        Icon(
            modifier = Modifier.padding(end = 16.dp),
            imageVector = Icons.Default.Refresh,
            contentDescription = "Update"
        )
    }
}

@Preview
@Composable
fun StoriesScreenPreview() {
    NYTNewsTheme(darkTheme = false) {
        StoriesComponent(stories = fakeStoriesUiList, feedUpdateState = FeedUpdateState.Available)
    }
}

@Preview
@Composable
fun StoriesScreenDarkPreview() {
    NYTNewsTheme(darkTheme = true) {
        StoriesComponent(stories = fakeStoriesUiList, feedUpdateState = FeedUpdateState.InProgress)
    }
}

@Preview
@Composable
fun StoryItemPreview_LongAbstract() {
    NYTNewsTheme {
        StoryItem(story = fakeStory)
    }
}

@Preview
@Composable
fun StoryItemPreview_ShortAbstract() {
    NYTNewsTheme {
        StoryItem(story = fakeStory.copy(favorite = true))
    }
}

@Preview(widthDp = 360, heightDp = 40)
@Composable
fun ExpandableToggleAreaCollapsedPreview() {
    NYTNewsTheme() {
        ExpandableToggleArea()
    }
}

@Preview(widthDp = 360, heightDp = 40)
@Composable
fun ExpandableToggleAreaExpandedPreview() {
    NYTNewsTheme() {
        ExpandableToggleArea(expanded = true)
    }
}