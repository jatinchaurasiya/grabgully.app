package com.grabgully.app.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.grabgully.app.data.model.Deal
import com.grabgully.app.ui.components.GullyTab
import com.grabgully.app.ui.screens.compare.CompareScreen
import com.grabgully.app.ui.screens.home.HomeScreen
import com.grabgully.app.ui.screens.search.SearchScreen
import com.grabgully.app.ui.screens.track.TrackScreen
import com.grabgully.app.ui.screens.leaderboard.LeaderboardScreen
import com.grabgully.app.ui.screens.profile.ProfileScreen

/**
 * Main navigation graph for Grab Gully.
 *
 * Routes:
 *   home         → HomeScreen
 *   search       → SearchScreen (receives optional ?url= for URL paste mode)
 *   compare/{id} → CompareScreen (receives listing ID)
 *   track        → TrackScreen (watchlist)
 *   leaderboard  → LeaderboardScreen
 *   profile      → ProfileScreen
 *
 * Deep links from FCM:
 *   grabgully://compare/{listingId}
 */
@Composable
fun GullyNavGraph(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute   = backStackEntry?.destination?.route ?: GullyTab.HOME.route

    NavHost(
        navController    = navController,
        startDestination = GullyTab.HOME.route,
    ) {
        // ── Home ──────────────────────────────────────────────────────────
        composable(GullyTab.HOME.route) {
            HomeScreen(
                onDealClick     = { deal ->
                    navController.navigate("compare/${deal.id}")
                },
                onSearchClick   = { navController.navigate(GullyTab.SEARCH.route) },
                onUrlPasteClick = { navController.navigate("search?urlMode=true") },
                currentRoute    = currentRoute,
                onTabSelect     = { tab -> navController.navigate(tab.route) {
                    launchSingleTop = true
                    restoreState    = true
                    popUpTo(GullyTab.HOME.route) { saveState = true }
                }},
            )
        }

        // ── Search ────────────────────────────────────────────────────────
        composable(
            route = "${GullyTab.SEARCH.route}?urlMode={urlMode}",
            arguments = listOf(
                androidx.navigation.navArgument("urlMode") {
                    defaultValue = false
                    type         = androidx.navigation.NavType.BoolType
                }
            )
        ) { backStack ->
            val urlMode = backStack.arguments?.getBoolean("urlMode") ?: false
            SearchScreen(
                urlMode       = urlMode,
                onDealClick   = { deal -> navController.navigate("compare/${deal.id}") },
                onBackClick   = { navController.popBackStack() },
                currentRoute  = currentRoute,
                onTabSelect   = { tab -> navController.navigate(tab.route) {
                    launchSingleTop = true
                    restoreState    = true
                    popUpTo(GullyTab.HOME.route) { saveState = true }
                }},
            )
        }

        // Shortcut: navigation to search without params
        composable(GullyTab.SEARCH.route) {
            SearchScreen(
                onDealClick  = { deal -> navController.navigate("compare/${deal.id}") },
                onBackClick  = { navController.popBackStack() },
                currentRoute = currentRoute,
                onTabSelect  = { tab -> navController.navigate(tab.route) {
                    launchSingleTop = true; restoreState = true
                    popUpTo(GullyTab.HOME.route) { saveState = true }
                }},
            )
        }

        // ── Compare ───────────────────────────────────────────────────────
        composable(
            route     = "compare/{listingId}",
            arguments = listOf(
                androidx.navigation.navArgument("listingId") {
                    type = androidx.navigation.NavType.StringType
                }
            ),
            deepLinks = listOf(
                androidx.navigation.navDeepLink {
                    uriPattern = "grabgully://compare/{listingId}"
                }
            ),
        ) { backStack ->
            val listingId = backStack.arguments?.getString("listingId") ?: return@composable
            CompareScreen(
                listingId  = listingId,
                onBackClick = { navController.popBackStack() },
            )
        }

        // ── Track (Watchlist) ─────────────────────────────────────────────
        composable(GullyTab.TRACK.route) {
            TrackScreen(
                onDealClick  = { listingId -> navController.navigate("compare/$listingId") },
                currentRoute = currentRoute,
                onTabSelect  = { tab -> navController.navigate(tab.route) {
                    launchSingleTop = true; restoreState = true
                    popUpTo(GullyTab.HOME.route) { saveState = true }
                }},
            )
        }

        // ── Leaderboard ───────────────────────────────────────────────────
        composable(GullyTab.LEADERBOARD.route) {
            LeaderboardScreen(
                currentRoute = currentRoute,
                onTabSelect  = { tab -> navController.navigate(tab.route) {
                    launchSingleTop = true; restoreState = true
                    popUpTo(GullyTab.HOME.route) { saveState = true }
                }},
            )
        }

        // ── Profile ───────────────────────────────────────────────────────
        composable(GullyTab.PROFILE.route) {
            ProfileScreen(
                currentRoute = currentRoute,
                onTabSelect  = { tab -> navController.navigate(tab.route) {
                    launchSingleTop = true; restoreState = true
                    popUpTo(GullyTab.HOME.route) { saveState = true }
                }},
            )
        }
    }
}
