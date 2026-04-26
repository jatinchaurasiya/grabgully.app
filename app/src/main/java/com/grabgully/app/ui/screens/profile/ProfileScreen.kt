package com.grabgully.app.ui.screens.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grabgully.app.ui.components.GullyBottomNav
import com.grabgully.app.ui.components.GullyTab
import com.grabgully.app.ui.screens.onboarding.OnboardingScreen
import com.grabgully.app.ui.theme.*
import kotlin.math.PI

/**
 * Profile Screen — XP stats, gamification, settings, sign-out.
 *
 * Shows:
 * - Avatar with animated XP ring
 * - Level, badge, streak
 * - Stats grid (Total Saved, Deals Found, Rank, Streak)
 * - Settings items (Notifications, Privacy, Share App, Rate Us, Sign Out)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentRoute: String             = "profile",
    onTabSelect:  (GullyTab) -> Unit = {},
    viewModel:    ProfileViewModel   = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // If not logged in, show Onboarding (Google Sign-In)
    if (!uiState.isLoggedIn) {
        OnboardingScreen(onSignedIn = { /* viewModel will auto-update */ })
        return
    }

    Scaffold(
        containerColor = ObsidianBlack,
        topBar = {
            TopAppBar(
                title = {
                    Text("My Profile", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSignOutDialog(true) }) {
                        Icon(Icons.Default.Logout, "Sign Out", tint = AlertRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDeep),
            )
        },
        bottomBar = {
            GullyBottomNav(currentRoute = currentRoute, onTabSelect = onTabSelect)
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Hero header with XP ring ───────────────────────────────────
            ProfileHeroSection(uiState = uiState)

            Spacer(Modifier.height(20.dp))

            // ── Stats grid ────────────────────────────────────────────────
            StatsGrid(uiState = uiState)

            Spacer(Modifier.height(24.dp))

            // ── Referral card ─────────────────────────────────────────────
            ReferralCard()

            Spacer(Modifier.height(24.dp))

            // ── Settings list ─────────────────────────────────────────────
            SettingsList(onSignOut = { viewModel.toggleSignOutDialog(true) })

            Spacer(Modifier.height(80.dp))
        }
    }

    // Sign-out confirmation dialog
    if (uiState.showSignOut) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleSignOutDialog(false) },
            containerColor   = SurfaceRaised,
            title            = { Text("Sign Out?", color = TextPrimary) },
            text             = { Text("Wapas aana! Deals wait karengi.", color = TextSecondary) },
            confirmButton    = {
                TextButton(onClick = viewModel::signOut) {
                    Text("Haan, Logout", color = AlertRed)
                }
            },
            dismissButton    = {
                TextButton(onClick = { viewModel.toggleSignOutDialog(false) }) {
                    Text("Ruko!", color = GoldPrimary)
                }
            }
        )
    }
}


// ── Hero header ────────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeroSection(uiState: ProfileUiState) {
    val xpForNextLevel = 1000 * (uiState.level + 1)
    val xpProgress     = (uiState.xp % 1000).toFloat() / 1000f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(GoldSurface, ObsidianBlack))
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // XP Ring around avatar
            XpProgressRing(
                progress  = xpProgress,
                level     = uiState.level,
                initials  = uiState.userName.take(2).uppercase(),
                size      = 100.dp,
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text  = uiState.userName,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text  = uiState.userEmail,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )

            Spacer(Modifier.height(12.dp))

            // Badge + streak row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BadgeChip(badge = uiState.badge)
                if (uiState.streak > 0) {
                    StreakChip(streak = uiState.streak)
                }
            }

            Spacer(Modifier.height(8.dp))

            // XP progress bar
            Column(
                modifier            = Modifier.fillMaxWidth(0.7f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                LinearProgressIndicator(
                    progress         = { xpProgress },
                    modifier         = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                    color            = GoldPrimary,
                    trackColor       = DividerColor,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${uiState.xp} / $xpForNextLevel XP to Level ${uiState.level + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                )
            }
        }
    }
}


// ── XP Progress Ring ──────────────────────────────────────────────────────────

