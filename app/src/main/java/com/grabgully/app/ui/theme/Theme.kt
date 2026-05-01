package com.grabgully.app.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════════════════════════════════
//  GRAB GULLY THEME — v4.0 (Teal & Green)
//  Light mode ONLY.
// ═══════════════════════════════════════════════════════════════════════════════

private val GrabGullyColorScheme: ColorScheme = lightColorScheme(
    // ── Backgrounds ──────────────────────────────────────────────────────────
    background          = BackgroundLight,
    surface             = SurfaceLight,
    surfaceVariant      = BackgroundLight,
    surfaceTint         = TealLight,
    inverseSurface      = FloatingNavBg,

    // ── Primary (Teal) ───────────────────────────────────────────────────────
    primary             = TealPrimary,
    onPrimary           = Color.White,
    primaryContainer    = TealLight,
    onPrimaryContainer  = TealDark,

    // ── Secondary (Mint Green) ───────────────────────────────────────────────
    secondary           = MintGreen,
    onSecondary         = TextPrimary,
    secondaryContainer  = MintLight,
    onSecondaryContainer = MintDark,

    // ── Tertiary (Savings green) ─────────────────────────────────────────────
    tertiary            = MintDark,
    onTertiary          = Color.White,
    tertiaryContainer   = MintLight,
    onTertiaryContainer = MintDark,

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
    outlineVariant      = DividerColor,

    // ── Scrim / overlay ──────────────────────────────────────────────────────
    scrim               = Color(0x33000000),
)

/**
 * Grab Gully theme composable.
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
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars     = true  // Dark icons for light theme
                isAppearanceLightNavigationBars = true  // Dark icons
            }
        }
    }

    MaterialTheme(
        colorScheme = GrabGullyColorScheme,
        typography  = GrabGullyTypography,
        content     = content,
    )
}
