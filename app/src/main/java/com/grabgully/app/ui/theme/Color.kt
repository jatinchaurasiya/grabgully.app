package com.grabgully.app.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════════
//  GRAB GULLY "TEAL & GREEN" PALETTE — v4.0 (Light Theme)
//  Philosophy: Clean, airy, friendly, inspired by modern SaaS dashboards.
// ═══════════════════════════════════════════════════════════════════════════════

// ── BACKGROUNDS ───────────────────────────────────────────────────────────────
/** Screen background — soft off-white/cool gray */
val BackgroundLight  = Color(0xFFF4F6F8)
/** Cards, elevated surfaces — pure white */
val SurfaceLight     = Color(0xFFFFFFFF)
/** Floating Bottom Navigation pill background */
val FloatingNavBg    = Color(0xFF141416)
/** Subtle dividers and borders */
val DividerColor     = Color(0xFFEAECEF)

// ── BRAND ACCENTS ─────────────────────────────────────────────────────────────
/** Main branding, large cards (slate blue/teal) */
val TealPrimary      = Color(0xFF3A6D8C)
/** Darker teal for contrast */
val TealDark         = Color(0xFF285068)
/** Light teal for secondary surfaces */
val TealLight        = Color(0xFFD6E6F2)

/** Active states, success indicators, vibrant accents (mint green) */
val MintGreen        = Color(0xFFA8D5BA)
/** Darker green for text on light green backgrounds */
val MintDark         = Color(0xFF2E7D32)
/** Very light green for subtle backgrounds */
val MintLight        = Color(0xFFE6F4EA)

// ── TEXT HIERARCHY ────────────────────────────────────────────────────────────
/** Headings, primary data points (dark charcoal) */
val TextPrimary      = Color(0xFF1E2124)
/** Subtitles, secondary info */
val TextSecondary    = Color(0xFF7B848B)
/** Hints, placeholders, disabled text */
val TextMuted        = Color(0xFFA3AEB7)

// ── SEMANTIC ──────────────────────────────────────────────────────────────────
/** Savings %, in-stock indicator */
val SavingsGreen     = MintDark
/** Light green surface behind savings amount */
val SavingsBg        = MintLight
/** Out of stock, errors */
val AlertRed         = Color(0xFFE57373)
/** Light red surface behind error messages */
val AlertBg          = Color(0xFFFFEBEE)
/** Info banners */
val InfoBlue         = Color(0xFF64B5F6)

// ── PLATFORM BADGE COLOURS ────────────────────────────────────────────────────
val AmazonColor      = Color(0xFFFF9900)
val FlipkartColor    = Color(0xFF2874F0)
val MyntraColor      = Color(0xFFFF3F6C)
val MeeshoColor      = Color(0xFF9C27B0)
val AjioColor        = Color(0xFF333333) // Darkened for light mode contrast
val SnapdealColor    = Color(0xFFE40046)

// ── GAMIFICATION ─────────────────────────────────────────────────────────────
val XpGold           = Color(0xFFFFD700)
val BadgeBronze      = Color(0xFFCD7F32)
val BadgeSilver      = Color(0xFFC0C0C0)
val BadgeGold        = Color(0xFFFFD700)
val BadgePlatinum    = Color(0xFF9E9E9E)

// ── PREMIUM TOKENS ───────────────────────────────────────────────────────────
/** Soft, diffuse shadow color */
val SoftShadow       = Color(0x1A000000) // black/10
/** Glass-effect header background */
val GlassBackground  = Color(0xE6FFFFFF) // white/90
/** Inactive chip background */
val InactiveChipBg   = Color(0xFFF0F2F5)
/** Inactive chip text */
val InactiveChipText = TextSecondary
