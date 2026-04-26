package com.grabgully.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grabgully.app.data.api.TokenProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoggedIn:   Boolean = false,
    val userName:     String  = "",
    val userEmail:    String  = "",
    val avatarUrl:    String  = "",
    val xp:           Int     = 0,
    val level:        Int     = 1,
    val streak:       Int     = 0,
    val totalSaved:   Double  = 0.0,
    val dealsFound:   Int     = 0,
    val rank:         Int     = 0,
    val badge:        String  = "bronze",
    val isLoading:    Boolean = false,
    val showSignOut:  Boolean = false,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenProvider: TokenProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        val token = tokenProvider.getAccessToken()
        if (!token.isNullOrBlank()) {
            // Mock user data — replace with Supabase Auth user fetch in Session 5
            _state.update {
                it.copy(
                    isLoggedIn  = true,
                    userName    = "Deal Hunter",
                    userEmail   = "user@grabgully.app",
                    xp          = 4200,
                    level       = 6,
                    streak      = 7,
                    totalSaved  = 12400.0,
                    dealsFound  = 83,
                    rank        = 6,
                    badge       = "silver",
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            tokenProvider.clearToken()
            _state.update { ProfileUiState(isLoggedIn = false) }
        }
    }

    fun toggleSignOutDialog(show: Boolean) {
        _state.update { it.copy(showSignOut = show) }
    }
}
