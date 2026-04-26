package com.grabgully.app.data.db.dao

import androidx.room.*
import com.grabgully.app.data.db.entity.CachedDealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedDealDao {

    @Query("SELECT * FROM cached_deals WHERE (:category IS NULL OR category = :category) ORDER BY discountPct DESC LIMIT :limit")
    fun observeByCategory(category: String?, limit: Int = 40): Flow<List<CachedDealEntity>>

    @Query("SELECT * FROM cached_deals ORDER BY discountPct DESC LIMIT :limit")
    suspend fun getTopDeals(limit: Int = 5): List<CachedDealEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(deals: List<CachedDealEntity>)

    @Query("DELETE FROM cached_deals WHERE cachedAt < :olderThan")
    suspend fun evictOldEntries(olderThan: Long)

    @Query("DELETE FROM cached_deals")
    suspend fun deleteAll()
}
