package com.grabgully.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val id:           String,
    val listingId:    String,
    val targetPrice:  Double?,
    val isNotified:   Boolean  = false,
    val createdAt:    String   = "",
    val title:        String   = "",
    val platform:     String   = "",
    val currentPrice: Double   = 0.0,
    val imageUrl:     String   = "",
    val affiliateUrl: String   = "",
    val cachedAt:     Long     = System.currentTimeMillis(),
)
