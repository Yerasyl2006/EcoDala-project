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
                        userId = preferences[userIdKey],
                        fullName = preferences[fullNameKey],
                        email = preferences[emailKey]
                    )
                }
                .collect { _session.value = it }
        }
    }

    suspend fun saveSession(token: String, user: EcoUser) {
        val context = appContext ?: return
        context.sessionDataStore.edit { preferences ->
            preferences[isLoggedInKey] = true
            preferences[tokenKey] = token
            preferences[userIdKey] = user.id
            preferences[fullNameKey] = user.fullName
            preferences[emailKey] = user.email
        }
        _session.value = UserSession(
            isLoggedIn = true,
            token = token,
            userId = user.id,
            fullName = user.fullName,
            email = user.email
        )
    }

    suspend fun clearSession() {
        val context = appContext ?: return
        context.sessionDataStore.edit { it.clear() }
        _session.value = UserSession()
    }
}
