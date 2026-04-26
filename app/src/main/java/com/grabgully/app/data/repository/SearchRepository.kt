package com.grabgully.app.data.repository

import com.grabgully.app.data.api.GullyApi
import com.grabgully.app.data.model.SearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val api: GullyApi,
) {
    suspend fun search(
        query:    String,
        platform: String? = null,
        limit:    Int     = 20,
    ): Result<List<SearchResult>> = runCatching {
        api.search(query, platform, limit)
    }

    suspend fun searchByUrl(url: String): Result<List<SearchResult>> = runCatching {
        api.searchByUrl(url)
    }
}
