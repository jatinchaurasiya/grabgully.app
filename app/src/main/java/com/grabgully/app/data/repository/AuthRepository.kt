package com.grabgully.app.data.repository

import com.grabgully.app.data.api.TokenProvider
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AuthRepository — manages Supabase authentication state.
 *
 * Session 5 will wire this to the Supabase Kotlin SDK (supabase-auth-kt).
 * For now it reads/writes tokens via [TokenProvider] (EncryptedSharedPreferences).
 *
 * Supabase Auth flow (Google OAuth):
 * 1. App calls signInWithGoogle()
 * 2. Supabase opens OAuth web flow (Custom Chrome Tab)
 * 3. Callback returns access_token + refresh_token
 * 4. Token is saved to EncryptedSharedPreferences via TokenProvider
 * 5. AuthInterceptor picks it up for /watchlist API calls
 *
 * TODO (Session 5):
 *   - Inject SupabaseClient via Hilt
 *   - Implement signInWithGoogle() using supabase-auth-kt
 *   - Implement token refresh on 401 from Retrofit
 *   - Implement signOut() to revoke Supabase session
 */
@Singleton
class AuthRepository @Inject constructor(
    private val tokenProvider: TokenProvider,
) {
    fun isLoggedIn(): Boolean = !tokenProvider.getAccessToken().isNullOrBlank()

    fun getAccessToken(): String? = tokenProvider.getAccessToken()

    fun saveToken(token: String) = tokenProvider.saveAccessToken(token)

    fun signOut() {
        tokenProvider.clearToken()
        // TODO: SupabaseClient.auth.signOut()
    }

    // Stub — replace with real Supabase call in Session 5
    suspend fun signInWithGoogle(): Result<Unit> = runCatching {
        // val result = supabaseClient.auth.signInWith(Google)
        // tokenProvider.saveAccessToken(result.accessToken)
        throw NotImplementedError("Wire Supabase OAuth in Session 5")
    }
}
