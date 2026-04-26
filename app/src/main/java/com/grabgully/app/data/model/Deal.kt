package com.grabgully.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Core deal data class — maps the /deals and /deals/top API response.
 *
 * JSON fields use snake_case (API convention) mapped to camelCase Kotlin
 * via @SerialName annotations.
 */
@Serializable
data class Deal(
    val id:             String,
    val title:          String,
    val brand:          String        = "",
    @SerialName("image_url")      val imageUrl:      String  = "",
    val platform:       String,
    @SerialName("current_price")  val currentPrice:  Double,
    @SerialName("original_price") val originalPrice: Double  = 0.0,
    @SerialName("discount_pct")   val discountPct:   Int     = 0,
    @SerialName("affiliate_url")  val affiliateUrl:  String,
    val category:       String        = "",
    @SerialName("in_stock")       val inStock:       Boolean = true,
    @SerialName("updated_at")     val updatedAt:     String  = "",
) {
    /** Formatted deal price: ₹2,499 */
    val formattedDealPrice: String
        get() = formatRupees(currentPrice)

    /** Formatted original price: ₹4,999 */
    val formattedOriginalPrice: String
        get() = formatRupees(originalPrice)

    /** True if this deal has a meaningful discount shown */
    val hasDiscount: Boolean
        get() = discountPct > 0 && originalPrice > currentPrice

    /** Savings amount in rupees */
    val savingsAmount: Double
        get() = (originalPrice - currentPrice).coerceAtLeast(0.0)

    companion object {
        fun formatRupees(amount: Double): String {
            if (amount <= 0) return "N/A"
            val intPart = amount.toLong()
            return "₹${"%,d".format(intPart)}"
        }
    }
}
