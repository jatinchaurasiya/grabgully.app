package com.grabgully.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.grabgully.app.data.api.GullyApi
import com.grabgully.app.data.model.Deal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

// ── Paging 3 Source ───────────────────────────────────────────────────────────

/**
 * PagingSource that calls GET /deals with page-based pagination.
 * The backend supports page= and limit= query params.
 */
class DealsPagingSource(
    private val api:      GullyApi,
    private val category: String?,
    private val platform: String?,
    private val minDiscount: Int,
) : PagingSource<Int, Deal>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Deal> {
        val page = params.key ?: 1
        return try {
            val deals = api.getDeals(
                category    = category,
                platform    = platform,
                minDiscount = minDiscount,
                page        = page,
                limit       = params.loadSize.coerceAtMost(20),
            )
            LoadResult.Page(
                data       = deals,
                prevKey    = if (page == 1) null else page - 1,
                nextKey    = if (deals.isEmpty()) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Deal>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
}

// ── Repository ────────────────────────────────────────────────────────────────

@Singleton
class DealsRepository @Inject constructor(
    private val api: GullyApi,
) {
    /**
     * Paged deal feed — use in HomeScreen LazyVerticalStaggeredGrid.
     */
    fun getPagedDeals(
        category:    String?  = null,
        platform:    String?  = null,
        minDiscount: Int      = 0,
    ): Flow<PagingData<Deal>> = Pager(
        config = PagingConfig(
            pageSize         = 20,
            prefetchDistance = 5,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = {
            DealsPagingSource(api, category, platform, minDiscount)
        }
    ).flow

    /**
     * Top deals for the hero banner carousel (not paged).
     */
    suspend fun getTopDeals(limit: Int = 5): Result<List<Deal>> = runCatching {
        api.getTopDeals(limit)
    }

    suspend fun getDeal(id: String): Result<Deal> = runCatching {
        api.getDeal(id)
    }
}
