package com.mls.mor.nytnews.ui.search

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val searchRoute = "search_route"

fun NavHostController.navigateToSearch(navOptions: NavOptions? = null) {
    this.navigate(searchRoute, navOptions)
}

fun NavGraphBuilder.searchScreen(
    onSearchItemClick: (SearchUiModel) -> Unit = {},
    snackbarHostState: SnackbarHostState
) {
    composable(route = searchRoute) {
        SearchRoute(
            snackbarHostState = snackbarHostState,
            onSearchItemClick = onSearchItemClick)
    }
}