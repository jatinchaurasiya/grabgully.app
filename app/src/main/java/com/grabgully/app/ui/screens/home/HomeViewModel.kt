package com.grabgully.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.grabgully.app.data.model.Deal
import com.grabgully.app.data.repository.DealsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val topDeals:         List<Deal> = emptyList(),
    val isLoadingBanner:  Boolean    = true,
    val bannerError:      String?    = null,
    val selectedCategory: String     = "all",
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dealsRepo: DealsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Selected category drives the deal feed (flatMapLatest re-creates pager)
    private val selectedCategory = MutableStateFlow<String?>(null)

    val dealsFeed: Flow<PagingData<Deal>> = selectedCategory
        .flatMapLatest { category ->
            dealsRepo.getPagedDeals(category = category)
        }
        .cachedIn(viewModelScope)

    init {
        loadTopDeals()
    }

    fun selectCategory(categoryId: String) {
        val cat = if (categoryId == "all") null else categoryId
        selectedCategory.value = cat
        _uiState.update { it.copy(selectedCategory = categoryId) }
    }

    fun loadTopDeals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingBanner = true, bannerError = null) }
            dealsRepo.getTopDeals(limit = 5)
                .onSuccess { deals ->
                    _uiState.update { it.copy(topDeals = deals, isLoadingBanner = false) }
                }
                .onFailure { err ->
                    _uiState.update {
                        it.copy(isLoadingBanner = false, bannerError = err.message)
                    }
                }
        }
    }
}
