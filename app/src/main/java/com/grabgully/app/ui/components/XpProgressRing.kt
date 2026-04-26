package com.grabgully.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grabgully.app.ui.theme.*

/**
 * Standalone XP Progress Ring component (extracted from ProfileScreen for reuse).
 *
 * Also used in:
 * - HomeScreen TopBar XpChip
 * - LeaderboardScreen avatar slots
 *
 * @param progress   0f–1f XP fill within current level
 * @param level      Current level number (shown as center badge)
 * @param initials   2-char initials shown inside ring
 * @param size       Overall ring diameter
 * @param strokeWidth Ring thickness
 */
@Composable
fun XpProgressRing(
    progress:    Float,
    level:       Int,
    initials:    String,
    size:        Dp      = 90.dp,
    strokeWidth: Dp      = 6.dp,
    modifier:    Modifier = Modifier,
) {
    val animProg by animateFloatAsState(
        targetValue   = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
        label         = "xp_progress",
    )

    Box(
        modifier         = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        // ── Animated ring ─────────────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sw     = strokeWidth.toPx()
            val inset  = sw / 2f
            val arcSize = Size(this.size.width - sw, this.size.height - sw)
            val topLeft = Offset(inset, inset)

            // Background track
            drawArc(
                color      = DividerColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                style      = Stroke(width = sw, cap = StrokeCap.Round),
                topLeft    = topLeft,
                size       = arcSize,
            )

            // Gold progress arc
            if (animProg > 0f) {
                drawArc(
                    brush      = Brush.sweepGradient(
                        colorStops = arrayOf(
                            0f    to GoldMuted,
                            0.5f  to GoldPrimary,
                            1f    to GoldBright,
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = 360f * animProg,
                    useCenter  = false,
                    style      = Stroke(width = sw, cap = StrokeCap.Round),
                    topLeft    = topLeft,
                    size       = arcSize,
                )
            }
        }

        // ── Initials + level badge ────────────────────────────────────────
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = initials,
                fontSize   = (size.value * 0.26f).sp,
                color      = GoldPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text     = "Lv $level",
                fontSize = (size.value * 0.13f).sp,
                color    = TextMuted,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF08080F)
@Composable
private fun XpProgressRingPreview() {
    GrabGullyTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier              = Modifier.padding(16.dp),
        ) {
            XpProgressRing(progress = 0.35f, level = 3, initials = "JK", size = 72.dp)
            XpProgressRing(progress = 0.75f, level = 8, initials = "GG", size = 90.dp)
            XpProgressRing(progress = 0.92f, level = 15, initials = "DK", size = 108.dp)
        }
    }
}
