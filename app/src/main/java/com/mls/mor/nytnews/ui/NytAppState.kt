package com.mls.mor.nytnews.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.mls.mor.nytnews.ui.bookmarks.navigateToBookmarks
import com.mls.mor.nytnews.ui.search.navigateToSearch
import com.mls.mor.nytnews.ui.topics.navigateToTopics
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberNytAppState(
    windowSizeClass: WindowSizeClass,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): NytAppState {
    return remember(
        navController,
        coroutineScope,
        windowSizeClass,
    ) {
        NytAppState(
            navController,
            coroutineScope,
            windowSizeClass,
        )
    }
}

@Stable
class NytAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val windowSizeClass: WindowSizeClass,
) {
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.values().asList()

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val shouldShowBottomBar: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar


    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.TOPICS -> navController.navigateToTopics(topLevelNavOptions)
            TopLevelDestination.SEARCH -> navController.navigateToSearch(topLevelNavOptions)
            TopLevelDestination.BOOKMARKS -> navController.navigateToBookmarks(topLevelNavOptions)
        }
    }

    override fun toString(): String {
        return "topLevelDestinations: $topLevelDestinations, windowSizeClass: $windowSizeClass"
    }

    fun navigateUp() {
        navController.navigateUp()
    }

    @Composable
    fun isCurrentDestinationTopLevel(): Boolean {
        return topLevelDestinations.any { topLevelDestination ->
            currentDestination?.hierarchy?.any {
                it.route?.contains(topLevelDestination.name, true) ?: false
            } ?: true
        }
    }
}