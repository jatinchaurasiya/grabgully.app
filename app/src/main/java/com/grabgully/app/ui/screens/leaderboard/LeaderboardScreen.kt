package com.grabgully.app.ui.screens.leaderboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grabgully.app.ui.components.GullyBottomNav
import com.grabgully.app.ui.components.GullyTab
import com.grabgully.app.ui.screens.onboarding.OnboardingScreen
import com.grabgully.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Leaderboard Screen — gamification and community rankings (v4.0 Teal Light Theme).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    currentRoute: String             = "leaderboard",
    onTabSelect:  (GullyTab) -> Unit = {},
    viewModel:    LeaderboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Enforce login
    if (!uiState.isLoggedIn) {
        OnboardingScreen(onSignedIn = { /* viewModel auto updates */ })
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
                            "Top Jugaadis",
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = PlusJakartaSansFamily,
                            color      = TealPrimary,
                        )
                        Spacer(Modifier.weight(1f))
                        // Info button
                        IconButton(onClick = { /* show rules */ }) {
                            Icon(Icons.Default.Info, "Rules", tint = TealPrimary)
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
        LazyColumn(
            modifier          = Modifier.fillMaxSize().padding(padding),
            contentPadding    = PaddingValues(bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Podium section (Top 3) ───────────────────────────────────────
            item {
                Spacer(Modifier.height(24.dp))
                if (uiState.topThree.size >= 3) {
                    BentoPodium(
                        rank2 = uiState.topThree[1],
                        rank1 = uiState.topThree[0],
                        rank3 = uiState.topThree[2],
                    )
                }
                Spacer(Modifier.height(32.dp))
            }

            // ── Community Impact Card ────────────────────────────────────────
            item {
                CommunityImpactCard(totalSavings = "₹4,25,000")
                Spacer(Modifier.height(24.dp))
            }

            // ── Remaining leaderboard (4-10) ─────────────────────────────────
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        "THIS WEEK'S STARS",
                        fontSize      = 12.sp,
                        fontWeight    = FontWeight.Bold,
                        fontFamily    = PlusJakartaSansFamily,
                        color         = TextSecondary,
                        letterSpacing = 2.sp,
                    )
                }
            }

            itemsIndexed(uiState.remainingRanks, key = { _, user -> user.userId }) { index, user ->
                RankRowCard(
                    rank = index + 4,
                    name = user.name,
                    xp   = user.xp,
                    isCurrentUser = user.isCurrentUser,
                )
            }
        }
    }
}


// ── Bento Podium (Light Theme) ────────────────────────────────────────────────

@Composable
private fun BentoPodium(
    rank2: LeaderboardUser,
    rank1: LeaderboardUser,
    rank3: LeaderboardUser,
) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment     = Alignment.Bottom,
    ) {
        // Rank 2 (Silver)
        Box(modifier = Modifier.weight(1f).height(180.dp), contentAlignment = Alignment.BottomCenter) {
            PodiumCard(user = rank2, rank = 2, color = Color(0xFF9E9E9E), height = 140.dp)
            PodiumAvatar(initials = rank2.name.take(2), color = Color(0xFF9E9E9E), modifier = Modifier.align(Alignment.TopCenter))
        }

        // Rank 1 (Gold/Teal) — the highest
        Box(modifier = Modifier.weight(1.1f).height(220.dp), contentAlignment = Alignment.BottomCenter) {
            PodiumCard(user = rank1, rank = 1, color = TealPrimary, height = 180.dp, isWinner = true)
            PodiumAvatar(initials = rank1.name.take(2), color = TealPrimary, size = 64.dp, modifier = Modifier.align(Alignment.TopCenter))
        }

        // Rank 3 (Bronze)
        Box(modifier = Modifier.weight(1f).height(160.dp), contentAlignment = Alignment.BottomCenter) {
            PodiumCard(user = rank3, rank = 3, color = Color(0xFFCD7F32), height = 120.dp)
            PodiumAvatar(initials = rank3.name.take(2), color = Color(0xFFCD7F32), modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
private fun PodiumCard(
    user:     LeaderboardUser,
    rank:     Int,
    color:    Color,
    height:   androidx.compose.ui.unit.Dp,
    isWinner: Boolean = false,
) {
    // Pulse animation for the winner
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.3f,
        targetValue   = 0.6f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label         = "glowAlpha",
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .then(
                if (isWinner) Modifier.shadow(24.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), spotColor = color.copy(alpha = glowAlpha), ambientColor = color.copy(alpha = glowAlpha))
                else Modifier.shadow(12.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp), spotColor = SoftShadow)
            ),
        shape = RoundedCornerShape(topStart = if (isWinner) 24.dp else 20.dp, topEnd = if (isWinner) 24.dp else 20.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
        colors = CardDefaults.cardColors(containerColor = if (isWinner) TealLight else SurfaceLight),
        border = BorderStroke(if (isWinner) 2.dp else 1.dp, if (isWinner) TealPrimary else DividerColor),
    ) {
        Column(
            modifier            = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text       = user.name.split(" ").first(),
                fontSize   = if (isWinner) 16.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary,
                maxLines   = 1,
            )
            Spacer(Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = color.copy(alpha = 0.1f),
            ) {
                Text(
                    text       = "${user.xp} XP",
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color      = color,
                    modifier   = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text       = "#$rank",
                fontSize   = if (isWinner) 40.sp else 28.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSansFamily,
                color      = color.copy(alpha = if (isWinner) 1f else 0.5f),
            )
        }
    }
}

@Composable
private fun PodiumAvatar(
    initials: String,
    color:    Color,
    size:     androidx.compose.ui.unit.Dp = 52.dp,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.size(size).shadow(8.dp, CircleShape, spotColor = color),
        shape    = CircleShape,
        color    = SurfaceLight,
        border   = BorderStroke(3.dp, color),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text       = initials.uppercase(),
                fontSize   = (size.value * 0.35).sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PlusJakartaSansFamily,
                color      = color,
            )
        }
    }
}


