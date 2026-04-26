package com.grabgully.app.data.repository

import com.grabgully.app.data.api.GullyApi
import com.grabgully.app.data.model.CompareResult
import com.grabgully.app.data.model.PricePoint
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompareRepository @Inject constructor(
    private val api: GullyApi,
) {
    suspend fun compare(listingId: String): Result<CompareResult> = runCatching {
        api.compare(listingId)
    }

    suspend fun priceHistory(listingId: String, days: Int = 90): Result<List<PricePoint>> =
        runCatching {
            api.priceHistory(listingId, days)
        }
}
