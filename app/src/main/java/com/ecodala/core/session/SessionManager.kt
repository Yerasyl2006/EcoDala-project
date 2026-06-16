package com.ecodala.core.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ecodala.core.domain.model.EcoUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

data class UserSession(
    val isLoggedIn: Boolean = false,
    val token: String? = null,
    val refreshToken: String? = null,
    val userId: String? = null,
    val fullName: String? = null,
    val email: String? = null
)

private val Context.sessionDataStore by preferencesDataStore(name = "auth_session")

object SessionManager {
    private val _session = MutableStateFlow(UserSession())
    val session: StateFlow<UserSession> = _session

    private var appContext: Context? = null

    private val isLoggedInKey = booleanPreferencesKey("is_logged_in")
    private val tokenKey = stringPreferencesKey("token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val userIdKey = stringPreferencesKey("user_id")
    private val fullNameKey = stringPreferencesKey("full_name")
    private val emailKey = stringPreferencesKey("email")

    fun initialize(context: Context, scope: CoroutineScope) {
        if (appContext != null) return

        appContext = context.applicationContext
        scope.launch {
            context.applicationContext.sessionDataStore.data
                .catch { exception ->
                    if (exception is IOException) {
                        emit(emptyPreferences())
                    } else {
                        throw exception
                    }
                }
                .map { preferences ->
                    UserSession(
                        isLoggedIn = preferences[isLoggedInKey] ?: false,
                        token = preferences[tokenKey],
                        refreshToken = preferences[refreshTokenKey],
                        userId = preferences[userIdKey],
                        fullName = preferences[fullNameKey],
                        email = preferences[emailKey]
                    )
                }
                .collect { _session.value = it }
        }
    }

    suspend fun saveSession(token: String, user: EcoUser) {
        saveSession(token = token, refreshToken = _session.value.refreshToken, user = user)
    }

    suspend fun saveSession(token: String, refreshToken: String?, user: EcoUser) {
        val context = appContext ?: return
        context.sessionDataStore.edit { preferences ->
            preferences[isLoggedInKey] = true
            preferences[tokenKey] = token
            refreshToken?.let { preferences[refreshTokenKey] = it }
            preferences[userIdKey] = user.id
            preferences[fullNameKey] = user.fullName
            preferences[emailKey] = user.email
        }
        _session.value = UserSession(
            isLoggedIn = true,
            token = token,
            refreshToken = refreshToken,
            userId = user.id,
            fullName = user.fullName,
            email = user.email
        )
    }

    suspend fun saveRawSession(accessToken: String, refreshToken: String?) {
        val context = appContext ?: return
        val current = _session.value
        context.sessionDataStore.edit { preferences ->
            preferences[isLoggedInKey] = true
            preferences[tokenKey] = accessToken
            refreshToken?.let { preferences[refreshTokenKey] = it }
        }
        _session.value = current.copy(
            isLoggedIn = true,
            token = accessToken,
            refreshToken = refreshToken
        )
    }

    suspend fun updateProfile(fullName: String, email: String) {
        val context = appContext ?: return
        val current = _session.value
        context.sessionDataStore.edit { preferences ->
            preferences[isLoggedInKey] = current.isLoggedIn
            current.token?.let { preferences[tokenKey] = it }
            current.refreshToken?.let { preferences[refreshTokenKey] = it }
            preferences[userIdKey] = current.userId ?: "user-1"
            preferences[fullNameKey] = fullName
            preferences[emailKey] = email
        }
        _session.value = current.copy(
            userId = current.userId ?: "user-1",
            fullName = fullName,
            email = email
        )
    }

    suspend fun clearSession() {
        val context = appContext ?: return
        context.sessionDataStore.edit { it.clear() }
        _session.value = UserSession()
    }
}
