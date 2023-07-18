@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.mor.nytnews.ui.topics

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mor.nytnews.R
import com.example.mor.nytnews.data.topics.TopicsType
import com.example.mor.nytnews.data.topics.defaultTopics
import com.example.mor.nytnews.ui.common.CustomCollapsingToolbarContainer
import com.example.mor.nytnews.ui.common.CustomRoundBorderTabIndicator
import com.example.mor.nytnews.ui.theme.NYTNewsTheme
import kotlinx.coroutines.launch

private const val TAG = "TopicsScreen"

@Composable
fun TopicsScreen(
    modifier: Modifier = Modifier,
    viewModel: TopicsViewModel = hiltViewModel(),
    onStoryClick: (StoryUI) -> Unit = {},
    onPopularStoryClick: (PopularUi) -> Unit = {},
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Log.d(TAG, "TopicsScreen: ${uiState.feedsStates}")
    TopicScreenComponent(
        modifier = modifier,
        topicsType = uiState.topics,
        storiesList = uiState.feedsStates.map { it.key to it.value.stories }.toMap(),
        popularsList = uiState.populars,
        onPageChange = { page ->
            Log.d("Page change", "Page changed to $page")
            viewModel.refreshCurrentTopic(uiState.topics[page])
        },
        feedUpdateStates = uiState.feedsStates.map { it.key to it.value.updateState }.toMap(),
        onStoryClick = onStoryClick,
        onPopularStoryClick = onPopularStoryClick,
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
    popularsList: List<PopularUi> = emptyList(),
    feedUpdateStates: Map<TopicsType, FeedUpdateState> = emptyMap(),
    onPageChange: (Int) -> Unit = {},
    onStoryClick: (StoryUI) -> Unit = {},
    onPopularStoryClick: (PopularUi) -> Unit = {},
    onBookmarkClick: (String, Boolean) -> Unit = { _, _ -> },
    onTopicsChooserDialogDismiss: (List<TopicsType>) -> Unit = {},
) {

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    var showTopicsSelectionDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val currentOnPageChange by rememberUpdatedState(onPageChange)

    LaunchedEffect(pagerState) {
        // Collect from the a snapshotFlow reading the currentPage
        snapshotFlow { pagerState.currentPage }.collect { page ->
            // Do something with each page change, for example:
            // viewModel.sendPageSelectedEvent(page)
            currentOnPageChange(page)
        }
    }

    val appBarState = rememberTopAppBarState()

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(appBarState)

    BoxWithConstraints() {
        // 40% of the screen height
        val collapsingToolbarHeight = maxHeight * 0.4f

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CustomCollapsingToolbarContainer(
                    initialHeight = collapsingToolbarHeight,
                    scrollBehavior = scrollBehavior,
                    alphaAnimation = true,
                )
                {
                    val showShimmer by remember(key1 = popularsList) { mutableStateOf(popularsList.isEmpty()) }

                    // returns true when the toolbar is fully collapsed
                    val appBarFullyCollapsed by remember {
                        derivedStateOf { appBarState.collapsedFraction > 0.99f }
                    }

                    PopularBarComponent(
                        modifier = Modifier.then(
                            // Hide the toolbar when the collapsing toolbar is fully collapsed.
                            // Workaround to fix the issue of consuming the touch events when collapsed.
                            if (appBarFullyCollapsed) {
                                Modifier.requiredHeight(0.dp)
                            } else {
                                Modifier.requiredHeight(collapsingToolbarHeight)
                            }
                        ),
                        onPopularStoryClick = onPopularStoryClick,
                        populars = popularsList,
                        shimmer = showShimmer
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
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
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = stringResource(R.string.edit_topics_content_description)
                                )
                            }
                        }
                    }

                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        edgePadding = 8.dp,
                        indicator = { tabPositions ->
                            CustomRoundBorderTabIndicator(
                                tabPositions,
                                pagerState.currentPage
                            )
                        },
                        divider = {}
                    ) {
                        topicsType.forEachIndexed { index, topicsType ->
                            val selected = index == pagerState.currentPage
                            Tab(
                                selected = selected,
                                text = {
                                    Text(
                                        text = topicsType.name,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                },
                                //                icon = { Icon(item.icon,  "")},
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            index
                                        )
                                    }
                                },
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
                        onStoryClick = onStoryClick,
                        onBookmarkClick = onBookmarkClick
                    )
                }
            }
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