package com.grabgully.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AffiliateHelper — client-side affiliate link handling.
 *
 * Affiliate strategy:
 * ┌────────────────┬──────────────────────────────────────────────────┐
 * │ Platform       │ Strategy                                         │
 * ├────────────────┼──────────────────────────────────────────────────┤
 * │ Amazon         │ Backend rewrites URL with Amazon Creator tag      │
 * │                │ → affiliateUrl already contains tag              │
 * ├────────────────┼──────────────────────────────────────────────────┤
 * │ Flipkart       │ CueLinks SDK intercepts Intent.ACTION_VIEW       │
 * │ Myntra         │ automatically converts to affiliate URL          │
 * │ Meesho         │ No extra code needed — just open the URL         │
 * │ Ajio           │                                                  │
 * │ Snapdeal       │                                                  │
 * └────────────────┴──────────────────────────────────────────────────┘
 *
 * Usage in CompareViewModel:
 * ```kotlin
 * affiliateHelper.open(listing.affiliateUrl)
 * ```
 *
 * The CueLinks SDK is initialised in GrabGullyApplication.onCreate()
 * via CueLinks.init(this). After that, every Intent.ACTION_VIEW for a
 * supported merchant URL is automatically intercepted and rewritten.
 */
@Singleton
class AffiliateHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    /**
     * Open a product affiliate URL in the browser.
     * CueLinks SDK intercepts this for non-Amazon platforms automatically.
     *
     * @param url The affiliateUrl from the API response
     */
    fun open(url: String) {
        if (url.isBlank()) return
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            android.util.Log.e("AffiliateHelper", "Could not open URL: $url", e)
        }
    }

    /**
     * Share a deal link via Android share sheet.
     * Used in deal detail page / long-press on DealCard.
     *
     * @param title      Product name
     * @param url        Affiliate URL
     * @param price      Formatted price string (e.g. "₹1,299")
     * @param platform   Platform name (e.g. "Amazon")
     */
    fun share(title: String, url: String, price: String, platform: String) {
        val shareText = buildString {
            append("🔥 *$title*\n")
            append("💸 Only $price on $platform!\n\n")
            append("Grab it now: $url\n\n")
            append("_Shared via Grab Gully — Har Deal Ka Baap!_")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type  = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(Intent.createChooser(intent, "Share deal via").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    /**
     * Detect if a URL is from a CueLinks-supported platform.
     * For informational logging only — SDK handles the interception.
     */
    fun isCueLinksPlatform(url: String): Boolean {
        val supported = listOf("flipkart.com", "myntra.com", "meesho.com", "ajio.com", "snapdeal.com")
        return supported.any { url.contains(it, ignoreCase = true) }
    }
}
