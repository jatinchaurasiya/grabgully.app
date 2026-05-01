package com.grabgully.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp

// ═══════════════════════════════════════════════════════════════════════════════
//  GRAB GULLY TYPOGRAPHY — v3.0
//  Font strategy (Compose Downloadable Fonts API):
//    • Plus Jakarta Sans → Display, Headline, Title, Price  (bold, geometric, premium)
//    • Inter             → Body, Caption, Labels            (humanist, highly readable)
//
//  Uses androidx.compose.ui.text.googlefonts to download at runtime.
//  No .ttf files needed in res/font/ — fonts are fetched + cached by GMS.
//
//  To enable: add to AndroidManifest.xml (already done):
//    <meta-data android:name="preloaded_fonts" android:resource="@array/preloaded_fonts"/>
// ═══════════════════════════════════════════════════════════════════════════════

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = com.grabgully.app.R.array.com_google_android_gms_fonts_certs,
)

// v3.0: Plus Jakarta Sans replaces Poppins for headlines — matches mockup
val PlusJakartaSansFamily: FontFamily = FontFamily(
    Font(
        googleFont      = GoogleFont("Plus Jakarta Sans"),
        fontProvider    = provider,
        weight          = FontWeight.Normal,
    ),
    Font(
        googleFont      = GoogleFont("Plus Jakarta Sans"),
        fontProvider    = provider,
        weight          = FontWeight.SemiBold,
    ),
    Font(
        googleFont      = GoogleFont("Plus Jakarta Sans"),
        fontProvider    = provider,
        weight          = FontWeight.Bold,
    ),
    Font(
        googleFont      = GoogleFont("Plus Jakarta Sans"),
        fontProvider    = provider,
        weight          = FontWeight.ExtraBold,
    ),
)

// Keep Poppins as an alias for backward compat (any external code referencing it)
val PoppinsFamily: FontFamily = PlusJakartaSansFamily

val InterFamily: FontFamily = FontFamily(
    Font(
        googleFont      = GoogleFont("Inter"),
        fontProvider    = provider,
        weight          = FontWeight.Normal,
    ),
    Font(
        googleFont      = GoogleFont("Inter"),
        fontProvider    = provider,
        weight          = FontWeight.Medium,
    ),
    Font(
        googleFont      = GoogleFont("Inter"),
        fontProvider    = provider,
        weight          = FontWeight.SemiBold,
    ),
)

// ── Custom price style (not part of M3 type scale) ───────────────────────────
/**
 * Used for deal prices in DealCard and CompareScreen.
 * 20sp Bold Plus Jakarta Sans in GoldPrimary — always applied directly.
 */
val PriceTextStyle = TextStyle(
    fontFamily = PlusJakartaSansFamily,
    fontWeight = FontWeight.ExtraBold,
    fontSize   = 20.sp,
    color      = TealPrimary,
)

/**
 * Strikethrough original price.
 */
val OriginalPriceStyle = TextStyle(
    fontFamily     = InterFamily,
    fontWeight     = FontWeight.Normal,
    fontSize       = 12.sp,
    color          = TextMuted,
    textDecoration = TextDecoration.LineThrough,
)

/**
 * % OFF badge text — compact Plus Jakarta Sans Bold.
 */
val BadgeTextStyle = TextStyle(
    fontFamily = PlusJakartaSansFamily,
    fontWeight = FontWeight.Bold,
    fontSize   = 10.sp,
)

/**
 * Large display price used in hero banner and profile savings.
 */
val HeroPriceStyle = TextStyle(
    fontFamily = PlusJakartaSansFamily,
    fontWeight = FontWeight.ExtraBold,
    fontSize   = 28.sp,
    color      = TealPrimary,
)

/**
 * Section subtitle — small uppercase tracking.
 */
val SectionSubtitleStyle = TextStyle(
    fontFamily    = InterFamily,
    fontWeight    = FontWeight.Bold,
    fontSize      = 10.sp,
    color         = TextSecondary,
    letterSpacing = 2.sp,
)

// ── Material 3 Typography ─────────────────────────────────────────────────────
val GrabGullyTypography = Typography(
    // ── Display ──────────────────────────────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize   = 28.sp,
        lineHeight = 36.sp,
        color      = TextPrimary,
    ),
    displayMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize   = 24.sp,
        lineHeight = 32.sp,
        color      = TextPrimary,
    ),

    // ── Headline ─────────────────────────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 22.sp,
        lineHeight = 30.sp,
        color      = TextPrimary,
    ),
    headlineMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 20.sp,
        lineHeight = 28.sp,
        color      = TextPrimary,
    ),

    // ── Title ─────────────────────────────────────────────────────────────────
    titleLarge = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 17.sp,
        lineHeight = 24.sp,
        color      = TextPrimary,
    ),
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 15.sp,
        lineHeight = 22.sp,
        color      = TextPrimary,
    ),
    titleSmall = TextStyle(
        fontFamily = PlusJakartaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color      = TextPrimary,
    ),

    // ── Body ──────────────────────────────────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        color      = TextPrimary,
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color      = TextSecondary,
    ),
    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 16.sp,
        color      = TextMuted,
    ),

    // ── Label ─────────────────────────────────────────────────────────────────
    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        color      = TextPrimary,
    ),
    labelMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 12.sp,
        lineHeight = 16.sp,
        color      = TextSecondary,
    ),
    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        lineHeight = 16.sp,
        color      = TextMuted,
    ),
)
