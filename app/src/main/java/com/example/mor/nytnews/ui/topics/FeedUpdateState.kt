package com.example.mor.nytnews.ui.topics

sealed interface FeedUpdateState {
    object Available : FeedUpdateState
    object Idle : FeedUpdateState
    object InProgress : FeedUpdateState
}