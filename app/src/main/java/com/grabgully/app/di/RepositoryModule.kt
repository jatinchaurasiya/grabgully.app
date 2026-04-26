package com.grabgully.app.di

import com.grabgully.app.data.repository.AuthRepository
import com.grabgully.app.data.repository.CompareRepository
import com.grabgully.app.data.repository.DealsRepository
import com.grabgully.app.data.repository.SearchRepository
import com.grabgully.app.data.repository.WatchlistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * RepositoryModule — provides all repositories as singletons.
 *
 * All repositories are @Inject constructor — Hilt can construct them directly.
 * This module exists for completeness and to make the dependency graph explicit.
 * No @Binds or @Provides are strictly required here since all repositories
 * use @Inject, but the module is a good place for future interface bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    // Hilt auto-provides @Inject-annotated repositories via component binding.
    // Add @Binds here if you later extract Repository interfaces:
    //
    // @Binds
    // abstract fun bindDealsRepo(impl: DealsRepository): IDealsRepository
}
