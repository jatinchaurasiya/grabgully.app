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
import androidx.compose.ui.draw.shadow
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

/**
 * Profile Screen — XP stats, gamification, settings, sign-out (v4.0 Teal Light Theme).
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
        containerColor = BackgroundLight,
        topBar = {
            Surface(
                color          = GlassBackground,
                tonalElevation = 0.dp,
            ) {
                Column {
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .height(56.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "My Profile",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = PlusJakartaSansFamily,
                            color      = TealPrimary,
                        )
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { viewModel.toggleSignOutDialog(true) }) {
                            Icon(Icons.Default.Logout, "Sign Out", tint = AlertRed)
                        }
                    }
                    HorizontalDivider(color = DividerColor)
                }
            }
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
            // ── Hero header ──────────────────────────────────────────────
            ProfileHeroSection(uiState = uiState)

            Spacer(Modifier.height(16.dp))

            // ── Stats bento grid ─────────────────────────────────────────
            StatsGrid(uiState = uiState)

            Spacer(Modifier.height(20.dp))

            // ── Savings dashboard (Teal Card from inspiration) ───────────
            SavingsDashboard(uiState = uiState)

            Spacer(Modifier.height(20.dp))

            // ── Referral card ────────────────────────────────────────────
            ReferralCard()

            Spacer(Modifier.height(20.dp))

            // ── Settings list ────────────────────────────────────────────
            SettingsList(onSignOut = { viewModel.toggleSignOutDialog(true) })

            Spacer(Modifier.height(120.dp))
        }
    }

    // Sign-out confirmation dialog
    if (uiState.showSignOut) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleSignOutDialog(false) },
            containerColor   = SurfaceLight,
            title            = { Text("Sign Out?", color = TextPrimary) },
            text             = { Text("Wapas aana! Deals wait karengi.", color = TextSecondary) },
            confirmButton    = {
                TextButton(onClick = viewModel::signOut) {
                    Text("Haan, Logout", color = AlertRed)
                }
            },
            dismissButton    = {
                TextButton(onClick = { viewModel.toggleSignOutDialog(false) }) {
                    Text("Ruko!", color = TealPrimary)
                }
            }
        )
    }
}


// ── Hero header (Light theme) ─────────────────────────────────────────────────

@Composable
private fun ProfileHeroSection(uiState: ProfileUiState) {
    val xpForNextLevel = 1000 * (uiState.level + 1)
    val xpProgress     = (uiState.xp % 1000).toFloat() / 1000f

    Column(
        modifier            = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // XP Ring around avatar
        XpProgressRing(
            progress  = xpProgress,
            level     = uiState.level,
            initials  = uiState.userName.take(2).uppercase(),
            size      = 100.dp,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text       = uiState.userName,
            fontSize   = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = PlusJakartaSansFamily,
            color      = TextPrimary,
        )
        Text(
            text  = uiState.userEmail,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )

        Spacer(Modifier.height(12.dp))

        // Badge + streak row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BadgeChip(badge = uiState.badge)
            if (uiState.streak > 0) {
                StreakChip(streak = uiState.streak)
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
        // Animated teal/mint arc
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
            // Teal progress arc
            drawArc(
                brush       = Brush.sweepGradient(listOf(MintGreen, TealPrimary, MintGreen)),
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
                .background(SurfaceLight),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = initials,
                fontSize   = (size.value * 0.28).sp,
                color      = TealPrimary,
                fontWeight = FontWeight.Bold,
            )
        }

        // Level badge
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(28.dp),
            shape  = CircleShape,
            color  = TealPrimary,
            border = BorderStroke(2.dp, BackgroundLight)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text       = "$level",
                    fontSize   = 12.sp,
                    color      = Color.White,
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
        "gold"     -> TealPrimary   to "🥇"
        "silver"   -> BadgeSilver   to "🥈"
        else       -> BadgeBronze   to "🥉"
    }
    Surface(
        shape  = RoundedCornerShape(20.dp),
        color  = color.copy(alpha = 0.1f),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.3f)),
    ) {
        Text(
            "$emoji ${badge.replaceFirstChar { it.uppercase() }}",
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = InterFamily,
            color      = color,
            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun StreakChip(streak: Int) {
    Surface(
        shape  = RoundedCornerShape(20.dp),
        color  = MintLight,
        border = BorderStroke(0.5.dp, MintGreen),
    ) {
        Text(
            "🔥 $streak day streak",
            fontSize   = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = InterFamily,
            color      = MintDark,
            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}


// ── Stats grid (Light bento cards) ───────────────────────────────────────────

@Composable
private fun StatsGrid(uiState: ProfileUiState) {
    val stats = listOf(
        Triple("₹${"%.0f".format(uiState.totalSaved)}", "Total Saved", TealPrimary),
        Triple("${uiState.dealsFound}", "Deals Found", TealPrimary),
        Triple("#${uiState.rank}", "Your Rank", TealPrimary),
        Triple("${uiState.streak}d", "Streak", MintDark),
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
        modifier = modifier.shadow(4.dp, RoundedCornerShape(16.dp), spotColor = SoftShadow),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceLight),
    ) {
        Column(
            modifier            = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text       = value,
                fontSize   = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSansFamily,
                color      = color,
                maxLines   = 1,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text     = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = InterFamily,
                color    = TextSecondary,
            )
        }
    }
}


// ── Savings dashboard (Teal Card matching inspiration) ────────────────────────

@Composable
private fun SavingsDashboard(uiState: ProfileUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = TealPrimary.copy(alpha = 0.3f)),
        shape  = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = TealPrimary),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text          = "Savings Monthly",
                    fontSize      = 12.sp,
                    fontWeight    = FontWeight.Medium,
                    fontFamily    = PlusJakartaSansFamily,
                    color         = TealLight,
                )
                Icon(Icons.Default.MoreHoriz, null, tint = TealLight)
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Highlight Mint Green Pill
            Surface(
                color = MintGreen,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Savings", color = MintDark, fontWeight = FontWeight.SemiBold)
                    Text("₹${"%.0f".format(uiState.totalSaved)}", color = MintDark, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = SurfaceLight,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Electronics", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("₹8,060", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = TealDark,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Fashion", color = TealLight, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("₹3,100", color = TealLight, fontSize = 14.sp)
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            // Performance / Take home pay equivalent
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Target Complete", color = TealLight, fontSize = 12.sp)
                    Text("98%", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                }
                Text(
                    "₹12,400",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
    }
}


// ── Referral card ─────────────────────────────────────────────────────────────

@Composable
private fun ReferralCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = SoftShadow),
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        border = BorderStroke(1.dp, MintGreen.copy(alpha = 0.5f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MintLight)
                .padding(20.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎁", fontSize = 32.sp)
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Dost Ko Bulao!",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PlusJakartaSansFamily,
                        color      = MintDark,
                    )
                    Text(
                        "Har referral pe 200 XP",
                        fontSize = 12.sp,
                        color    = MintDark.copy(alpha = 0.8f),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { /* Share intent */ },
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = MintDark,
                        contentColor   = Color.White,
                    ),
                    shape = RoundedCornerShape(50),
                ) {
                    Text("Share", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


// ── Settings list (Light theme) ───────────────────────────────────────────────

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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).shadow(4.dp, RoundedCornerShape(24.dp), spotColor = SoftShadow),
        shape    = RoundedCornerShape(24.dp),
        colors   = CardDefaults.cardColors(containerColor = SurfaceLight),
    ) {
        Column {
            items.forEachIndexed { index, item ->
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = item.onClick)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Icon in circle
                    Surface(
                        shape    = CircleShape,
                        color    = if (item.tint == AlertRed) AlertBg else InactiveChipBg,
                        modifier = Modifier.size(40.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(item.icon, null, tint = item.tint, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        item.label,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = InterFamily,
                        color      = if (item.tint == AlertRed) AlertRed else TextPrimary,
                        modifier   = Modifier.weight(1f),
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        null,
                        tint     = TextMuted,
                        modifier = Modifier.size(20.dp),
                    )
                }
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        color    = DividerColor,
                        modifier = Modifier.padding(start = 72.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F6F8)
@Composable
private fun ProfilePreview() {
    GrabGullyTheme { ProfileScreen() }
}
