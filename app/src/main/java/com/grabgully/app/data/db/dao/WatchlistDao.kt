package com.grabgully.app.data.db.dao

import androidx.room.*
import com.grabgully.app.data.db.entity.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM watchlist ORDER BY cachedAt DESC")
    fun observeAll(): Flow<List<WatchlistEntity>>

    @Query("SELECT * FROM watchlist WHERE id = :id")
    suspend fun getById(id: String): WatchlistEntity?

    @Query("SELECT COUNT(*) FROM watchlist")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WatchlistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WatchlistEntity)

    @Delete
    suspend fun delete(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM watchlist")
    suspend fun deleteAll()

    @Query("UPDATE watchlist SET targetPrice = :target, isNotified = 0 WHERE id = :id")
    suspend fun updateAlert(id: String, target: Double)
}
