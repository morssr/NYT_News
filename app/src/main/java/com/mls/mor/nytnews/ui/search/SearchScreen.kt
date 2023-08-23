@file:OptIn(ExperimentalFoundationApi::class)

package com.mls.mor.nytnews.ui.search

import android.content.Context
import android.content.res.Resources.NotFoundException
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mls.mor.nytnews.R
import com.mls.mor.nytnews.ui.common.ExpandableText
import com.mls.mor.nytnews.ui.common.ItemCommonAsyncImage
import com.mls.mor.nytnews.ui.common.LottieAnimationElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

private const val TAG = "SearchScreen"

@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onSearchItemClick: (SearchUiModel) -> Unit = {},
) {
    val searchItems = viewModel.searchResults.collectAsLazyPagingItems()
    val lastSearchItems = viewModel.lastSearchItems.collectAsStateWithLifecycle()
    val interestsItems = viewModel.interestsList.collectAsStateWithLifecycle()
    val recommendedItems = viewModel.recommendedList.collectAsStateWithLifecycle()


    SearchScreen(
        modifier = modifier.fillMaxSize(),
        snackbarHostState = snackbarHostState,
        searchItems = searchItems,
        lastSearchItems = lastSearchItems.value,
        recommendedSearchItems = recommendedItems.value,
        interestsSearchItems = interestsItems.value,
        showStartSearchAnimation = listOf(
            lastSearchItems.value,
            recommendedItems.value,
            interestsItems.value
        ).any { it.isEmpty() },
        onSearchClick = { query -> viewModel.search(query) },
        onSearchItemClick = onSearchItemClick,
        onAddToBookmarksClick = { storyId ->
            viewModel.addToBookmarks(storyId)
        },
        onUndoAddToBookmarksClick = { storyId ->
            viewModel.removeFromBookmarks(storyId)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    searchItems: LazyPagingItems<SearchUiModel>,
    lastSearchItems: List<SearchUiModel> = emptyList(),
    recommendedSearchItems: List<SearchUiModel> = emptyList(),
    interestsSearchItems: List<SearchUiModel> = emptyList(),
    showStartSearchAnimation: Boolean = true,
    onSearchClick: (String) -> Unit = {},
    onSearchItemClick: (SearchUiModel) -> Unit = {},
    onAddToBookmarksClick: (id: String) -> Unit = {},
    onUndoAddToBookmarksClick: (id: String) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var query by rememberSaveable { mutableStateOf("") }
        var active by rememberSaveable { mutableStateOf(false) }

        val imeController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        SearchBar(
            modifier = Modifier.then(
                if (!active) Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp) else Modifier
            ),
            query = query,
            onQueryChange = { query = it },
            onSearch = {
                active = true
                focusManager.clearFocus()
                imeController?.hide()
                onSearchClick(query)
            },
            active = active,
            onActiveChange = {},
            leadingIcon = {
                if (active) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "back",
                        modifier = Modifier.clickable {
                            active = false
                        })
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "search"
                    )
                }

            },
            trailingIcon = {
                AnimatedVisibility(visible = query.isNotEmpty()) {
                    Icon(
                        modifier = Modifier
                            .scale(0.75f)
                            .clickable { query = "" },
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "clear"
                    )
                }

            },
            placeholder = { Text(text = stringResource(id = R.string.search_placeholder)) },
            colors = if (active) {
                SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    dividerColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                SearchBarDefaults.colors()
            },
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                when (searchItems.loadState.refresh) {
                    is LoadState.Loading -> {
                        LottieAnimationElement(animationPath = R.raw.lottie_searching_in_progress_animation)
                    }

                    is LoadState.Error -> {
                        val e = searchItems.loadState.refresh as LoadState.Error
                        when (e.error) {
                            is NotFoundException -> {
                                LottieAnimationElement(
                                    animationPath = R.raw.lottie_no_results_found,
                                    title = stringResource(R.string.no_results_found_message)
                                )
                            }

                            else -> {
                                LottieAnimationElement(
                                    animationPath = R.raw.lottie_something_went_wrong_animation,
                                    title = stringResource(R.string.something_went_wrong_message),
                                    extraBottomContent = {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(text = stringResource(R.string.check_your_internet_connection_message))
                                            Button(
                                                onClick = { searchItems.retry() },
                                                modifier = Modifier.padding(top = 16.dp)
                                            ) {
                                                Text(text = stringResource(R.string.retry))
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }

                    else -> {
                        LazyVerticalStaggeredGrid(
                            modifier = modifier,
                            columns = StaggeredGridCells.Adaptive(300.dp),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalItemSpacing = 16.dp
                            ) {

                            items(count = searchItems.itemCount) {
                                val story = searchItems[it]
                                story?.let { storyItem: SearchUiModel ->
                                    Column {
                                        SearchStoryItem(
                                            modifier = Modifier,
                                            story = storyItem,
                                            onStoryClick = onSearchItemClick,
                                            onBookmarkClick = { id ->
                                                onAddToBookmarksClick(id)
                                                showAddToBookmarksSnackbar(
                                                    scope,
                                                    snackbarHostState,
                                                    context,
                                                    onUndoAddToBookmarksClick,
                                                    id
                                                )
                                            }
                                        )

                                        Divider(
                                            modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                        )
                                    }
                                }
                            }

                            item {
                                FooterPagingListStateHandler(
                                    state = searchItems.loadState,
                                    onRetryClick = { searchItems.retry() }
                                )
                            }

                        }
                    }
                }
            }

            // Handle back button. If search is active, close it. Otherwise, navigate back.
            BackHandler {
                if (active) {
                    active = false
                }
            }

        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {

            //if search is not active show idle content
            if (showStartSearchAnimation) {
                LottieAnimationElement(
                    modifier = Modifier.weight(1f),
                    animationPath = R.raw.lottie_search_animation,
                    title = stringResource(R.string.search_for_something_message),
                )

            } else {
                SearchIdleContent(
                    lastSearchItems,
                    onSearchItemClick,
                    recommendedSearchItems,
                    interestsSearchItems
                )
            }
        }
    }
}

@Composable
private fun SearchIdleContent(
    lastSearchItems: List<SearchUiModel>,
    onSearchItemClick: (SearchUiModel) -> Unit,
    recommendedSearchItems: List<SearchUiModel>,
    interestsSearchItems: List<SearchUiModel>
) {
    HorizontalSearchItemsListElement(
        modifier = Modifier,
        title = stringResource(R.string.last_search),
        searchUiItems = lastSearchItems,
        onSearchItemClick = onSearchItemClick,
    )

    Spacer(modifier = Modifier.height(8.dp))

    HorizontalSearchItemsListElement(
        modifier = Modifier,
        title = stringResource(R.string.recommended_for_you),
        searchUiItems = recommendedSearchItems,
        onSearchItemClick = onSearchItemClick,
    )

    Spacer(modifier = Modifier.height(8.dp))

    HorizontalSearchItemsListElement(
        modifier = Modifier,
        title = stringResource(id = R.string.interests),
        searchUiItems = interestsSearchItems,
        onSearchItemClick = onSearchItemClick,
    )

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun FooterPagingListStateHandler(
    state: CombinedLoadStates,
    onRetryClick: () -> Unit = {},
) {
    Box {
        val appendState = state.append
        if (appendState is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (appendState is LoadState.Error) {
            val message = when (val error = appendState.error) {
                is HttpException -> {
                    when (error.code()) {
                        404 -> stringResource(R.string.no_results_found_message)
                        429 -> stringResource(R.string.too_many_requests_error_message)
                        else -> stringResource(R.string.something_went_wrong_message)
                    }
                }

                else -> stringResource(R.string.something_went_wrong_message)
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = message)

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = onRetryClick) {
                    Text(text = stringResource(id = R.string.retry))
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(id = R.string.retry)
                    )
                }
            }
        }
    }
}

@Composable
fun SearchStoryItem(
    modifier: Modifier = Modifier,
    story: SearchUiModel,
    onBookmarkClick: (String) -> Unit = {},
    onStoryClick: (SearchUiModel) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(50))
            .clickable {
                onStoryClick(story)
            },
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
                        height = Dimension.value(180.dp)
                    },
                imageUrl = story.imageUrl,
                contentDescription = story.title,
            )

            Text(
                modifier = Modifier
                    .paddingFromBaseline(top = 32.dp)
                    .padding(horizontal = 16.dp)
                    .constrainAs(title) {
                        top.linkTo(image.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                text = story.title,
                style = MaterialTheme.typography.titleLarge
            )

            ExpandableText(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .paddingFromBaseline(24.dp)
                    .constrainAs(abstract) {
                        top.linkTo(title.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(favorite.top)
                    },
                collapsedMaxLine = 2,
                text = story.abstract,
                onUnAnnotatedTextClick = { onStoryClick(story) }
            )

            IconButton(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .padding(top = 16.dp)
                    .constrainAs(favorite) {
                        bottom.linkTo(parent.bottom, margin = 8.dp)
                        end.linkTo(parent.end)
                    },
                onClick = { onBookmarkClick(story.id) }
            ) {
                Icon(
                    Icons.Outlined.BookmarkAdd,
                    contentDescription = "add to bookmarks list"
                )
            }
        }
    }
}

private fun showAddToBookmarksSnackbar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context,
    onUndoAddToBookmarksClick: (id: String) -> Unit,
    id: String
) {
    scope.launch {
        val snackbarResult = snackbarHostState.showSnackbar(
            message = context.getString(R.string.bookmark_added),
            actionLabel = context.getString(R.string.undo),
            duration = SnackbarDuration.Long
        )

        if (snackbarResult == SnackbarResult.ActionPerformed) {
            onUndoAddToBookmarksClick(id)
        }
    }
}
