package com.grabgully.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Maps GET /watchlist response item */
@Serializable
data class WatchlistItem(
    val id:                      String,
    @SerialName("listing_id")    val listingId:    String,
    @SerialName("target_price")  val targetPrice:  Double? = null,
    @SerialName("is_notified")   val isNotified:   Boolean = false,
    @SerialName("created_at")    val createdAt:    String  = "",
    val title:                   String            = "",
    val platform:                String            = "",
    @SerialName("current_price") val currentPrice: Double  = 0.0,
    @SerialName("image_url")     val imageUrl:     String  = "",
    @SerialName("affiliate_url") val affiliateUrl: String  = "",
) {
    /** Price has dropped to or below target */
    val isPriceDropped: Boolean
        get() = targetPrice != null && currentPrice <= targetPrice

    val formattedCurrentPrice: String
        get() = Deal.formatRupees(currentPrice)

    val formattedTargetPrice: String
        get() = targetPrice?.let { Deal.formatRupees(it) } ?: "Alert nahi hai"

    val statusLabel: String
        get() = when {
            isPriceDropped -> "Price Gira!"
            targetPrice != null -> "Alert Set"
            else -> "Nazar Mein"
        }
}

/** Request body for POST /watchlist */
@Serializable
data class AddWatchlistRequest(
    @SerialName("listing_id")   val listingId:   String,
    @SerialName("target_price") val targetPrice: Double? = null,
)

/** Request body for PATCH /watchlist/{id}/alert */
@Serializable
data class SetAlertRequest(
    @SerialName("target_price") val targetPrice: Double,
)

/** Request body for PATCH /watchlist/fcm-token */
@Serializable
data class FcmTokenRequest(
    @SerialName("fcm_token") val fcmToken: String,
)
