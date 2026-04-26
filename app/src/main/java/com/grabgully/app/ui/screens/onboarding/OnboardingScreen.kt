package com.grabgully.app.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grabgully.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Onboarding / Sign-In Screen
 *
 * Pages:
 *  1. "Har Deal Ka Baap." — hero splash with animated gold pulse
 *  2. "Compare Prices" — compare illustration
 *  3. "Never Miss a Deal" — track + alerts
 *
 * Then: Google Sign-In button via Supabase Auth.
 *
 * Note: Full Supabase Google OAuth flow is wired in Session 5.
 * This screen calls [onSignedIn] when the user is authenticated.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onSignedIn: () -> Unit = {},
) {
    val pages = listOf(
        OnboardingPage(
            emoji       = "🛒",
            title       = "Har Deal Ka Baap.",
            subtitle    = "India's most premium deal-discovery app.\nEk tap mein sabse sasta.",
            accentColor = GoldPrimary,
        ),
        OnboardingPage(
            emoji       = "🔍",
            title       = "Compare Every Price",
            subtitle    = "Amazon, Flipkart, Myntra, Meesho — we check\nevery platform so you don't have to.",
            accentColor = InfoBlue,
        ),
        OnboardingPage(
            emoji       = "🔔",
            title       = "Never Miss a Drop",
            subtitle    = "Set a target price. When it drops,\nwe'll ping you instantly.",
            accentColor = SavingsGreen,
        ),
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    var showSignIn by remember { mutableStateOf(false) }

    // Auto-advance pages
    LaunchedEffect(Unit) {
        repeat(pages.size - 1) {
            delay(3000)
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
        showSignIn = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
    ) {
        // Background radial glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors  = listOf(GoldSurface.copy(alpha = 0.6f), Color.Transparent),
                        radius  = 800f,
                    )
                )
        )

        Column(
            modifier            = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Brand header
            Spacer(Modifier.height(56.dp))
            Text(
                text     = "GRAB GULLY",
                style    = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp),
                color    = GoldPrimary,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(32.dp))

            // Pager
            HorizontalPager(
                state    = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                OnboardingPageContent(page = pages[page])
            }

            // Dot indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.padding(vertical = 16.dp),
            ) {
                repeat(pages.size) { index ->
                    val selected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(if (selected) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (selected) pages[pagerState.currentPage].accentColor
                                else DividerColor
                            )
                    )
                }
            }

            // Sign-In section
            AnimatedVisibility(
                visible = showSignIn,
                enter   = fadeIn() + slideInVertically { it / 2 },
            ) {
                SignInSection(onSignedIn = onSignedIn)
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    // Pulse animation for emoji
    val pulse = rememberInfiniteTransition(label = "emoji_pulse")
    val scale by pulse.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.08f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label         = "scale",
    )

    Column(
        modifier            = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Large emoji with pulse
        Text(
            text     = page.emoji,
            fontSize = (96 * scale).sp,
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text       = page.title,
            style      = MaterialTheme.typography.headlineLarge,
            color      = page.accentColor,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center,
            modifier   = Modifier.padding(horizontal = 32.dp),
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text      = page.subtitle,
            style     = MaterialTheme.typography.bodyLarge,
            color     = TextSecondary,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(horizontal = 40.dp),
        )
    }
}

@Composable
private fun SignInSection(onSignedIn: () -> Unit) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Google Sign-In button
        OutlinedButton(
            onClick  = {
                // TODO Session 5: Supabase Google OAuth launch
                // SupabaseClient.auth.signInWith(Google) { ... }
                onSignedIn()
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(14.dp),
            border   = BorderStroke(1.dp, DividerColor),
            colors   = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
        ) {
            Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text("Google Se Login Karo", style = MaterialTheme.typography.labelLarge)
        }

        // Guest mode
        TextButton(onClick = onSignedIn) {
            Text(
                "Guest ke tarah continue karo",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
        }

        Text(
            "Login karoge toh price alerts, watchlist\naur leaderboard milega!",
            style    = MaterialTheme.typography.bodySmall,
            color    = TextMuted,
            textAlign = TextAlign.Center,
        )
    }
}

private data class OnboardingPage(
    val emoji:       String,
    val title:       String,
    val subtitle:    String,
    val accentColor: Color,
)

@Preview(showBackground = true, backgroundColor = 0xFF08080F)
@Composable
private fun OnboardingPreview() {
    GrabGullyTheme { OnboardingScreen() }
}
