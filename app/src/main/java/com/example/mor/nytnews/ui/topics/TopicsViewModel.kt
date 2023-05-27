package com.example.mor.nytnews.ui.topics

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mor.nytnews.IoDispatcher
import com.example.mor.nytnews.data.bookmarks.cache.BookmarkedStory
import com.example.mor.nytnews.data.bookmarks.cache.BookmarksRepository
import com.example.mor.nytnews.data.bookmarks.cache.toBookmarkedStory
import com.example.mor.nytnews.data.topics.TopicsRepository
import com.example.mor.nytnews.data.topics.TopicsType
import com.example.mor.nytnews.data.topics.cache.Story
import com.example.mor.nytnews.data.topics.defaultTopics
import com.example.mor.nytnews.data.topics.toTopicsString
import com.example.mor.nytnews.utilities.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "TopicsViewModel"
private const val KEY_TOPICS = "topics"

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class TopicsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val topicsRepository: TopicsRepository,
    private val bookmarksRepository: BookmarksRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopicsUiState(
        topics = savedStateHandle.get<String>(KEY_TOPICS)?.let {
            TopicsType.listFromString(it)
        } ?: defaultTopics))

    val uiState: StateFlow<TopicsUiState> = _uiState.asStateFlow()

    private val currentTopic = MutableStateFlow(TopicsType.HOME)

    init {
        topicsRepository.getMyTopicsListStream()
            .onEach { topicsList -> savedStateHandle[KEY_TOPICS] = topicsList.toTopicsString() }
            .onEach { topics -> _uiState.update { it.copy(topics = topics) } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = defaultTopics
            ).launchIn(viewModelScope)

        currentTopic
            .onEach { Log.d(TAG, "current topic changed to $it") }
            .debounce(350)
            .onEach { topic ->
                Log.d(TAG, "debounce finished and $topic is about to be loaded")
                val isCurrentFeedStateEmpty = _uiState.value.feedsStates[topic] == null
                if (isCurrentFeedStateEmpty) {
                    Log.d(TAG, "current feed state is empty, update to loading in progress")
                    _uiState.update { oldState -> updateToLoadingProgress(oldState, topic) }
                }
            }
            .flatMapLatest { topic ->
                val isUpdateRequired = topicsRepository.isTopicUpdateAvailable(topic, 30)
                if (isUpdateRequired) {
                    Log.d(TAG, "update is required for topic $topic. Load from remote")
                } else {
                    Log.d(TAG, "update is not required for topic $topic. Load from cache")
                }
                topicsRepository.getStoriesByTopicStream(topic, isUpdateRequired)
                    .combine(bookmarksRepository.getBookmarksStream()) { response, bookmarks ->
                        Log.d(TAG, "combine flows stories with bookmarked called")
                        createFeedUiState(response, bookmarks)
                    }
                    .onEach { Log.d(TAG, "new feed state is ready to emit $it") }
                    //map topic with the new feed state for further update
                    .map { topic to it }

            }.onEach { it: Pair<TopicsType, FeedUiState> ->
                Log.e(TAG, "feed state update: topic: ${it.first} state:${it.second}")
                _uiState.update { uiState -> updatedFeedStateByTopic(uiState, it.first, it.second) }
            }
            .flowOn(dispatcher)
            .launchIn(viewModelScope)
    }

    fun updateTopics(topics: List<TopicsType>) {
        Log.d(TAG, "updateTopic() called with: topics = $topics")
        viewModelScope.launch(dispatcher) {
            topicsRepository.updateMyTopicsList(topics)
        }
    }

    fun refreshCurrentTopic(topic: TopicsType) {
        Log.d(TAG, "refreshCurrentTopic() called with: topic = $topic")
        currentTopic.value = topic
    }

    private fun createFeedUiState(
        response: Response<List<Story>>,
        bookmarks: List<BookmarkedStory>
    ) = FeedUiState(
        updateState = FeedUpdateState.Idle,
        error = if (response is Response.Failure) response.error.message else null,
        stories = if (response is Response.Success) {
            response.data.map { story ->
                val bookmarked = bookmarks.any { it.id == story.id }
                story.toStoryUI(bookmarked = bookmarked)
            }
        } else {
            emptyList()
        })

    private fun updateToLoadingProgress(
        oldState: TopicsUiState,
        topic: TopicsType
    ) = oldState.copy(feedsStates = oldState.feedsStates.toMutableMap().apply {
        put(
            topic,
            oldState.feedsStates[topic]?.copy(updateState = FeedUpdateState.InProgress)
                ?: FeedUiState(updateState = FeedUpdateState.InProgress)
        )
    })

    private fun updatedFeedStateByTopic(
        oldUiState: TopicsUiState,
        topic: TopicsType,
        newFeedState: FeedUiState
    ): TopicsUiState {
        return oldUiState.copy(feedsStates = oldUiState.feedsStates.toMutableMap().apply {
            put(topic, newFeedState)
        }
        )
    }

    fun updateBookmark(id: String, bookmarked: Boolean) {
        Log.d(TAG, "updateBookmark() called with: id = $id, bookmarked = $bookmarked")
        viewModelScope.launch(dispatcher) {
            val story = topicsRepository.getStoryById(id)
            if (bookmarked) {
                bookmarksRepository.deleteBookmarkById(story.id)
            } else {
                bookmarksRepository.saveBookmarks(listOf(story.toBookmarkedStory()))
            }
        }
    }
}

data class TopicsUiState(
    val topics: List<TopicsType> = listOf(TopicsType.HOME),
    val feedsStates: Map<TopicsType, FeedUiState> = emptyMap(),
    val currentStableTopic: TopicsType = TopicsType.HOME,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class FeedUiState(
    val stories: List<StoryUI> = emptyList(),
    val updateState: FeedUpdateState = FeedUpdateState.Idle,
    val error: String? = null
)
