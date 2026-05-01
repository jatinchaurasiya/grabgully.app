package com.grabgully.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.grabgully.app.ui.theme.*

/**
 * Generic empty state with Lottie animation + message + optional CTA.
 *
 * Usage:
 * ```kotlin
 * EmptyState(
 *     lottieRes  = R.raw.empty_search,
 *     message    = "Abhi kuch nahi track ho raha.",
 *     subMessage = "Shuru karo!",
 *     cta        = "Deals Dekho",
 *     onCtaClick = { navController.navigate("home") },
 * )
 * ```
 *
 * Lottie files to add to app/src/main/res/raw/:
 *   - empty_search.json    (magnifying glass)
 *   - empty_watchlist.json (empty box)
 *   - empty_trophy.json    (leaderboard)
 *
 * Download from lottiefiles.com — search "magnifying glass dark"
 */
@Composable
fun EmptyState(
    lottieRes:  Int,
    message:    String,
    subMessage: String    = "",
    cta:        String    = "",
    onCtaClick: () -> Unit = {},
    modifier:   Modifier  = Modifier,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
    val progress    by animateLottieCompositionAsState(
        composition = composition,
        iterations  = LottieConstants.IterateForever,
    )

    Column(
        modifier              = modifier
            .fillMaxWidth()
            .padding(32.dp),
        verticalArrangement   = Arrangement.Center,
        horizontalAlignment   = androidx.compose.ui.Alignment.CenterHorizontally,
    ) {
        LottieAnimation(
            composition = composition,
            progress    = { progress },
            modifier    = Modifier.size(200.dp),
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text  = message,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
        )

        if (subMessage.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text  = subMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }

        if (cta.isNotBlank()) {
            Spacer(Modifier.height(24.dp))
            androidx.compose.material3.Button(
                onClick = onCtaClick,
                colors  = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = TealPrimary,
                    contentColor   = androidx.compose.ui.graphics.Color.White,
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            ) {
                Text(
                    text  = cta,
                    style = MaterialTheme.typography.labelLarge,
                    color = androidx.compose.ui.graphics.Color.White,
                )
            }
        }
    }
}
