package com.grabgully.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grabgully.app.ui.theme.*

/**
 * Colored platform pill badge (Amazon orange, Flipkart blue, etc.)
 * v3.0: Compact rounded-full pill with shadow, 9sp extra-bold, tight tracking.
 */
@Composable
fun PlatformBadge(
    platform: String,
    modifier: Modifier = Modifier,
) {
    val (bg, label) = platformStyle(platform)
    Text(
        text          = label,
        fontSize      = 9.sp,
        fontWeight    = FontWeight.ExtraBold,
        fontFamily    = PlusJakartaSansFamily,
        color         = Color.White,
        letterSpacing = (-0.3).sp,
        modifier      = modifier
            .background(
                color = bg,
                shape = RoundedCornerShape(50),
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
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
    else       -> TealLight     to platform.uppercase()
}

@Preview
@Composable
private fun PlatformBadgePreview() {
    GrabGullyTheme {
        PlatformBadge(platform = "amazon")
    }
}
