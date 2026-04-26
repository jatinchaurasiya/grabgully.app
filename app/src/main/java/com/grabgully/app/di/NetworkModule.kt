package com.grabgully.app.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.grabgully.app.BuildConfig
import com.grabgully.app.data.api.AuthInterceptor
import com.grabgully.app.data.api.GullyApi
import com.grabgully.app.data.api.TokenProvider
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true   // Safe against API additions
        isLenient         = true   // Tolerate some JSON quirks
        coerceInputValues = true   // Use defaults when null
    }

    @Provides
    @Singleton
    fun provideEncryptedPrefs(@ApplicationContext ctx: Context): EncryptedSharedPreferences {
        val masterKey = MasterKey.Builder(ctx)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        @Suppress("UNCHECKED_CAST")
        return EncryptedSharedPreferences.create(
            ctx,
            "gully_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        ) as EncryptedSharedPreferences
    }

    @Provides
    @Singleton
    fun provideTokenProvider(prefs: EncryptedSharedPreferences): TokenProvider {
        return object : TokenProvider {
            override fun getAccessToken() = prefs.getString("access_token", null)
            override fun saveAccessToken(token: String) {
                prefs.edit().putString("access_token", token).apply()
            }
            override fun clearToken() {
                prefs.edit().remove("access_token").apply()
            }
            override fun getFcmToken() = prefs.getString("fcm_token", null)
            override fun saveFcmToken(token: String) {
                prefs.edit().putString("fcm_token", token).apply()
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL.trimEnd('/') + "/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideGullyApi(retrofit: Retrofit): GullyApi =
        retrofit.create(GullyApi::class.java)
}
