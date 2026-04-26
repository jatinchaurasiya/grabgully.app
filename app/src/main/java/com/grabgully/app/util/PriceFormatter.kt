package com.grabgully.app.util

import java.text.NumberFormat
import java.util.Locale

/**
 * PriceFormatter — centralized price display utilities.
 *
 * All price formatting for the app passes through here to ensure:
 * - Consistent ₹ symbol placement
 * - Indian numbering system (1,23,456 not 123,456)
 * - Compact format for badges (₹1.2k instead of ₹1,200)
 * - Percentage formatting
 */
object PriceFormatter {

    private val indiaLocale   = Locale("en", "IN")
    private val indiaFormat   = NumberFormat.getCurrencyInstance(indiaLocale)
    private val integerFormat = NumberFormat.getIntegerInstance(indiaLocale)

    init {
        // Remove ISO currency code, keep ₹ symbol only
        indiaFormat.currency = java.util.Currency.getInstance("INR")
    }

    /**
     * Full rupee format: ₹2,49,999
     * Uses Indian numbering (lakhs/crores convention).
     */
    fun format(amount: Double): String {
        if (amount <= 0) return "N/A"
        return "₹${integerFormat.format(amount.toLong())}"
    }

    /**
     * Compact format for tight spaces:
     *   <1000  → ₹999
     *   <100k  → ₹12.5k
     *   ≥100k  → ₹1.2L
     */
    fun formatCompact(amount: Double): String = when {
        amount <= 0      -> "N/A"
        amount < 1_000   -> "₹${amount.toInt()}"
        amount < 1_00_000 -> "₹${"%.1f".format(amount / 1_000)}k"
        else             -> "₹${"%.1f".format(amount / 1_00_000)}L"
    }

    /**
     * Discount percentage: "57% OFF"
     */
    fun formatDiscount(pct: Int): String = if (pct > 0) "$pct% OFF" else ""

    /**
     * Savings amount: "Save ₹1,700"
     */
    fun formatSavings(originalPrice: Double, dealPrice: Double): String {
        val savings = (originalPrice - dealPrice).coerceAtLeast(0.0)
        return if (savings > 0) "Save ${format(savings)}" else ""
    }

    /**
     * Price range label for CompareScreen: "₹999 – ₹2,499"
     */
    fun formatRange(low: Double, high: Double): String =
        "${format(low)} – ${format(high)}"

    /**
     * Target price label for Track alerts: "Alert < ₹999"
     */
    fun formatTarget(target: Double): String = "Alert < ${format(target)}"
}
