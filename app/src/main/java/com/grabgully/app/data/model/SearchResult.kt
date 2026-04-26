package com.grabgully.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Maps GET /search and GET /search/url responses */
@Serializable
data class SearchResult(
    val id:                      String,
    val title:                   String,
    val brand:                   String            = "",
    @SerialName("image_url")     val imageUrl:     String  = "",
    val platform:                String,
    @SerialName("current_price") val currentPrice: Double,
    @SerialName("original_price") val originalPrice: Double = 0.0,
    @SerialName("discount_pct")  val discountPct:  Int     = 0,
    @SerialName("affiliate_url") val affiliateUrl: String,
    val category:                String            = "",
    val source:                  String            = "db", // "db" | "amazon_creator"
) {
    val formattedPrice: String get() = Deal.formatRupees(currentPrice)

    /** Convert to Deal for rendering with DealCard */
    fun toDeal(): Deal = Deal(
        id            = id,
        title         = title,
        brand         = brand,
        imageUrl      = imageUrl,
        platform      = platform,
        currentPrice  = currentPrice,
        originalPrice = originalPrice,
        discountPct   = discountPct,
        affiliateUrl  = affiliateUrl,
        category      = category,
    )
}
