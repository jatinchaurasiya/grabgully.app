package com.grabgully.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Single platform price — part of CompareResult.listings */
@Serializable
data class PlatformPrice(
    @SerialName("listing_id")    val listingId:     String,
    val platform:                String,
    @SerialName("current_price") val currentPrice:  Double,
    @SerialName("original_price") val originalPrice: Double = 0.0,
    @SerialName("discount_pct")  val discountPct:   Int     = 0,
    @SerialName("affiliate_url") val affiliateUrl:  String,
    @SerialName("in_stock")      val inStock:       Boolean = true,
    @SerialName("updated_at")    val updatedAt:     String  = "",
    @SerialName("is_cheapest")   val isCheapest:    Boolean = false,
) {
    val formattedPrice: String
        get() = Deal.formatRupees(currentPrice)
}

/**
 * Compare screen response — maps GET /compare/{listing_id}
 */
@Serializable
data class CompareResult(
    @SerialName("product_title") val productTitle: String,
    @SerialName("image_url")     val imageUrl:     String  = "",
    val category:                String            = "",
    val listings:                List<PlatformPrice> = emptyList(),
    val cheapest:                PlatformPrice?    = null,
    @SerialName("price_drop_24h") val priceDrop24h: Double? = null,
) {
    val hasPriceDrop: Boolean get() = priceDrop24h != null && priceDrop24h > 0.0
    val formattedPriceDrop: String get() = priceDrop24h?.let { "₹${"%,.0f".format(it)}" } ?: ""
}

/** Single point in price history — maps GET /compare/{id}/history */
@Serializable
data class PricePoint(
    val price:      Double,
    @SerialName("scraped_at") val scrapedAt: String,
)
