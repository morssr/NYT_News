@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.mor.nytnews.ui.topics

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
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
import com.example.mor.nytnews.ui.settings.AppSettingsDialog
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
    var showAppSettingsDialog by remember { mutableStateOf(false) }
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

        if (showAppSettingsDialog) {
            AppSettingsDialog(
                onDismiss = { showAppSettingsDialog = false },
            )
        }

        // 40% of the screen height
        val collapsingToolbarHeight = maxHeight * 0.42f

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

                    // returns true when the toolbar is fully collapsed
                    val appBarFullyCollapsed by remember {
                        derivedStateOf { appBarState.collapsedFraction > 0.99f }
                    }

                    Column(
                        modifier = Modifier.then(
                            // Hide the toolbar when the collapsing toolbar is fully collapsed.
                            // Workaround to fix the issue of consuming the touch events when collapsed.
                            if (appBarFullyCollapsed) {
                                Modifier.requiredHeight(0.dp)
                            } else {
                                Modifier.requiredHeight(collapsingToolbarHeight)
                            }
                        )
                    ) {

                        TopAppBar(
                            onSettingsClick = { showAppSettingsDialog = true },
                        )

                        val showShimmer by remember(key1 = popularsList) {
                            mutableStateOf(
                                popularsList.isEmpty()
                            )
                        }

                        PopularBarComponent(
                            modifier = Modifier.weight(1f),
                            onPopularStoryClick = onPopularStoryClick,
                            populars = popularsList,
                            shimmer = showShimmer
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                if (showTopicsSelectionDialog) {
                    TopicsInterestsDialog(
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

                Column(modifier = Modifier.animateContentSize()) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Text(
                            text = stringResource(R.string.what_you_curious_about),
                            modifier = Modifier.padding(start = 16.dp),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(
                            modifier = Modifier.padding(end = 4.dp),
                            onClick = { showTopicsSelectionDialog = true }) {
                            Icon(
                                Icons.Outlined.Edit,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = stringResource(R.string.edit_topics_content_description)
                            )
                        }
                    }

                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        edgePadding = 8.dp,
                        containerColor = MaterialTheme.colorScheme.background,
                        indicator = {},
                        divider = {}
                    ) {
                        topicsType.forEachIndexed { index, topicsType ->
                            val selected = index == pagerState.currentPage
                            Tab(
                                modifier = Modifier.padding(bottom = 8.dp),
                                selected = selected,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(
                                            index
                                        )
                                    }
                                },
                            ) {
                                CustomTabContent(selected, topicsType)
                            }
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

@Composable
private fun CustomTabContent(
    selected: Boolean,
    topicsType: TopicsType
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(30)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (selected) {
                        Modifier.background(
                            MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        Modifier
                    }
                ), contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Text(
                    text = topicsType.name,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                Text(
                    text = topicsType.name,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun TopAppBar(
    modifier: Modifier = Modifier,
    onLogoClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 8.dp, end = 8.dp)
    ) {
        IconButton(modifier = Modifier.align(Alignment.CenterStart), onClick = onLogoClick) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "app logo",
                modifier = Modifier.fillMaxSize()
            )
        }

        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = onSettingsClick
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "settings"
            )
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
        TopicsInterestsDialog(
            selectedTopics = defaultTopics,
            onDismiss = { updated, topics ->
                Log.d("TAG", "TopicsDialogSelectionPreview: $updated $topics")
            },
        )
    }
}