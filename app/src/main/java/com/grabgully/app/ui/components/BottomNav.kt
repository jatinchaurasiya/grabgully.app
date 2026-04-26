package com.grabgully.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grabgully.app.ui.theme.*

enum class GullyTab(
    val route:     String,
    val label:     String,
    val icon:      ImageVector,
    val iconSelected: ImageVector,
) {
    HOME(
        route        = "home",
        label        = "Home",
        icon         = Icons.Default.LocalFireDepartment,
        iconSelected = Icons.Default.LocalFireDepartment,
    ),
    SEARCH(
        route        = "search",
        label        = "Search",
        icon         = Icons.Default.Search,
        iconSelected = Icons.Default.Search,
    ),
    TRACK(
        route        = "track",
        label        = "Track",
        icon         = Icons.Default.Visibility,
        iconSelected = Icons.Default.Visibility,
    ),
    LEADERBOARD(
        route        = "leaderboard",
        label        = "Leaders",
        icon         = Icons.Default.EmojiEvents,
        iconSelected = Icons.Default.EmojiEvents,
    ),
    PROFILE(
        route        = "profile",
        label        = "Profile",
        icon         = Icons.Default.Person,
        iconSelected = Icons.Default.Person,
    ),
}

/**
 * Premium bottom navigation bar.
 *
 * Design tokens:
 * - Background: SurfaceDeep (#111119)
 * - Top border: DividerColor 1dp
 * - Active tab: GoldPrimary icon + label
 * - Inactive tab: TextMuted icon + no label
 */
@Composable
fun GullyBottomNav(
    currentRoute: String,
    onTabSelect:  (GullyTab) -> Unit,
    modifier:     Modifier = Modifier,
) {
    NavigationBar(
        containerColor = SurfaceDeep,
        tonalElevation = 0.dp,
        modifier = modifier.drawBehind {
            // Top border — 1dp DividerColor
            val borderWidth = 1.dp.toPx()
            drawLine(
                color       = DividerColor,
                start       = Offset(0f, 0f),
                end         = Offset(size.width, 0f),
                strokeWidth = borderWidth,
            )
        },
    ) {
        GullyTab.entries.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick  = { onTabSelect(tab) },
                icon     = {
                    Icon(
                        imageVector        = if (selected) tab.iconSelected else tab.icon,
                        contentDescription = tab.label,
                    )
                },
                label = {
                    Text(
                        text  = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor     = GoldPrimary,
                    selectedTextColor     = GoldPrimary,
                    unselectedIconColor   = TextMuted,
                    unselectedTextColor   = TextMuted,
                    indicatorColor        = GoldSurface,
                ),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111119)
@Composable
private fun BottomNavPreview() {
    GrabGullyTheme {
        GullyBottomNav(currentRoute = "home", onTabSelect = {})
    }
}
