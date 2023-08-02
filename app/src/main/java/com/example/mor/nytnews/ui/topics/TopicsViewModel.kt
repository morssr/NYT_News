package com.example.mor.nytnews.ui.topics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.example.mor.nytnews.IoDispatcher
import com.example.mor.nytnews.MainLogger
import com.example.mor.nytnews.data.bookmarks.cache.BookmarkedStory
import com.example.mor.nytnews.data.bookmarks.cache.BookmarksRepository
import com.example.mor.nytnews.data.bookmarks.cache.toBookmarkedStory
import com.example.mor.nytnews.data.popular.PopularRepository
import com.example.mor.nytnews.data.popular.api.PopularPeriod
import com.example.mor.nytnews.data.popular.common.PopularType
import com.example.mor.nytnews.data.topics.TopicsRepository
import com.example.mor.nytnews.data.topics.TopicsType
import com.example.mor.nytnews.data.topics.cache.Story
import com.example.mor.nytnews.data.topics.defaultTopics
import com.example.mor.nytnews.data.topics.toTopicsString
import com.example.mor.nytnews.ui.common.StateProductionError
import com.example.mor.nytnews.utilities.ApiResponseException
import com.example.mor.nytnews.utilities.ApiResponse
import com.example.mor.nytnews.utilities.BadResponseException
import com.example.mor.nytnews.utilities.NetworkException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
import java.net.UnknownHostException
import javax.inject.Inject

