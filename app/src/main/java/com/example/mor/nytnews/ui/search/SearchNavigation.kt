package com.example.mor.nytnews.ui.search

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val searchRoute = "search_route"

fun NavHostController.navigateToSearch(navOptions: NavOptions? = null) {
    this.navigate(searchRoute, navOptions)
}

fun NavGraphBuilder.searchScreen(onSearchItemClick: (SearchUiModel) -> Unit = {}) {
    composable(route = searchRoute) {
        SearchRoute(onSearchItemClick = onSearchItemClick)
    }
}