package com.grabgully.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.grabgully.app.ui.navigation.GullyNavGraph
import com.grabgully.app.ui.theme.GrabGullyTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity — single activity host for the entire app.
 *
 * Responsibilities:
 * 1. Install the SplashScreen (androidx.core:core-splashscreen 1.0.1)
 *    → Shows Theme.GrabGully.SplashScreen with obsidian black bg + animated icon
 *    → Dismisses when Compose is ready
 * 2. Enable edge-to-edge rendering (transparent status + nav bars)
 * 3. Set the Compose content root:
 *    GrabGullyTheme { GullyNavGraph(navController) }
 *
 * The Hilt @AndroidEntryPoint annotation makes this a member-injection target.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // ── Splash screen — install BEFORE setContent ─────────────────────
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Edge-to-edge: Compose draws under status/nav bars
        enableEdgeToEdge()

        // Keep splash visible until Compose renders first frame
        // (Set to false immediately here; use splashScreen.setKeepOnScreenCondition
        //  if you need to wait for an async operation like auth check)
        splashScreen.setKeepOnScreenCondition { false }

        setContent {
            GrabGullyTheme {
                val navController = rememberNavController()
                GullyNavGraph(navController = navController)
            }
        }
    }
}
