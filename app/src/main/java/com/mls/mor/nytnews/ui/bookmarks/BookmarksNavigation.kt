package com.mls.mor.nytnews.ui.bookmarks

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val bookmarksRoute = "bookmarks_route"

fun NavHostController.navigateToBookmarks(navOptions: NavOptions? = null) {
    this.navigate(bookmarksRoute, navOptions)
}

fun NavGraphBuilder.bookmarksScreen(onStoryClick: (story: BookmarkUi) -> Unit = {}) {
    composable(route = bookmarksRoute) {
        BookmarksRoute(onStoryClick = onStoryClick)
    }
}