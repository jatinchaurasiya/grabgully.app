package com.grabgully.app.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════════
//  GRAB GULLY PREMIUM PALETTE — v2.0
//  Philosophy: Obsidian Black + Antique Gold = Trust + Aspiration
//  AMOLED-optimised: true black = ~60% battery saving on OLED screens
//  Gold reads as valuable, not cheap. Never use plain yellow or orange.
// ═══════════════════════════════════════════════════════════════════════════════

// ── BACKGROUNDS — layered depth system ───────────────────────────────────────
/** Screen background — absolute dark, AMOLED-optimised */
val ObsidianBlack    = Color(0xFF08080F)
/** Cards, bottom sheets, elevated surfaces */
val SurfaceDeep      = Color(0xFF111119)
/** Elevated modals, dialogs */
val SurfaceRaised    = Color(0xFF191925)
/** Pressed state, selected chips */
val SurfaceHighlight = Color(0xFF22223A)
/** Subtle dividers */
val DividerColor     = Color(0xFF252540)

// ── GOLD — the hero accent ────────────────────────────────────────────────────
/** Main CTAs, prices, active icons — antique gold, NOT orange */
val GoldPrimary      = Color(0xFFC9A84C)
/** Hover, focus ring, active states */
val GoldBright       = Color(0xFFE8C96A)
/** Secondary text, muted accents */
val GoldMuted        = Color(0xFF9A7A30)
/** Tinted surface behind gold elements */
val GoldSurface      = Color(0xFF1E1A0A)
/** Light tint — used only in illustrations */
val GoldPale         = Color(0xFFF5EDD3)

// ── TEXT — hierarchy on dark surfaces ────────────────────────────────────────
/** Headings, key data points */
val TextPrimary      = Color(0xFFFFFFFF)
/** Body text, labels, descriptions */
val TextSecondary    = Color(0xFFA0A0C0)
/** Hints, placeholders, timestamps */
val TextMuted        = Color(0xFF585870)
/** Gold-coloured labels (brand name, section titles) */
val TextGold         = Color(0xFFC9A84C)

// ── SEMANTIC ──────────────────────────────────────────────────────────────────
/** Savings %, in-stock indicator */
val SavingsGreen     = Color(0xFF2ECC71)
/** Dark green surface behind savings amount */
val SavingsBg        = Color(0xFF0A2016)
/** Out of stock, errors */
val AlertRed         = Color(0xFFE74C3C)
/** Dark red surface behind error messages */
val AlertBg          = Color(0xFF2A0A0A)
/** Info banners */
val InfoBlue         = Color(0xFF4A90E2)

// ── PLATFORM BADGE COLOURS ────────────────────────────────────────────────────
val AmazonColor      = Color(0xFFFF9900)
val FlipkartColor    = Color(0xFF2874F0)
val MyntraColor      = Color(0xFFFF3F6C)
val MeeshoColor      = Color(0xFF9C27B0)
val AjioColor        = Color(0xFFE8E8E8)
val SnapdealColor    = Color(0xFFE40046)

// ── XP / GAMIFICATION ────────────────────────────────────────────────────────
/** Streak bars, XP counters — bright gold */
val XpGold           = Color(0xFFFFD700)
/** XP ring around avatar */
val XpRing           = Color(0xFFC9A84C)
/** Badge tiers */
val BadgeBronze      = Color(0xFFCD7F32)
val BadgeSilver      = Color(0xFFC0C0C0)
val BadgeGold        = Color(0xFFFFD700)
val BadgePlatinum    = Color(0xFFE5E4E2)
