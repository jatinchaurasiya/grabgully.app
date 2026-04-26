package com.grabgully.app.data.api

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that injects the Supabase JWT bearer token
 * on all /watchlist API calls.
 *
 * The token is sourced from [TokenProvider] which reads from
 * EncryptedSharedPreferences. If no token is present (user not
 * signed in), the request proceeds without an Authorization header
 * (the backend will return 401 which the ViewModel handles).
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token   = tokenProvider.getAccessToken()

        // Only inject auth on routes that need it
        val needsAuth = request.url.pathSegments.firstOrNull() == "watchlist"
        if (token.isNullOrBlank() || !needsAuth) {
            return chain.proceed(request)
        }

        val authed = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authed)
    }
}

/**
 * Abstraction over token storage.
 * Implemented by EncryptedSharedPreferences in the DI module.
 */
interface TokenProvider {
    fun getAccessToken(): String?
    fun saveAccessToken(token: String)
    fun clearToken()
    // FCM device token
    fun getFcmToken(): String?
    fun saveFcmToken(token: String)
}