private const val TAG = "TopicsViewModel"
private const val KEY_TOPICS = "topics"

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class TopicsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val topicsRepository: TopicsRepository,
    private val bookmarksRepository: BookmarksRepository,
    private val popularRepository: PopularRepository,
    @MainLogger logger: Logger,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val log = logger.withTag(TAG)

    private val _uiState = MutableStateFlow(TopicsUiState(
        topics = savedStateHandle.get<String>(KEY_TOPICS)?.let {
            TopicsType.listFromString(it)
        } ?: defaultTopics))

    val uiState: StateFlow<TopicsUiState> = _uiState.asStateFlow()

    private val currentTopic = MutableStateFlow(TopicsType.HOME)

    init {
        log.v { "init called with dispatcher: $dispatcher" }
        topicsRepository.getMyTopicsListStream()
            .onEach { topicsList -> savedStateHandle[KEY_TOPICS] = topicsList.toTopicsString() }
            .onEach { topics -> _uiState.update { it.copy(topics = topics) } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = defaultTopics
            ).launchIn(viewModelScope)

        currentTopic
            .onEach { log.d { "current topic changed to $it" } }
            .debounce(350)
            .onEach { topic ->
                log.v { "debounce finished and $topic is about to be loaded" }
                val isCurrentFeedStateEmpty = _uiState.value.feedsStates[topic] == null
                if (isCurrentFeedStateEmpty) {
                    log.d { "current feed state is empty, update to loading in progress" }
                    _uiState.update { oldState -> updateToLoadingProgress(oldState, topic) }
                }
            }
            .flatMapLatest { topic ->
                val isUpdateRequired = checkIfTopicUpdateRequired(topic).also {
                    if (it) {
                        log.d { "update is required for topic $topic. Load from remote" }
                    } else {
                        log.d { "update is not required for topic $topic. Load from cache" }
                    }
                }

                topicsRepository.getStoriesByTopicStream(topic, isUpdateRequired)
                    .combine(bookmarksRepository.getBookmarksStream()) { response, bookmarks ->
                        log.v { "combine flows stories with bookmarked called" }
                        createFeedUiState(response, bookmarks)
                    }
                    .onEach { log.v { "new $topic feed state is ready to emit" } }
                    //map topic with the new feed state for further update
                    .map { topic to it }

            }
            .onEach { it: Pair<TopicsType, FeedUiState> ->
                log.d { "feed state update called for topic: ${it.first}" }
                _uiState.update { uiState -> updatedFeedStateByTopic(uiState, it.first, it.second) }
            }
            .flowOn(dispatcher)
            .launchIn(viewModelScope)

        popularRepository.getPopularsByTypeStream(
            type = PopularType.MOST_VIEWED,
            period = PopularPeriod.DAY,
            remoteSync = true
        )
            .map { it.toPopularUiList() }
            .onEach { _uiState.update { uiState -> uiState.copy(populars = it) } }
            .catch { throwable -> log.e { "get most popular stream returns error: $throwable" } }
            .flowOn(dispatcher)
            .launchIn(viewModelScope)
    }

    fun updateTopics(topics: List<TopicsType>) {
        log.d { "updateTopic() called with: topics = $topics" }
        viewModelScope.launch(dispatcher) {
            topicsRepository.updateMyTopicsList(topics)
        }
    }

    fun refreshCurrentTopic(topic: TopicsType) {
        log.d { "refreshCurrentTopic() called with: topic = $topic" }
        currentTopic.value = topic
    }

    private suspend fun checkIfTopicUpdateRequired(topic: TopicsType) =
        when (val response = topicsRepository.isTopicUpdateAvailable(topic, 30)) {
            is ApiResponse.Success -> {
                _uiState.update { it.copy(offlineMode = false) }
                response.data
            }

            is ApiResponse.Failure -> {
                log.e { "error while checking if update is required for topic $topic" }
                if (response.error is ApiResponseException || response.error is UnknownHostException) {
                    _uiState.update { it.copy(offlineMode = true) }
                }
                false
            }
        }

    private fun createFeedUiState(
        response: ApiResponse<List<Story>>,
        bookmarks: List<BookmarkedStory>
    ): FeedUiState {
        return when (val response = response) {
            is ApiResponse.Success -> {
                log.v { "createFeedUiState() called with: response success. stories list size is ${response.data.size}" }
                val storiesData = response.data.map { story ->
                    val bookmarked = bookmarks.any { it.id == story.id }
                    story.toStoryUI(bookmarked = bookmarked)
                }
                FeedUiState(
                    updateState = FeedUpdateState.Idle,
                    stories = storiesData
                )
            }

            is ApiResponse.Failure -> {
                log.w { "createFeedUiState() called with: response failure: ${response.error.message}. cached stories size: ${response.fallbackData?.size}" }
                val cachedStoriesData = response.fallbackData?.map { story ->
                    val bookmarked = bookmarks.any { it.id == story.id }
                    story.toStoryUI(bookmarked = bookmarked)
                } ?: emptyList()

                val failureUpdateState = FeedUpdateState.Error(
                    when (response.error) {
                        is NetworkException,
                        is UnknownHostException -> StateProductionError.NoInternet

                        is BadResponseException -> StateProductionError.EndpointError
                        else -> StateProductionError.Unknown
                    }
                )
                FeedUiState(
                    updateState = failureUpdateState,
                    stories = cachedStoriesData
                )
            }
        }
    }

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
    ) = oldUiState.copy(feedsStates = oldUiState.feedsStates.toMutableMap().apply {
        put(topic, newFeedState)
    }
    )

    fun updateBookmark(id: String, bookmarked: Boolean) {
        log.d { "updateBookmark() called with: id = $id, bookmarked = $bookmarked" }
        viewModelScope.launch(dispatcher) {
            val story = topicsRepository.getStoryById(id)
            if (bookmarked) {
                bookmarksRepository.deleteBookmarkById(story.id)
            } else {
                bookmarksRepository.saveBookmarks(listOf(story.toBookmarkedStory(currentTopic.value)))
            }
        }
    }
}

data class TopicsUiState(
    val currentStableTopic: TopicsType = TopicsType.HOME,
    val topics: List<TopicsType> = listOf(TopicsType.HOME),
    val feedsStates: Map<TopicsType, FeedUiState> = emptyMap(),
    val populars: List<PopularUi> = emptyList(),
    val isLoading: Boolean = false,
    val offlineMode: Boolean = false
)

data class FeedUiState(
    val stories: List<StoryUI> = emptyList(),
    val updateState: FeedUpdateState = FeedUpdateState.Idle,
)
