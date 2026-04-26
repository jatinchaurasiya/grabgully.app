package com.grabgully.app.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.grabgully.app.GrabGullyApplication.Companion.CHANNEL_PRICE_ALERTS
import com.grabgully.app.MainActivity
import com.grabgully.app.R
import com.grabgully.app.data.api.TokenProvider
import com.grabgully.app.data.repository.WatchlistRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * GullyFcmService — handles Firebase Cloud Messaging events.
 *
 * Message types the backend sends:
 *
 *  price_drop:
 *    data: { type: "price_drop", listing_id: "xxx", title: "...",
 *            old_price: "1999", new_price: "1299", platform: "amazon" }
 *    → Shows a rich price drop notification
 *    → Deep link taps open CompareScreen via grabgully://compare/{listing_id}
 *
 *  deal_flash:
 *    data: { type: "deal_flash", listing_id: "xxx", title: "...", discount_pct: "60" }
 *    → Flash deal notification (time-limited)
 *
 * Token lifecycle:
 *  onNewToken() → saved to EncryptedSharedPreferences via TokenProvider
 *              → PATCH /watchlist/fcm-token if user is logged in
 */
@AndroidEntryPoint
class GullyFcmService : FirebaseMessagingService() {

    @Inject lateinit var tokenProvider:    TokenProvider
    @Inject lateinit var watchlistRepo:    WatchlistRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // ── New FCM token ─────────────────────────────────────────────────────────
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        tokenProvider.saveFcmToken(token)

        // If user is signed in, push updated token to backend
        if (!tokenProvider.getAccessToken().isNullOrBlank()) {
            serviceScope.launch {
                watchlistRepo.updateFcmToken(token)
            }
        }
    }

    // ── Incoming message ──────────────────────────────────────────────────────
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        when (data["type"]) {
            "price_drop" -> showPriceDropNotification(data)
            "deal_flash" -> showDealFlashNotification(data)
            else         -> showGenericNotification(message)
        }
    }

    // ── Price drop notification ───────────────────────────────────────────────
    private fun showPriceDropNotification(data: Map<String, String>) {
        val listingId = data["listing_id"] ?: return
        val title     = data["title"] ?: "Price Drop Alert!"
        val oldPrice  = data["old_price"]?.toDoubleOrNull()
        val newPrice  = data["new_price"]?.toDoubleOrNull()
        val platform  = data["platform"] ?: ""

        val deepLink = "grabgully://compare/$listingId"
        val tapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink), this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, listingId.hashCode(), tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val contentText = buildString {
            if (oldPrice != null && newPrice != null) {
                append("₹${"%.0f".format(oldPrice)} → ₹${"%.0f".format(newPrice)}")
                val drop = ((oldPrice - newPrice) / oldPrice * 100).toInt()
                append(" ($drop% gira!)")
            } else {
                append("Price gira! Jaldi dekho.")
            }
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_PRICE_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("💸 $title")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(0xFFC9A84C.toInt()) // GoldPrimary
            .build()

        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(listingId.hashCode(), notification)
    }

    // ── Flash deal notification ───────────────────────────────────────────────
    private fun showDealFlashNotification(data: Map<String, String>) {
        val listingId   = data["listing_id"] ?: return
        val title       = data["title"] ?: "Flash Deal!"
        val discountPct = data["discount_pct"] ?: "?"

        val deepLink = "grabgully://compare/$listingId"
        val tapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(deepLink), this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, listingId.hashCode() + 1, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_PRICE_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⚡ Flash Deal: $discountPct% OFF!")
            .setContentText(title)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(0xFFC9A84C.toInt())
            .build()

        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(listingId.hashCode() + 1, notification)
    }

    // ── Generic fallback ──────────────────────────────────────────────────────
    private fun showGenericNotification(message: RemoteMessage) {
        val notification = message.notification ?: return

        val notif = NotificationCompat.Builder(this, CHANNEL_PRICE_ALERTS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notification.title ?: "Grab Gully")
            .setContentText(notification.body ?: "")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(System.currentTimeMillis().toInt(), notif)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
