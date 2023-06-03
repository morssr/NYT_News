package com.example.mor.nytnews.ui.bookmarks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.example.mor.nytnews.IoDispatcher
import com.example.mor.nytnews.MainLogger
import com.example.mor.nytnews.data.bookmarks.cache.BookmarksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BookmarksViewModel"

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookmarksRepository: BookmarksRepository,
    @MainLogger logger: Logger,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val log = logger.withTag(TAG)

    val uiState = bookmarksRepository.getBookmarksStream()
        .map { bookmarks ->
            BookmarksUiState(
                bookmarks = bookmarks.map { it.toBookmarkUi() }
            )
        }
        .flowOn(dispatcher)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = BookmarksUiState()
        )

    fun deleteBookmark(id: String) {
        log.d { "deleteBookmarkById() called with id: $id" }
        viewModelScope.launch(dispatcher) {
            bookmarksRepository.deleteBookmarkById(id)
        }
    }
}

data class BookmarksUiState(
    val bookmarks: List<BookmarkUi> = emptyList()
)