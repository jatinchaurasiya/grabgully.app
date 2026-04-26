package com.grabgully.app.data.repository

import com.grabgully.app.data.api.GullyApi
import com.grabgully.app.data.db.dao.WatchlistDao
import com.grabgully.app.data.db.entity.WatchlistEntity
import com.grabgully.app.data.model.AddWatchlistRequest
import com.grabgully.app.data.model.FcmTokenRequest
import com.grabgully.app.data.model.SetAlertRequest
import com.grabgully.app.data.model.WatchlistItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchlistRepository @Inject constructor(
    private val api: GullyApi,
    private val dao: WatchlistDao,
) {
    /**
     * Offline-first watchlist stream.
     * The UI observes Room; sync happens on pull-to-refresh or app open.
     */
    fun observeWatchlist(): Flow<List<WatchlistItem>> =
        dao.observeAll().map { entities ->
            entities.map { it.toWatchlistItem() }
        }

    /**
     * Sync remote watchlist into Room.
     * Call this on app foreground or manual pull-to-refresh.
     */
    suspend fun syncFromRemote(): Result<Unit> = runCatching {
        val items = api.getWatchlist()
        val entities = items.map { it.toEntity() }
        dao.deleteAll()
        dao.insertAll(entities)
    }

    suspend fun addToWatchlist(listingId: String, targetPrice: Double?): Result<Unit> =
        runCatching {
            api.addToWatchlist(AddWatchlistRequest(listingId, targetPrice))
            syncFromRemote().getOrThrow()
        }

    suspend fun removeFromWatchlist(itemId: String): Result<Unit> = runCatching {
        api.removeFromWatchlist(itemId)
        dao.deleteById(itemId)
    }

    suspend fun setAlert(itemId: String, targetPrice: Double): Result<Unit> = runCatching {
        api.setAlert(itemId, SetAlertRequest(targetPrice))
        dao.updateAlert(itemId, targetPrice)
    }

    suspend fun updateFcmToken(token: String): Result<Unit> = runCatching {
        api.updateFcmToken(FcmTokenRequest(token))
    }

    // ── Entity / Model converters ─────────────────────────────────────────────

    private fun WatchlistItem.toEntity() = WatchlistEntity(
        id            = id,
        listingId     = listingId,
        targetPrice   = targetPrice,
        isNotified    = isNotified,
        createdAt     = createdAt,
        title         = title,
        platform      = platform,
        currentPrice  = currentPrice,
        imageUrl      = imageUrl,
        affiliateUrl  = affiliateUrl,
    )

    private fun WatchlistEntity.toWatchlistItem() = WatchlistItem(
        id            = id,
        listingId     = listingId,
        targetPrice   = targetPrice,
        isNotified    = isNotified,
        createdAt     = createdAt,
        title         = title,
        platform      = platform,
        currentPrice  = currentPrice,
        imageUrl      = imageUrl,
        affiliateUrl  = affiliateUrl,
    )
}
