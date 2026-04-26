package com.grabgully.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grabgully.app.data.model.SearchResult
import com.grabgully.app.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchUiState(
    val query:            String             = "",
    val results:          List<SearchResult> = emptyList(),
    val isLoading:        Boolean            = false,
    val error:            String?            = null,
    val urlMode:          Boolean            = false,
    val selectedPlatform: String?            = null,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepo: SearchRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
        // Debounce search by 400ms
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            if (query.length >= 2) search()
        }
    }

    fun search() {
        val q   = _state.value.query.trim()
        val url = _state.value.urlMode
        if (q.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = if (url && q.startsWith("http")) {
                searchRepo.searchByUrl(q)
            } else {
                searchRepo.search(q, _state.value.selectedPlatform)
            }
            result
                .onSuccess { items ->
                    _state.update { it.copy(results = items, isLoading = false) }
                }
                .onFailure { err ->
                    _state.update { it.copy(error = err.message, isLoading = false) }
                }
        }
    }

    fun setUrlMode(enabled: Boolean) {
        _state.update { it.copy(urlMode = enabled) }
    }

    fun toggleUrlMode() {
        _state.update { it.copy(urlMode = !it.urlMode, query = "") }
    }

    fun clear() {
        searchJob?.cancel()
        _state.update { it.copy(query = "", results = emptyList(), error = null) }
    }

    fun selectPlatform(platform: String?) {
        _state.update { it.copy(selectedPlatform = platform) }
        if (_state.value.query.isNotBlank()) search()
    }
}
