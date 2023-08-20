package com.mls.mor.nytnews.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import co.touchlab.kermit.Logger
import com.mls.mor.nytnews.MainLogger
import com.mls.mor.nytnews.data.bookmarks.cache.BookmarksRepository
import com.mls.mor.nytnews.data.bookmarks.cache.toBookmarkedStory
import com.mls.mor.nytnews.data.search.SearchRepository
import com.mls.mor.nytnews.data.search.toSearchUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SearchViewModel"
private const val SEARCH_QUERY_KEY = "search_query"
private const val DEFAULT_SEARCH_QUERY_KEY = ""

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val bookmarksRepository: BookmarksRepository,
    @MainLogger logger: Logger,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val log = logger.withTag(TAG)

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults =
        savedStateHandle.getStateFlow(SEARCH_QUERY_KEY, DEFAULT_SEARCH_QUERY_KEY)
            .filter { query ->
                log.v { "searchResults change: filter blank/empty $query" }
                query.isNotBlank()
            }
            .flatMapLatest { query ->
                searchRepository.searchStoriesPagingSource(query).flow
                    .map { pagingData ->
                        pagingData.map { searchEntity ->
                            searchEntity.toSearchUiModel()
                        }
                    }
            }.cachedIn(viewModelScope)

    val lastSearchItems = searchRepository.getLastStoriesSearch()
        .map { it.toSearchUiModel() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val interestsList = searchRepository.getInterestsList()
        .map { it.toSearchUiModel() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recommendedList = searchRepository.getRecommendedList()
        .map { it.toSearchUiModel() }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun search(query: String) {
        log.d { "search called with query: $query" }
        savedStateHandle[SEARCH_QUERY_KEY] = query
    }

    fun addToBookmarks(storyId: String) {
        log.d { "onBookmarkClick called with storyId: $storyId" }
        viewModelScope.launch {
            val story = searchRepository.getStoryById(storyId)
            bookmarksRepository.saveBookmarks(listOf(story.toBookmarkedStory()))
        }
    }

    fun removeFromBookmarks(storyId: String) {
        log.d { "onBookmarkClick called with storyId: $storyId" }
        viewModelScope.launch {
            bookmarksRepository.deleteBookmarkById(storyId)
        }
    }
}