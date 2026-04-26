package com.grabgully.app.di

import android.content.Context
import androidx.room.Room
import com.grabgully.app.data.db.AppDatabase
import com.grabgully.app.data.db.dao.CachedDealDao
import com.grabgully.app.data.db.dao.WatchlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "grab_gully_db",
        )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideWatchlistDao(db: AppDatabase): WatchlistDao = db.watchlistDao()

    @Provides
    fun provideCachedDealDao(db: AppDatabase): CachedDealDao = db.cachedDealDao()
}
