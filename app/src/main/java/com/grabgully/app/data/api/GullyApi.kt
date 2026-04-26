package com.grabgully.app.data.api

import com.grabgully.app.data.model.CompareResult
import com.grabgully.app.data.model.Deal
import com.grabgully.app.data.model.FcmTokenRequest
import com.grabgully.app.data.model.AddWatchlistRequest
import com.grabgully.app.data.model.PricePoint
import com.grabgully.app.data.model.SearchResult
import com.grabgully.app.data.model.SetAlertRequest
import com.grabgully.app.data.model.WatchlistItem
import retrofit2.http.*

/**
 * GullyApi — single Retrofit interface for ALL scraper backend endpoints.
 *
 * Base URL comes from BuildConfig.BASE_URL (set in local.properties).
 * Auth header (Bearer JWT) is injected by [AuthInterceptor] for watchlist routes.
 */
interface GullyApi {

    // ── Deal Feed ─────────────────────────────────────────────────────────────

    /**
     * GET /deals — paginated deal feed.
     * Called by Paging 3 in DealsRepository for the HomeScreen grid.
     */
    @GET("deals")
    suspend fun getDeals(
        @Query("category")     category:    String? = null,
        @Query("platform")     platform:    String? = null,
        @Query("min_discount") minDiscount: Int     = 0,
        @Query("page")         page:        Int     = 1,
        @Query("limit")        limit:       Int     = 20,
    ): List<Deal>

    /**
     * GET /deals/top — top deals by discount % for hero banner.
     */
    @GET("deals/top")
    suspend fun getTopDeals(
        @Query("limit") limit: Int = 5,
    ): List<Deal>

    /**
     * GET /deals/{id} — single deal detail.
     */
    @GET("deals/{id}")
    suspend fun getDeal(@Path("id") id: String): Deal

    // ── Search ────────────────────────────────────────────────────────────────

    /**
     * GET /search — universal search across all platforms + Amazon Creator API.
     */
    @GET("search")
    suspend fun search(
        @Query("q")        query:    String,
        @Query("platform") platform: String? = null,
        @Query("limit")    limit:    Int     = 20,
    ): List<SearchResult>

    /**
     * GET /search/url — URL paste search. Paste any product URL, get results.
     */
    @GET("search/url")
    suspend fun searchByUrl(
        @Query("url") url: String,
    ): List<SearchResult>

    // ── Compare ───────────────────────────────────────────────────────────────

    /**
     * GET /compare/{id} — cross-platform price comparison.
     * Returns all platforms with same/similar product + cheapest + 24h drop.
     */
    @GET("compare/{id}")
    suspend fun compare(@Path("id") listingId: String): CompareResult

    /**
     * GET /compare/{id}/history — 90-day price history for Vico chart.
     */
    @GET("compare/{id}/history")
    suspend fun priceHistory(
        @Path("id")     listingId: String,
        @Query("days")  days:      Int = 90,
    ): List<PricePoint>

    // ── Watchlist (all require Supabase JWT Bearer token) ─────────────────────

    @GET("watchlist")
    suspend fun getWatchlist(): List<WatchlistItem>

    @POST("watchlist")
    suspend fun addToWatchlist(@Body body: AddWatchlistRequest)

    @DELETE("watchlist/{id}")
    suspend fun removeFromWatchlist(@Path("id") itemId: String)

    @PATCH("watchlist/{id}/alert")
    suspend fun setAlert(
        @Path("id")  itemId: String,
        @Body        body:   SetAlertRequest,
    )

    @PATCH("watchlist/fcm-token")
    suspend fun updateFcmToken(@Body body: FcmTokenRequest)

    // ── Health ────────────────────────────────────────────────────────────────

    @GET("health")
    suspend fun health(): Map<String, String>
}
