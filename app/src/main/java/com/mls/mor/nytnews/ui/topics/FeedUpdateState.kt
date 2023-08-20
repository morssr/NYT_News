package com.mls.mor.nytnews.ui.topics

import com.mls.mor.nytnews.ui.common.StateProductionError

sealed interface FeedUpdateState {
    object Available : FeedUpdateState
    object Idle : FeedUpdateState
    object InProgress : FeedUpdateState
    data class Error(val error: StateProductionError) : FeedUpdateState
}