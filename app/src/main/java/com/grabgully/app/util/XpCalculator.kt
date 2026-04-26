package com.grabgully.app.util

/**
 * XpCalculator — centralized XP award logic.
 *
 * XP Events (from PRD Section 4):
 *  • Daily login:           50 XP
 *  • Deal viewed:           5 XP
 *  • Deal clicked/bought:   25 XP
 *  • Price alert set:       30 XP
 *  • Search performed:      10 XP
 *  • Referral sent:         200 XP
 *  • 7-day streak bonus:    500 XP
 *  • 30-day streak bonus:   2000 XP
 *
 * Levels (cumulative XP thresholds):
 *  Level 1:   0 XP     → Noob Bargainer
 *  Level 2:   500 XP   → Discount Dost
 *  Level 3:   1500 XP  → Deal Dhundhak
 *  Level 4:   3000 XP  → Savings Sipahi
 *  Level 5:   5000 XP  → Bazaar Bhai
 *  Level 6:   8000 XP  → Price Predator
 *  Level 7:   12000 XP → Deal Ninja
 *  Level 8:   18000 XP → Market Maharaj
 *  Level 9:   25000 XP → Savings Sultan
 *  Level 10:  35000 XP → Gully King 👑
 */
object XpCalculator {

    // ── XP award amounts ──────────────────────────────────────────────────────
    const val XP_DAILY_LOGIN   = 50
    const val XP_DEAL_VIEWED   = 5
    const val XP_DEAL_CLICKED  = 25
    const val XP_ALERT_SET     = 30
    const val XP_SEARCH        = 10
    const val XP_REFERRAL      = 200
    const val XP_STREAK_7D     = 500
    const val XP_STREAK_30D    = 2000

    // ── Level thresholds ──────────────────────────────────────────────────────
    private val levelThresholds = listOf(
        0,      // Level 1
        500,    // Level 2
        1500,   // Level 3
        3000,   // Level 4
        5000,   // Level 5
        8000,   // Level 6
        12000,  // Level 7
        18000,  // Level 8
        25000,  // Level 9
        35000,  // Level 10
    )

    private val levelTitles = listOf(
        "Noob Bargainer",
        "Discount Dost",
        "Deal Dhundhak",
        "Savings Sipahi",
        "Bazaar Bhai",
        "Price Predator",
        "Deal Ninja",
        "Market Maharaj",
        "Savings Sultan",
        "Gully King 👑",
    )

    /**
     * Calculate level (1-based) from total XP.
     * Level 10 is the max.
     */
    fun levelFromXp(totalXp: Int): Int {
        var level = 1
        for (i in levelThresholds.indices) {
            if (totalXp >= levelThresholds[i]) level = i + 1
            else break
        }
        return level.coerceAtMost(10)
    }

    /**
     * Human-readable level title.
     */
    fun levelTitle(level: Int): String =
        levelTitles.getOrElse(level - 1) { levelTitles.last() }

    /**
     * Progress within the current level as a 0f–1f fraction.
     * Used to draw the XP ring arc.
     */
    fun levelProgress(totalXp: Int): Float {
        val level     = levelFromXp(totalXp)
        if (level >= 10) return 1f
        val threshold = levelThresholds[level - 1]
        val next      = levelThresholds[level]
        val progress  = (totalXp - threshold).toFloat() / (next - threshold).toFloat()
        return progress.coerceIn(0f, 1f)
    }

    /**
     * XP needed until the next level-up.
     */
    fun xpToNextLevel(totalXp: Int): Int {
        val level = levelFromXp(totalXp)
        if (level >= 10) return 0
        return levelThresholds[level] - totalXp
    }

    /**
     * Badge tier from level.
     *  1-3:  bronze
     *  4-5:  silver
     *  6-8:  gold
     *  9-10: platinum
     */
    fun badgeFromLevel(level: Int): String = when (level) {
        in 1..3   -> "bronze"
        in 4..5   -> "silver"
        in 6..8   -> "gold"
        else       -> "platinum"
    }

    /**
     * Streak XP bonus — called at the end of a streak day.
     */
    fun streakBonus(streakDays: Int): Int = when {
        streakDays % 30 == 0 -> XP_STREAK_30D
        streakDays % 7  == 0 -> XP_STREAK_7D
        else                  -> 0
    }
}
