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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grabgully.app.ui.components.GullyBottomNav
import com.grabgully.app.ui.components.GullyTab
import com.grabgully.app.ui.theme.*

// ── Mock data model (replace with Supabase query in Session 6) ────────────────
data class LeaderboardEntry(
    val rank:       Int,
    val username:   String,
    val xp:         Int,
    val badge:      String,      // "bronze" | "silver" | "gold" | "platinum"
    val totalSaved: Double,      // Total rupees saved this month
    val isCurrentUser: Boolean = false,
)

private val mockLeaderboard = listOf(
    LeaderboardEntry(1, "DealKing_Rahul",   12400, "platinum", 45200.0),
    LeaderboardEntry(2, "SavingsQueen",     10800, "gold",     38900.0),
    LeaderboardEntry(3, "BargainBhai",       9200, "gold",     31500.0),
    LeaderboardEntry(4, "TechHunter99",      7600, "silver",   24800.0),
    LeaderboardEntry(5, "MeeshoMaven",       6800, "silver",   19200.0),
    LeaderboardEntry(6, "You (Jatin)",       4200, "bronze",   12400.0, isCurrentUser = true),
    LeaderboardEntry(7, "SaleSeeker",        3800, "bronze",   10900.0),
    LeaderboardEntry(8, "FlipkartFan",       3200, "bronze",    8200.0),
    LeaderboardEntry(9, "DealSniper",        2900, "bronze",    7100.0),
    LeaderboardEntry(10,"OfferObsessed",     2400, "bronze",    5800.0),
)

/**
 * Leaderboard Screen — monthly XP rankings.
 *
 * Layout:
 *  ─ Podium (top 3) with animated glow rings
 *  ─ LazyColumn of rank rows 4–10
 *  ─ Sticky "Your rank" card at bottom
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    currentRoute: String             = "leaderboard",
    onTabSelect:  (GullyTab) -> Unit = {},
) {
    Scaffold(
        containerColor = ObsidianBlack,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, null, tint = GoldPrimary, modifier = Modifier.size(26.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Bazaar Kings", style = MaterialTheme.typography.headlineMedium, color = GoldPrimary)
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GoldSurface,
                        border = BorderStroke(0.5.dp, GoldPrimary),
                    ) {
                        Text(
                            "This Month",
                            style    = MaterialTheme.typography.labelSmall,
                            color    = GoldPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDeep),
            )
        },
        bottomBar = {
            GullyBottomNav(currentRoute = currentRoute, onTabSelect = onTabSelect)
        },
    ) { padding ->
        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            // ── Podium (top 3) ────────────────────────────────────────────
            item {
                PodiumSection(top3 = mockLeaderboard.take(3))
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(12.dp))
            }

            // ── Rank list (4–10) ──────────────────────────────────────────
            itemsIndexed(
                items = mockLeaderboard.drop(3),
                key   = { _, item -> item.rank },
            ) { _, entry ->
                RankRow(entry = entry)
                HorizontalDivider(
                    color    = DividerColor.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}


// ── Podium ────────────────────────────────────────────────────────────────────

@Composable
private fun PodiumSection(top3: List<LeaderboardEntry>) {
    if (top3.size < 3) return

    // Pulsing glow animation for #1
    val glowAnim = rememberInfiniteTransition(label = "glow")
    val glowAlpha by glowAnim.animateFloat(
        initialValue  = 0.4f,
        targetValue   = 0.9f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label         = "glow_alpha",
    )

    Column(
        modifier            = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("🏆 This Month's Champions", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
        Spacer(Modifier.height(20.dp))

        // Podium layout: 2nd | 1st | 3rd
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.Bottom,
        ) {
            PodiumSlot(entry = top3[1], height = 80.dp, crownColor = BadgeSilver)
            PodiumSlot(entry = top3[0], height = 110.dp, crownColor = BadgeGold, glowAlpha = glowAlpha)
            PodiumSlot(entry = top3[2], height = 60.dp, crownColor = BadgeBronze)
        }
    }
}

@Composable
private fun PodiumSlot(
    entry:      LeaderboardEntry,
    height:     androidx.compose.ui.unit.Dp,
    crownColor: Color,
    glowAlpha:  Float = 0f,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        // Crown emoji
        Text(
            text  = when (entry.rank) { 1 -> "👑"; 2 -> "🥈"; else -> "🥉" },
            fontSize = if (entry.rank == 1) 28.sp else 22.sp,
        )
        Spacer(Modifier.height(4.dp))

        // Avatar circle with glow for #1
        Box(contentAlignment = Alignment.Center) {
            if (entry.rank == 1 && glowAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(GoldPrimary.copy(alpha = glowAlpha), Color.Transparent)
                                )
                            )
                        }
                )
            }
            Box(
                modifier          = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(crownColor.copy(alpha = 0.8f), SurfaceRaised))
                    )
                    .border(2.dp, crownColor, CircleShape),
                contentAlignment  = Alignment.Center,
            ) {
                Text(
                    text  = entry.username.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text  = entry.username.take(10),
            style = MaterialTheme.typography.bodySmall,
            color = if (entry.rank == 1) GoldPrimary else TextSecondary,
        )
        Text(
            text  = "${entry.xp} XP",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
        )

        Spacer(Modifier.height(6.dp))

        // Podium bar
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(height)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(crownColor.copy(alpha = 0.6f), SurfaceRaised)
                    )
                )
                .border(
                    0.5.dp,
                    crownColor.copy(alpha = 0.4f),
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = "#${entry.rank}",
                style = MaterialTheme.typography.titleLarge,
                color = crownColor,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}


// ── Rank row ──────────────────────────────────────────────────────────────────

@Composable
private fun RankRow(entry: LeaderboardEntry) {
    val badgeColor = when (entry.badge) {
        "platinum" -> BadgePlatinum
        "gold"     -> BadgeGold
        "silver"   -> BadgeSilver
        else       -> BadgeBronze
    }

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .then(
                if (entry.isCurrentUser)
                    Modifier.background(GoldSurface)
                else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Rank number
        Text(
            text      = "#${entry.rank}",
            style     = MaterialTheme.typography.titleMedium,
            color     = if (entry.isCurrentUser) GoldPrimary else TextMuted,
            modifier  = Modifier.width(36.dp),
            fontWeight = FontWeight.Bold,
        )

        // Avatar
        Box(
            modifier         = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(SurfaceHighlight)
                .border(1.5.dp, badgeColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = entry.username.take(2).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = if (entry.isCurrentUser) "${entry.username} (You)" else entry.username,
                style = MaterialTheme.typography.bodyMedium,
                color = if (entry.isCurrentUser) GoldPrimary else TextPrimary,
                fontWeight = if (entry.isCurrentUser) FontWeight.SemiBold else FontWeight.Normal,
            )
            Text(
                "Saved ₹${"%.0f".format(entry.totalSaved)}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
        }

        // XP + badge
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "${entry.xp} XP",
                style     = MaterialTheme.typography.labelLarge,
                color     = if (entry.isCurrentUser) GoldPrimary else TextSecondary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                entry.badge.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = badgeColor,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF08080F)
@Composable
private fun LeaderboardPreview() {
    GrabGullyTheme { LeaderboardScreen() }
}
