@file:OptIn(ExperimentalMaterial3Api::class)

package com.mls.mor.nytnews.ui.topics

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mls.mor.nytnews.R
import com.mls.mor.nytnews.data.topics.TopicsType
import com.mls.mor.nytnews.data.topics.defaultTopics
import com.mls.mor.nytnews.ui.common.AboutUsDialog
import com.mls.mor.nytnews.ui.common.CustomCollapsingToolbarContainer
import com.mls.mor.nytnews.ui.common.CustomScrollableTabRow
import com.mls.mor.nytnews.ui.common.EmailChooserMenu
import com.mls.mor.nytnews.ui.common.collapseAppBar
import com.mls.mor.nytnews.ui.settings.AppSettingsDialog
import com.mls.mor.nytnews.ui.settings.ContactUsDialog
import com.mls.mor.nytnews.ui.theme.NYTNewsTheme
import com.mls.mor.nytnews.ui.theme.appScreenGradientBackground
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
        dialogSelector = uiState.dialogSelector,
        onStoryClick = onStoryClick,
        onPopularStoryClick = onPopularStoryClick,
        onBookmarkClick = { id, bookmarked -> viewModel.updateBookmark(id, bookmarked) },
        onTopicsChooserDialogDismiss = { viewModel.updateTopics(it) },
        onTryAgainClick = { viewModel.reloadCurrentTopic() },
        onShowDialogClick = { viewModel.showDialog(it) },
        onDismissDialogClick = { viewModel.dismissDialog() },
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
    dialogSelector: DialogSelector = DialogSelector.None,
    onPageChange: (Int) -> Unit = {},
    onStoryClick: (StoryUI) -> Unit = {},
    onPopularStoryClick: (PopularUi) -> Unit = {},
    onBookmarkClick: (String, Boolean) -> Unit = { _, _ -> },
    onTopicsChooserDialogDismiss: (List<TopicsType>) -> Unit = {},
    onTryAgainClick: () -> Unit = {},
    onShowDialogClick: (DialogSelector) -> Unit = {},
    onDismissDialogClick: () -> Unit = {},
) {

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    var showInterestsSelectionDialog by remember { mutableStateOf(false) }
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

    BoxWithConstraints(
        modifier = modifier.appScreenGradientBackground()
    ) {

        // calculate the height of the collapsing toolbar based on the screen height
        val collapsingToolbarHeight by remember {
            derivedStateOf { calculateCollapsingToolbarHeight(maxHeight) }
        }

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = Color.Transparent,
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
                        HomeTopAppBar(
                            showMainMenu = dialogSelector == DialogSelector.MainMenuDropdown,
                            onMenuClick = { onShowDialogClick(DialogSelector.MainMenuDropdown) },
                            onLogoClick = { onShowDialogClick(DialogSelector.AboutUs) },
                            onDismissMenu = { onDismissDialogClick() },
                            onSettingClick = { onShowDialogClick(DialogSelector.Settings) },
                            onAboutUsClick = { onShowDialogClick(DialogSelector.AboutUs) },
                            onContactUsClick = { onShowDialogClick(DialogSelector.ContactUs) },
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
            Column(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {

                TopicsGeneralDialogsPresenter(
                    dialogSelector = dialogSelector,
                    onShowDialogClick = onShowDialogClick,
                    onDismiss = onDismissDialogClick
                )

                if (showInterestsSelectionDialog) {
                    InterestsBottomSheetDialog(
                        onDismiss = { updated, topics ->
                            if (updated) {
                                coroutineScope.launch { pagerState.animateScrollToPage(0) }
                            }
                            onTopicsChooserDialogDismiss(topics)
                            showInterestsSelectionDialog = false
                        },
                        selectedTopics = topicsType
                    )
                }

                Column {

                    InterestBar(onShowTopicsSelectionDialog = { showInterestsSelectionDialog = true })

                    InterestTabsRow(
                        topicsType,
                        pagerState.currentPage,
                        onTabClick = { page ->
                            appBarState.collapseAppBar()

                            coroutineScope.launch {
                                pagerState.animateScrollToPage(page)
                            }
                        }
                    )
                }

                HorizontalPager(
                    pageCount = topicsType.size,
                    state = pagerState,
                    beyondBoundsPageCount = 1
                ) {
                    StoriesComponent(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp),
                        stories = storiesList[topicsType[it]] ?: emptyList(),
                        feedUpdateState = feedUpdateStates[topicsType[it]] ?: FeedUpdateState.Idle,
                        onStoryClick = onStoryClick,
                        onBookmarkClick = onBookmarkClick,
                        onTryAgainClick = onTryAgainClick
                    )
                }
            }
        }
    }
}

