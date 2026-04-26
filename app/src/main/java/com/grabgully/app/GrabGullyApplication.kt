package com.grabgully.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
// import com.cuelinks.sdk.CueLinks
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * GrabGullyApplication — Hilt entry point + SDK initialization.
 *
 * Initialises on cold start:
 * 1. Hilt DI graph (@HiltAndroidApp triggers generated component)
 * 2. CueLinks SDK — registers the affiliate channel for all non-Amazon links
 * 3. Coil 3 SingletonImageLoader — uses OkHttp from Hilt for shared connection pool
 * 4. FCM notification channels — required for Android 8.0+ (targetSdk 35)
 */
@HiltAndroidApp
class GrabGullyApplication : Application() {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()

        initCueLinks()
        initCoil()
        initNotificationChannels()
    }

    // ── CueLink SDK ───────────────────────────────────────────────────────────
    /**
     * CueLinks SDK v1.0.3 — client-side affiliate conversion.
     *
     * How it works:
     * - When the app launches an Intent.ACTION_VIEW for a product URL,
     *   CueLinks intercepts it and converts it to an affiliate link automatically.
     * - Amazon links use Amazon Creator API (backend handles those).
     * - Flipkart, Myntra, Meesho, Ajio, Snapdeal → CueLinks client-side.
     *
     * The Channel ID is set via <meta-data> in AndroidManifest.xml.
     * DO NOT hard-code it here — pull from manifest metadata.
     */
    private fun initCueLinks() {
        try {
            // CueLinks.init(this) // Temporarily commented out until dependency is fixed
        } catch (e: Exception) {
            // CueLinks init is non-fatal; log but don't crash
            android.util.Log.e("GrabGully", "CueLinks init failed: ${e.message}")
        }
    }

    // ── Coil 3 image loader ───────────────────────────────────────────────────
    private fun initCoil() {
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .components {
                    add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient }))
                }
                .crossfade(true)
                .build()
        }
    }

    // ── Notification channels (Android 8.0+) ─────────────────────────────────
    private fun initNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)

            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_PRICE_ALERTS,
                    getString(R.string.channel_price_alerts_name),
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description          = getString(R.string.channel_price_alerts_desc)
                    enableLights(true)
                    lightColor           = 0xFFC9A84C.toInt() // GoldPrimary
                    enableVibration(true)
                }
            )
        }
    }

    companion object {
        const val CHANNEL_PRICE_ALERTS = "price_alerts"
    }
}
