package com.example.mor.nytnews.ui.topics

import com.example.mor.nytnews.ui.common.StateProductionError

sealed interface FeedUpdateState {
    object Available : FeedUpdateState
    object Idle : FeedUpdateState
    object InProgress : FeedUpdateState
    data class Error(val error: StateProductionError) : FeedUpdateState
}