@Composable
private fun InterestTabsRow(
    topicsType: List<TopicsType> = defaultTopics,
    currentSelectedPageIndex: Int = 0,
    onTabClick: (Int) -> Unit = {},
) {
    val context = LocalContext.current

    CustomScrollableTabRow(
        selectedTabIndex = currentSelectedPageIndex,
        containerColor = Color.Transparent,
        edgePadding = 8.dp,
        indicator = {},
        divider = {}
    ) {
        topicsType.forEachIndexed { index, topicsType ->
            val selected = index == currentSelectedPageIndex
            val interestString by remember(key1 = topicsType) {
                derivedStateOf { interestEnumToStringResources(context, topicsType) }
            }

            Tab(
                modifier = Modifier.padding(horizontal = 6.dp),
                selected = selected,
                onClick = { onTabClick(index) },
            ) {
                CustomTabCell(interestString, selected)
            }
        }
    }
}

@Composable
private fun InterestBar(
    modifier: Modifier = Modifier,
    onShowTopicsSelectionDialog: () -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(
            text = stringResource(R.string.what_you_curious_about),
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(
            modifier = Modifier.padding(end = 4.dp),
            onClick = onShowTopicsSelectionDialog
        ) {
            Icon(
                painter = painterResource(id = R.drawable.tabler_edit_24dp),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = stringResource(R.string.edit_topics_content_description)
            )
        }
    }
}

@Composable
private fun TopicsGeneralDialogsPresenter(
    dialogSelector: DialogSelector,
    onShowDialogClick: (DialogSelector) -> Unit = {},
    onDismiss: () -> Unit = {},
) {

    when (dialogSelector) {
        DialogSelector.AboutUs -> {
            AboutUsDialog(onDismiss = { onDismiss() })
        }

        DialogSelector.ContactUs -> {
            ContactUsDialog(
                onDismiss = { onDismiss() },
                onEmailClick = {
                    onShowDialogClick(DialogSelector.EmailChooser)
                },
            )
        }

        DialogSelector.Settings -> {
            AppSettingsDialog(onDismiss = { onDismiss() })
        }

        DialogSelector.EmailChooser -> {
            EmailChooserMenu(recipient = stringResource(id = R.string.app_contact_email_address))
            onShowDialogClick(DialogSelector.None)
        }

        else -> {}
    }

}

@Composable
private fun CustomTabCell(
    text: String,
    selected: Boolean,
) {
    Surface(
        modifier = Modifier,
        color = Color.Transparent,
        shape = RoundedCornerShape(30)
    ) {

        Box(
            modifier = Modifier
                .then(
                    if (selected) {
                        Modifier.background(
                            MaterialTheme.colorScheme.primary,
                        )
                    } else {
                        Modifier
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(30)
                            )
                    }
                ), contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Text(
                    text = text,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

private fun calculateCollapsingToolbarHeight(containerHeight: Dp): Dp {
    return when {
        containerHeight < 480.dp -> containerHeight * 0.75f
        containerHeight < 640.dp -> containerHeight * 0.5f
        containerHeight < 800.dp -> containerHeight * 0.43f
        else -> containerHeight * 0.33f
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
    NYTNewsTheme {
        InterestsBottomSheetDialog(
            selectedTopics = defaultTopics,
            onDismiss = { updated, topics ->
                Log.d("TAG", "TopicsDialogSelectionPreview: $updated $topics")
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InterestBarPreview() {
    NYTNewsTheme {
        InterestBar()
    }
}

@Preview(showBackground = true)
@Composable
private fun InterestTabRowPreview() {
    NYTNewsTheme {
        Box(modifier = Modifier.padding(vertical = 16.dp)) {
            InterestTabsRow()
        }
    }
}