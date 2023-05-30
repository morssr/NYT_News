package com.example.mor.nytnews.ui.topics

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val topicsRoute = "topics_route"

fun NavHostController.navigateToTopics(navOptions: NavOptions? = null) {
    this.navigate(topicsRoute, navOptions)
}

fun NavGraphBuilder.topicsScreen(onTopicClick: (String) -> Unit = {}) {
    composable(route = topicsRoute) {
        TopicsScreen()
    }
}