@Composable
fun XpProgressRing(
    progress:  Float,
    level:     Int,
    initials:  String,
    size:      androidx.compose.ui.unit.Dp = 90.dp,
    modifier:  Modifier = Modifier,
) {
    val animProg by animateFloatAsState(
        targetValue   = progress,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label         = "xp_ring",
    )

    Box(
        modifier         = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        // Animated gold arc
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            val inset  = 6.dp.toPx()
            // Background track
            drawArc(
                color       = DividerColor,
                startAngle  = -90f,
                sweepAngle  = 360f,
                useCenter   = false,
                style       = stroke,
                topLeft     = Offset(inset, inset),
                size        = androidx.compose.ui.geometry.Size(size.toPx() - inset * 2, size.toPx() - inset * 2),
            )
            // Gold progress arc
            drawArc(
                brush       = Brush.sweepGradient(listOf(GoldMuted, GoldPrimary, GoldBright)),
                startAngle  = -90f,
                sweepAngle  = 360f * animProg,
                useCenter   = false,
                style       = stroke,
                topLeft     = Offset(inset, inset),
                size        = androidx.compose.ui.geometry.Size(size.toPx() - inset * 2, size.toPx() - inset * 2),
            )
        }

        // Avatar circle
        Box(
            modifier         = Modifier
                .size(size - 16.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(GoldSurface, SurfaceRaised))),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = initials,
                fontSize   = (size.value * 0.28).sp,
                color      = GoldPrimary,
                fontWeight = FontWeight.Bold,
            )
        }

        // Level badge
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(22.dp),
            shape  = CircleShape,
            color  = GoldPrimary,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text     = "$level",
                    fontSize = 10.sp,
                    color    = ObsidianBlack,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}


// ── Badge & streak chips ──────────────────────────────────────────────────────

@Composable
private fun BadgeChip(badge: String) {
    val (color, emoji) = when (badge) {
        "platinum" -> BadgePlatinum to "💎"
        "gold"     -> BadgeGold     to "🥇"
        "silver"   -> BadgeSilver   to "🥈"
        else       -> BadgeBronze   to "🥉"
    }
    Surface(
        shape  = RoundedCornerShape(20.dp),
        color  = color.copy(alpha = 0.15f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.5f)),
    ) {
        Text(
            "$emoji ${badge.replaceFirstChar { it.uppercase() }}",
            style    = MaterialTheme.typography.labelSmall,
            color    = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun StreakChip(streak: Int) {
    Surface(
        shape  = RoundedCornerShape(20.dp),
        color  = Color(0xFF2C1A00),
        border = BorderStroke(0.5.dp, GoldMuted.copy(alpha = 0.5f)),
    ) {
        Text(
            "🔥 $streak day streak",
            style    = MaterialTheme.typography.labelSmall,
            color    = GoldPrimary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}


// ── Stats grid ────────────────────────────────────────────────────────────────

@Composable
private fun StatsGrid(uiState: ProfileUiState) {
    val stats = listOf(
        Triple("₹${"%.0f".format(uiState.totalSaved)}", "Total Saved", SavingsGreen),
        Triple("${uiState.dealsFound}", "Deals Found", GoldPrimary),
        Triple("#${uiState.rank}", "Your Rank", InfoBlue),
        Triple("${uiState.streak}d", "Streak", XpGold),
    )

    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        stats.forEach { (value, label, color) ->
            StatCard(
                value    = value,
                label    = label,
                color    = color,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceDeep),
    ) {
        Column(
            modifier            = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text       = value,
                style      = MaterialTheme.typography.titleLarge,
                color      = color,
                fontWeight = FontWeight.Bold,
                maxLines   = 1,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
            )
        }
    }
}


// ── Referral card ─────────────────────────────────────────────────────────────

@Composable
private fun ReferralCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GoldSurface),
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("🎁", fontSize = 32.sp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Dost Ko Bulao!", style = MaterialTheme.typography.titleMedium, color = GoldPrimary)
                Text(
                    "Har referral pe 200 XP aur unhe bhi 100 XP milega",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { /* Share intent */ },
                colors  = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBlack),
                shape   = RoundedCornerShape(10.dp),
            ) {
                Text("Share", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}


// ── Settings list ─────────────────────────────────────────────────────────────

@Composable
private fun SettingsList(onSignOut: () -> Unit) {
    data class SettingsItem(val icon: ImageVector, val label: String, val tint: Color = TextSecondary, val onClick: () -> Unit = {})

    val items = listOf(
        SettingsItem(Icons.Default.Notifications, "Price Alert Notifications"),
        SettingsItem(Icons.Default.Language, "Preferred Language"),
        SettingsItem(Icons.Default.IosShare, "Share App"),
        SettingsItem(Icons.Default.StarRate, "Rate Grab Gully"),
        SettingsItem(Icons.Default.Policy, "Privacy Policy"),
        SettingsItem(Icons.Default.Logout, "Sign Out", AlertRed, onSignOut),
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceDeep),
    ) {
        Column {
            items.forEachIndexed { index, item ->
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = item.onClick)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(item.icon, null, tint = item.tint, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(14.dp))
                    Text(item.label, style = MaterialTheme.typography.bodyMedium, color = if (item.tint == AlertRed) AlertRed else TextPrimary, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.ChevronRight, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                }
                if (index < items.lastIndex) {
                    HorizontalDivider(color = DividerColor.copy(alpha = 0.5f), modifier = Modifier.padding(start = 52.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF08080F)
@Composable
private fun ProfilePreview() {
    GrabGullyTheme { ProfileScreen() }
}
