package com.example.mor.nytnews.ui.bookmarks

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val bookmarksRoute = "bookmarks_route"

fun NavHostController.navigateToBookmarks(navOptions: NavOptions? = null) {
    this.navigate(bookmarksRoute, navOptions)
}

fun NavGraphBuilder.bookmarksScreen() {
    composable(route = bookmarksRoute) {
        BookmarksRoute()
    }
}