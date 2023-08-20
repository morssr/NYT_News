@file:OptIn(ExperimentalLayoutApi::class)

package com.mls.mor.nytnews.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mls.mor.nytnews.ui.bookmarks.bookmarksScreen
import com.mls.mor.nytnews.ui.common.DisclaimerDialog
import com.mls.mor.nytnews.ui.common.NavArgumentsConstants
import com.mls.mor.nytnews.ui.common.webview.WebViewRoute
import com.mls.mor.nytnews.ui.common.webview.articleRoute
import com.mls.mor.nytnews.ui.common.webview.navigateToArticle
import com.mls.mor.nytnews.ui.search.searchScreen
import com.mls.mor.nytnews.ui.settings.SettingsViewModel
import com.mls.mor.nytnews.ui.theme.Icon
import com.mls.mor.nytnews.ui.topics.topicsRoute
import com.mls.mor.nytnews.ui.topics.topicsScreen

private const val TAG = "NytApp"

@Composable
fun NytApp(
    windowSizeClass: WindowSizeClass,
    appState: NytAppState = rememberNytAppState(windowSizeClass = windowSizeClass)
) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (appState.currentDestination.isCurrentDestinationTopLevel(appState.topLevelDestinations)) {
                NytBottomBar(
                    destinations = appState.topLevelDestinations,
                    onNavigateToDestination = appState::navigateToTopLevelDestination,
                    currentDestination = appState.currentDestination,
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {

            ShowDisclaimerDialogIfNeeded(settingsViewModel)

            NavHost(
                navController = appState.navController,
                startDestination = topicsRoute,
            ) {
                topicsScreen(
                    onStoryClick = {
                        appState.navController.navigateToArticle(
                            it.storyUrl,
                            it.title
                        )
                    },
                    onPopularStoryClick = {
                        appState.navController.navigateToArticle(
                            it.storyUrl,
                            it.title
                        )
                    }
                )

                searchScreen(
                    snackbarHostState = snackbarHostState,
                    onSearchItemClick = {
                        appState.navController.navigateToArticle(it.storyUrl, it.title)
                    }
                )

                bookmarksScreen(
                    onStoryClick = {
                        appState.navController.navigateToArticle(it.storyUrl, it.title)
                    }
                )

                composable(
                    route = articleRoute,
                    arguments = listOf(
                        navArgument(NavArgumentsConstants.URL_KEY) { type = NavType.StringType },
                        navArgument(NavArgumentsConstants.TITLE_KEY) { type = NavType.StringType },
                    )
                ) { backStackEntry ->
                    val url =
                        backStackEntry.arguments?.getString(NavArgumentsConstants.URL_KEY) ?: ""
                    val title =
                        backStackEntry.arguments?.getString(NavArgumentsConstants.TITLE_KEY) ?: ""

                    WebViewRoute(url = url, title = title, onBackClick = appState::navigateUp)
                }
            }
        }
    }
}

@Composable
private fun NytBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    val icon = if (selected) {
                        destination.selectedIcon
                    } else {
                        destination.unselectedIcon
                    }
                    when (icon) {
                        is Icon.ImageVectorIcon -> Icon(
                            imageVector = icon.imageVector,
                            contentDescription = stringResource(id = destination.iconTextId),
                        )

                        is Icon.DrawableResourceIcon -> Icon(
                            painter = painterResource(id = icon.id),
                            contentDescription = stringResource(id = destination.iconTextId)
                        )
                    }
                },
                label = { Text(stringResource(destination.iconTextId)) },
            )
        }
    }
}

@Composable
private fun ShowDisclaimerDialogIfNeeded(settingsViewModel: SettingsViewModel) {
    var showDisclaimerDialog by rememberSaveable { mutableStateOf(true) }

    val showDisclaimerPref =
        settingsViewModel.settingsUiState.collectAsStateWithLifecycle().value.showDisclaimer

    if (showDisclaimerDialog && showDisclaimerPref) {
        DisclaimerDialog(onDismiss = { dontShowAgainCheckbox: Boolean ->
            settingsViewModel.setShowDisclaimer(dontShowAgainCheckbox.not())
            showDisclaimerDialog = false
        })
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false


private fun NavDestination?.isCurrentDestinationTopLevel(topLevelDestinations: List<TopLevelDestination>): Boolean {
    return topLevelDestinations.any { topLevelDestination ->
        this?.hierarchy?.any {
            it.route?.contains(topLevelDestination.name, true) ?: false
        } ?: false
    }
}