package com.example.mor.nytnews.ui.search

import android.content.res.Resources.NotFoundException
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BookmarkRemove
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mor.nytnews.R
import com.example.mor.nytnews.ui.common.ExpandableText
import com.example.mor.nytnews.ui.common.LottieAnimationElement
import retrofit2.HttpException

private const val TAG = "SearchScreen"

@Composable
fun SearchRoute(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchItems = viewModel.searchResults.collectAsLazyPagingItems()
    val lastSearchItems = viewModel.lastSearchItems.collectAsStateWithLifecycle()
    val interestsItems = viewModel.interestsList.collectAsStateWithLifecycle()
    val recommendedItems = viewModel.recommendedList.collectAsStateWithLifecycle()

    SearchScreen(
        modifier = modifier.fillMaxSize(),
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
        onBookmarkClick = { storyId, isBookmarked ->
//                viewModel.onBookmarkClick(storyId, isBookmarked)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchItems: LazyPagingItems<SearchUiModel>,
    lastSearchItems: List<SearchUiModel> = emptyList(),
    recommendedSearchItems: List<SearchUiModel> = emptyList(),
    interestsSearchItems: List<SearchUiModel> = emptyList(),
    showStartSearchAnimation: Boolean = true,
    onSearchClick: (String) -> Unit = {},
    onSearchItemClick: (String) -> Unit = {},
    onBookmarkClick: (String, Boolean) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var query by rememberSaveable { mutableStateOf("") }
        var active by rememberSaveable { mutableStateOf(false) }

        val imeController = LocalSoftwareKeyboardController.current

        SearchBar(
            modifier = Modifier,
            query = query,
            onQueryChange = { query = it },
            onSearch = {
                active = true
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
                Icon(imageVector = Icons.Rounded.Mic, contentDescription = "voice")
            },
            placeholder = { Text(text = stringResource(id = R.string.search)) },
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
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            items(count = searchItems.itemCount) {
                                val story = searchItems[it]
                                story?.let { storyItem: SearchUiModel ->
                                    SearchStoryItem(
                                        modifier = Modifier,
                                        story = storyItem,
                                        onBookmarkClick = onBookmarkClick
                                    )
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
    onSearchItemClick: (String) -> Unit,
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
    onBookmarkClick: (String, Boolean) -> Unit = { _, _ -> },
    onStoryClick: (SearchUiModel) -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(50))
            .clickable {
                onStoryClick(story)
            },
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (image, title, abstract, favorite) = createRefs()

            AsyncImage(
                modifier = Modifier
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.value(180.dp)
                    },
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
                text = story.abstract
            )

            FilledIconToggleButton(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .padding(top = 16.dp)
                    .constrainAs(favorite) {
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                        end.linkTo(parent.end)
                    },
                checked = story.bookmarked,
                onCheckedChange = { onBookmarkClick(story.id, story.bookmarked) }
            ) {
                if (story.bookmarked) {
                    Icon(Icons.Rounded.BookmarkRemove, contentDescription = "add to favorites")
                } else {
                    Icon(
                        Icons.Outlined.BookmarkAdd,
                        contentDescription = "remove from favorites"
                    )
                }
            }
        }
    }
}
