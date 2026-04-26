package com.grabgully.app.ui.screens.track

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grabgully.app.data.model.WatchlistItem
import com.grabgully.app.data.repository.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrackUiState(
    val items:     List<WatchlistItem> = emptyList(),
    val isLoading: Boolean             = true,
    val error:     String?             = null,
    val isSyncing: Boolean             = false,
)

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val watchlistRepo: WatchlistRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TrackUiState())
    val uiState: StateFlow<TrackUiState> = _state.asStateFlow()

    init {
        // Observe Room (offline-first) — updates live as DB changes
        viewModelScope.launch {
            watchlistRepo.observeWatchlist()
                .catch { e -> _state.update { it.copy(error = e.message, isLoading = false) } }
                .collect { items ->
                    _state.update { it.copy(items = items, isLoading = false) }
                }
        }
        // Initial remote sync
        syncFromRemote()
    }

    fun syncFromRemote() {
        viewModelScope.launch {
            _state.update { it.copy(isSyncing = true) }
            watchlistRepo.syncFromRemote()
                .onFailure { e ->
                    _state.update { it.copy(error = "Sync failed: ${e.message}") }
                }
            _state.update { it.copy(isSyncing = false) }
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            watchlistRepo.removeFromWatchlist(itemId)
        }
    }

    fun setAlert(itemId: String, targetPrice: Double) {
        viewModelScope.launch {
            watchlistRepo.setAlert(itemId, targetPrice)
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }
}
