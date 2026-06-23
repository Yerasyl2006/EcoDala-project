package com.ecodala.core.data.remote

import com.ecodala.BuildConfig
import com.ecodala.core.data.remote.dto.RefreshTokenRequestDto
import com.ecodala.core.logging.AppLogger
import com.ecodala.core.session.SessionManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModule {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val refreshApi: EcoDalaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.ECODALA_API_BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor())
                    .build()
            )
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(EcoDalaApi::class.java)
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val token = SessionManager.session.value.token
            val request = if (token.isNullOrBlank()) {
                chain.request()
            } else {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            }
            chain.proceed(request)
        }
        .authenticator(TokenRefreshAuthenticator())
        .addInterceptor(loggingInterceptor())
        .build()

    val api: EcoDalaApi = Retrofit.Builder()
        .baseUrl(BuildConfig.ECODALA_API_BASE_URL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(EcoDalaApi::class.java)

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    private class TokenRefreshAuthenticator : Authenticator {
        override fun authenticate(route: Route?, response: Response): okhttp3.Request? {
            if (responseCount(response) >= 2) return null
            val refresh = SessionManager.session.value.refreshToken?.takeIf { it.isNotBlank() } ?: return null

            val newToken = runBlocking {
                runCatching {
                    refreshApi.refreshToken(RefreshTokenRequestDto(refresh = refresh))
                }.onFailure { AppLogger.w("Token refresh failed", it) }.getOrNull()
            } ?: return null

            runBlocking {
                SessionManager.saveRawSession(
                    accessToken = newToken.access,
                    refreshToken = newToken.refresh.takeIf { it.isNotBlank() } ?: refresh
                )
            }
            AppLogger.d("Access token refreshed")

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${newToken.access}")
                .build()
        }

        private fun responseCount(response: Response): Int {
            var count = 1
            var prior = response.priorResponse
            while (prior != null) {
                count++
                prior = prior.priorResponse
            }
            return count
        }
    }
}
