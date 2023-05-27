@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.mor.nytnews.ui.topics

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mor.nytnews.data.topics.TopicsType
import com.example.mor.nytnews.data.topics.defaultTopics
import com.example.mor.nytnews.ui.theme.NYTNewsTheme
import kotlinx.coroutines.launch

private const val TAG = "TopicsScreen"

@Composable
fun TopicsScreen(
    modifier: Modifier = Modifier,
    viewModel: TopicsViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Log.d(TAG, "TopicsScreen: ${uiState.feedsStates}")
    TopicScreenComponent(
        modifier = modifier,
        topicsType = uiState.topics,
        storiesList = uiState.feedsStates.map { it.key to it.value.stories }.toMap(),
        onPageChange = { page ->
            Log.d("Page change", "Page changed to $page")
            viewModel.refreshCurrentTopic(uiState.topics[page])
        },
        feedUpdateStates = uiState.feedsStates.map { it.key to it.value.updateState }.toMap(),
        onBookmarkClick = { id, bookmarked -> viewModel.updateBookmark(id, bookmarked) },
        onTopicsChooserDialogDismiss = { viewModel.updateTopics(it) }
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun TopicScreenComponent(
    modifier: Modifier = Modifier,
    topicsType: List<TopicsType>,
    storiesList: Map<TopicsType, List<StoryUI>>,
    feedUpdateStates: Map<TopicsType, FeedUpdateState> = emptyMap(),
    onPageChange: (Int) -> Unit = {},
    onBookmarkClick: (String, Boolean) -> Unit = { _, _ -> },
    onTopicsChooserDialogDismiss: (List<TopicsType>) -> Unit = {},
) {

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    var showTopicsSelectionDialog by remember { mutableStateOf(false) }

    val currentOnPageChange by rememberUpdatedState(onPageChange)

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Do something with each page change, for example:
            // viewModel.sendPageSelectedEvent(page)
            currentOnPageChange(page)
        }
    }

    Column {

        if (showTopicsSelectionDialog) {
            TopicsSelectionDialog(
                onDismiss = { updated, topics ->
                    if (updated) {
                        coroutineScope.launch { pagerState.animateScrollToPage(0) }
                    }
                    onTopicsChooserDialogDismiss(topics)
                    showTopicsSelectionDialog = false
                },
                selectedTopics = topicsType
            )
        }

        Row(modifier = Modifier.animateContentSize()) {
            Surface {
                if (pagerState.currentPage < 3) {
                    IconButton(onClick = { showTopicsSelectionDialog = true }) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit topics")
                    }
                }
            }

            ScrollableTabRow(selectedTabIndex = pagerState.currentPage, edgePadding = 8.dp) {
                topicsType.forEachIndexed { index, topicsType ->
                    Tab(
                        selected = index == pagerState.currentPage,
                        text = { Text(text = topicsType.name) },
                        //                icon = { Icon(item.icon,  "")},
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    )
                }
            }
        }

        HorizontalPager(
            pageCount = topicsType.size,
            state = pagerState,
            beyondBoundsPageCount = 1
        ) {
            StoriesComponent(
                modifier = modifier.weight(1f),
                stories = storiesList[topicsType[it]] ?: emptyList(),
                feedUpdateState = feedUpdateStates[topicsType[it]] ?: FeedUpdateState.Idle,
                onBookmarkClick = onBookmarkClick
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopicsSelectionDialog(
    selectableTopics: List<TopicsType> = TopicsType.allTopics,
    selectedTopics: List<TopicsType> = defaultTopics,
    onDismiss: (updated: Boolean, updatedTopics: List<TopicsType>) -> Unit = { _, _ -> }
) {
    var newSelectedTopics by remember { mutableStateOf(selectedTopics) }

    ModalBottomSheet(onDismissRequest = {
        onDismiss(
            selectedTopics != newSelectedTopics,
            newSelectedTopics
        )
    }) {

        Text(
            text = "Your Topics",
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold
        )

        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            selectableTopics.forEach {

                val selected by remember(newSelectedTopics) {
                    mutableStateOf(
                        newSelectedTopics.contains(it)
                    )
                }

                FilterChip(
                    modifier = Modifier,
                    selected = selected,
                    onClick = {

                        if (it == TopicsType.HOME) {
                            return@FilterChip
                        }

                        newSelectedTopics = if (selected) {
                            newSelectedTopics.filter { topic -> topic != it }
                        } else {
                            newSelectedTopics + it
                        }
                    },

                    label = { Text(it.topicName.uppercase()) },
                    leadingIcon = if (selected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Selected",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    }
                )
            }

        }
    }
}

@Preview
@Composable
fun TopicsScreenPreview() {
    NYTNewsTheme {
        TopicScreenComponent(
            storiesList = fakeTopicsStoriesMap,
            topicsType = defaultTopics
        )
    }
}

@Preview
@Composable
fun TopicsDialogSelectionPreview() {
    NYTNewsTheme() {
        TopicsSelectionDialog(
            selectedTopics = defaultTopics,
            onDismiss = { updated, topics ->
                Log.d("TAG", "TopicsDialogSelectionPreview: $updated $topics")
            },
        )
    }
}