package com.grabgully.app.ui.screens.compare

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grabgully.app.data.model.CompareResult
import com.grabgully.app.data.model.PlatformPrice
import com.grabgully.app.data.model.PricePoint
import com.grabgully.app.data.repository.CompareRepository
import com.grabgully.app.data.repository.WatchlistRepository
import com.grabgully.app.util.AffiliateHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CompareUiState(
    val result:       CompareResult?   = null,
    val history:      List<PricePoint> = emptyList(),
    val isLoading:    Boolean          = true,
    val error:        String?          = null,
    val isWishlisted: Boolean          = false,
)

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val compareRepo:     CompareRepository,
    private val watchlistRepo:   WatchlistRepository,
    private val affiliateHelper: AffiliateHelper,
) : ViewModel() {

    private val _state = MutableStateFlow(CompareUiState())
    val uiState: StateFlow<CompareUiState> = _state.asStateFlow()

    fun load(listingId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Load compare + price history in parallel
            val compareDeferred = async { compareRepo.compare(listingId) }
            val historyDeferred = async { compareRepo.priceHistory(listingId) }

            compareDeferred.await()
                .onSuccess { result ->
                    _state.update { it.copy(result = result, isLoading = false) }
                }
                .onFailure { err ->
                    _state.update { it.copy(error = err.message, isLoading = false) }
                }

            historyDeferred.await()
                .onSuccess { history ->
                    _state.update { it.copy(history = history) }
                }
        }
    }

    /**
     * Open affiliate URL in browser.
     * CueLinks SDK automatically converts non-Amazon links to affiliate URLs.
     * Amazon links already contain the Creator tag from the backend.
     */
    fun openAffiliate(listing: PlatformPrice) {
        affiliateHelper.open(listing.affiliateUrl)
    }

    /**
     * Share deal via Android share sheet.
     */
    fun shareDeal(listing: PlatformPrice) {
        val title = _state.value.result?.productTitle ?: return
        affiliateHelper.share(
            title    = title,
            url      = listing.affiliateUrl,
            price    = listing.formattedPrice,
            platform = listing.platform,
        )
    }

    fun toggleWatchlist(listingId: String) {
        val isWishlisted = _state.value.isWishlisted
        viewModelScope.launch {
            if (isWishlisted) {
                _state.update { it.copy(isWishlisted = false) }
                // TODO: get actual watchlist item ID and call removeFromWatchlist
            } else {
                watchlistRepo.addToWatchlist(listingId, targetPrice = null)
                    .onSuccess { _state.update { s -> s.copy(isWishlisted = true) } }
                    .onFailure { e -> _state.update { s -> s.copy(error = "Login karke try karo: ${e.message}") } }
            }
        }
    }
}
