package com.grabgully.app.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════════════════════════════════
//  GRAB GULLY THEME — v2.0
//  Dark mode ONLY. No light theme at MVP stage.
//  The obsidian black + antique gold palette is intentional — we never show
//  a white background. Any white you see is TextPrimary on a dark surface.
// ═══════════════════════════════════════════════════════════════════════════════

private val GrabGullyColorScheme: ColorScheme = darkColorScheme(
    // ── Backgrounds ──────────────────────────────────────────────────────────
    background          = ObsidianBlack,
    surface             = SurfaceDeep,
    surfaceVariant      = SurfaceRaised,
    surfaceTint         = GoldSurface,
    inverseSurface      = GoldPale,

    // ── Primary (Gold) ───────────────────────────────────────────────────────
    primary             = GoldPrimary,
    onPrimary           = ObsidianBlack,
    primaryContainer    = GoldSurface,
    onPrimaryContainer  = GoldBright,

    // ── Secondary (Muted gold) ───────────────────────────────────────────────
    secondary           = GoldMuted,
    onSecondary         = ObsidianBlack,
    secondaryContainer  = SurfaceHighlight,
    onSecondaryContainer = TextSecondary,

    // ── Tertiary (Savings green) ─────────────────────────────────────────────
    tertiary            = SavingsGreen,
    onTertiary          = ObsidianBlack,
    tertiaryContainer   = SavingsBg,
    onTertiaryContainer = SavingsGreen,

    // ── Error ────────────────────────────────────────────────────────────────
    error               = AlertRed,
    onError             = Color.White,
    errorContainer      = AlertBg,
    onErrorContainer    = AlertRed,

    // ── Text / On-surface ────────────────────────────────────────────────────
    onBackground        = TextPrimary,
    onSurface           = TextPrimary,
    onSurfaceVariant    = TextSecondary,
    outline             = DividerColor,
    outlineVariant      = SurfaceHighlight,

    // ── Scrim / overlay ──────────────────────────────────────────────────────
    scrim               = Color(0xCC000000),
)

/**
 * Grab Gully theme composable.
 *
 * Usage: Wrap your entire Compose tree in this:
 * ```kotlin
 * GrabGullyTheme {
 *     GullyNavGraph(...)
 * }
 * ```
 *
 * The theme:
 * 1. Applies the obsidian + gold [GrabGullyColorScheme]
 * 2. Applies Poppins + Inter [GrabGullyTypography]
 * 3. Sets status bar to dark (white icons) on ObsidianBlack
 * 4. Makes nav bar match the bottom navigation background (SurfaceDeep)
 */
@Composable
fun GrabGullyTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Transparent status + nav bars — Compose draws edge-to-edge
            WindowCompat.setDecorFitsSystemWindows(window, false)
            @Suppress("DEPRECATION")
            window.statusBarColor = ObsidianBlack.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars     = false  // White icons
                isAppearanceLightNavigationBars = false  // White icons
            }
        }
    }

    MaterialTheme(
        colorScheme = GrabGullyColorScheme,
        typography  = GrabGullyTypography,
        content     = content,
    )
}
