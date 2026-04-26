package com.grabgully.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.grabgully.app.data.db.dao.CachedDealDao
import com.grabgully.app.data.db.dao.WatchlistDao
import com.grabgully.app.data.db.entity.CachedDealEntity
import com.grabgully.app.data.db.entity.WatchlistEntity

@Database(
    entities  = [WatchlistEntity::class, CachedDealEntity::class],
    version   = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
    abstract fun cachedDealDao(): CachedDealDao
}
