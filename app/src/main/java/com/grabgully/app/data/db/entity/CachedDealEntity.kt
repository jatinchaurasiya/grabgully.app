package com.grabgully.app.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_deals")
data class CachedDealEntity(
    @PrimaryKey val id:            String,
    val title:         String,
    val brand:         String      = "",
    val imageUrl:      String      = "",
    val platform:      String,
    val currentPrice:  Double,
    val originalPrice: Double      = 0.0,
    val discountPct:   Int         = 0,
    val affiliateUrl:  String,
    val category:      String      = "",
    val inStock:       Boolean     = true,
    val updatedAt:     String      = "",
    val cachedAt:      Long        = System.currentTimeMillis(),
)
