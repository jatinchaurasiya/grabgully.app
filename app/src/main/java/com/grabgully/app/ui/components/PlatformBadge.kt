package com.grabgully.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.grabgully.app.ui.theme.*

/**
 * Colored platform pill badge (Amazon orange, Flipkart blue, etc.)
 * Shown on top-left corner of DealCard images.
 */
@Composable
fun PlatformBadge(
    platform: String,
    modifier: Modifier = Modifier,
) {
    val (bg, label) = platformStyle(platform)
    Text(
        text     = label,
        style    = BadgeTextStyle,
        color    = Color.White,
        modifier = modifier
            .background(
                color = bg.copy(alpha = 0.92f),
                shape = RoundedCornerShape(6.dp),
            )
            .padding(horizontal = 6.dp, vertical = 3.dp),
    )
}

/** Returns (background color, display label) for a platform string. */
fun platformStyle(platform: String): Pair<Color, String> = when (platform.lowercase()) {
    "amazon"   -> AmazonColor   to "AMAZON"
    "flipkart" -> FlipkartColor to "FLIPKART"
    "myntra"   -> MyntraColor   to "MYNTRA"
    "meesho"   -> MeeshoColor   to "MEESHO"
    "ajio"     -> AjioColor     to "AJIO"
    "snapdeal" -> SnapdealColor to "SNAPDEAL"
    else       -> GoldMuted     to platform.uppercase()
}

@Preview
@Composable
private fun PlatformBadgePreview() {
    GrabGullyTheme {
        PlatformBadge(platform = "amazon")
    }
}