// ── Community Impact Card ─────────────────────────────────────────────────────

@Composable
private fun CommunityImpactCard(totalSavings: String) {
    // Count up animation
    var targetValue by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        delay(300)
        targetValue = 425000f
    }
    val animatedSavings by animateFloatAsState(
        targetValue   = targetValue,
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label         = "savings",
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).shadow(16.dp, RoundedCornerShape(24.dp), spotColor = TealPrimary.copy(alpha = 0.2f)),
        shape    = RoundedCornerShape(24.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(TealPrimary, TealDark)))
                .padding(24.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    "COMMUNITY SAVINGS",
                    fontSize      = 10.sp,
                    fontWeight    = FontWeight.Bold,
                    fontFamily    = PlusJakartaSansFamily,
                    color         = TealLight,
                    letterSpacing = 2.sp,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "₹${"%,.0f".format(animatedSavings)}",
                    fontSize   = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = PlusJakartaSansFamily,
                    color      = Color.White,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Bachaye is hafte Grab Gully squad ne! 🎉",
                    fontSize = 12.sp,
                    color    = TealLight,
                )
            }
        }
    }
}


// ── Rank Row Card (v4.0 Teal Light Theme) ─────────────────────────────────────

@Composable
private fun RankRowCard(
    rank:          Int,
    name:          String,
    xp:            Int,
    isCurrentUser: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).shadow(if (isCurrentUser) 8.dp else 2.dp, RoundedCornerShape(16.dp), spotColor = SoftShadow),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = if (isCurrentUser) TealLight else SurfaceLight),
        border   = BorderStroke(1.dp, if (isCurrentUser) TealPrimary else DividerColor),
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Rank number
            Text(
                text       = "$rank",
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSansFamily,
                color      = if (isCurrentUser) TealPrimary else TextSecondary,
                modifier   = Modifier.width(32.dp),
                textAlign  = TextAlign.Center,
            )
            
            // Avatar (small)
            Surface(
                modifier = Modifier.size(36.dp).padding(start = 4.dp),
                shape    = CircleShape,
                color    = BackgroundLight,
                border   = BorderStroke(1.dp, DividerColor),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text       = name.take(2).uppercase(),
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color      = TextPrimary,
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Name
            Text(
                text       = if (isCurrentUser) "You ($name)" else name,
                fontSize   = 14.sp,
                fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                color      = TextPrimary,
                modifier   = Modifier.weight(1f),
            )

            // XP
            Text(
                text       = "$xp XP",
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PlusJakartaSansFamily,
                color      = if (isCurrentUser) TealPrimary else MintDark,
            )
        }
    }
}

// ── View Models & Data Models ─────────────────────────────────────────────────
// (Included for completeness of the screen preview, same as before)

data class LeaderboardUser(val userId: String, val name: String, val xp: Int, val isCurrentUser: Boolean = false)

class LeaderboardViewModel : androidx.lifecycle.ViewModel() {
    val uiState = kotlinx.coroutines.flow.MutableStateFlow(LeaderboardUiState())

    init {
        // Mock data
        val users = listOf(
            LeaderboardUser("1", "Rahul Sharma", 15400),
            LeaderboardUser("2", "Priya Singh", 14200),
            LeaderboardUser("3", "Amit Kumar", 13800),
            LeaderboardUser("4", "Neha Gupta", 12100),
            LeaderboardUser("5", "Vikas Patel", 11500),
            LeaderboardUser("6", "Jatin Chaurasiya", 10800, true),
            LeaderboardUser("7", "Sanjay Verma", 9200),
        )
        uiState.value = LeaderboardUiState(
            isLoggedIn     = true,
            topThree       = users.take(3),
            remainingRanks = users.drop(3),
        )
    }
}

data class LeaderboardUiState(
    val isLoggedIn:     Boolean = true,
    val topThree:       List<LeaderboardUser> = emptyList(),
    val remainingRanks: List<LeaderboardUser> = emptyList(),
)

@Preview(showBackground = true, backgroundColor = 0xFFF4F6F8)
@Composable
private fun LeaderboardPreview() {
    GrabGullyTheme { LeaderboardScreen() }
}
