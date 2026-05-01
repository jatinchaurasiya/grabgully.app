package com.grabgully.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grabgully.app.ui.theme.*

enum class GullyTab(
    val route:     String,
    val icon:      ImageVector,
    val iconSelected: ImageVector,
) {
    HOME(
        route        = "home",
        icon         = Icons.Outlined.Home,
        iconSelected = Icons.Filled.Home,
    ),
    SEARCH(
        route        = "search",
        icon         = Icons.Outlined.Search,
        iconSelected = Icons.Filled.Search,
    ),
    TRACK(
        route        = "track",
        icon         = Icons.Outlined.Visibility,
        iconSelected = Icons.Filled.Visibility,
    ),
    LEADERBOARD(
        route        = "leaderboard",
        icon         = Icons.Outlined.EmojiEvents,
        iconSelected = Icons.Filled.EmojiEvents,
    ),
    PROFILE(
        route        = "profile",
        icon         = Icons.Outlined.Person,
        iconSelected = Icons.Filled.Person,
    ),
}

/**
 * Premium Floating Bottom Navigation Bar (Teal & Green Light Theme).
 *
 * Design matches inspiration image:
 * - Floating black pill shape
 * - Just icons (no text labels)
 * - Active state: Mint Green circular background with black icon
 * - Inactive state: Muted white/gray icon, no background
 */
@Composable
fun GullyBottomNav(
    currentRoute: String,
    onTabSelect:  (GullyTab) -> Unit,
    modifier:     Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Surface(
            color    = FloatingNavBg,
            shape    = RoundedCornerShape(50),
            modifier = Modifier.shadow(
                elevation = 24.dp,
                shape     = RoundedCornerShape(50),
                spotColor = TealDark.copy(alpha = 0.2f),
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                GullyTab.entries.forEach { tab ->
                    val selected = currentRoute == tab.route
                    
                    val bgColor = animateColorAsState(
                        targetValue   = if (selected) MintGreen else Color.Transparent,
                        animationSpec = tween(300),
                        label         = "nav_bg_${tab.route}",
                    )
                    
                    val iconColor = animateColorAsState(
                        targetValue   = if (selected) FloatingNavBg else TextMuted,
                        animationSpec = tween(300),
                        label         = "nav_icon_${tab.route}",
                    )

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(bgColor.value)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication        = null,
                            ) { onTabSelect(tab) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector        = if (selected) tab.iconSelected else tab.icon,
                            contentDescription = tab.route,
                            tint               = iconColor.value,
                            modifier           = Modifier.size(24.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F6F8)
@Composable
private fun BottomNavPreview() {
    GrabGullyTheme {
        GullyBottomNav(currentRoute = "home", onTabSelect = {})
    }
}
