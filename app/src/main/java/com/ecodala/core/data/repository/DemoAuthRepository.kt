package com.ecodala.core.data.repository

import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.EcoUser
import com.ecodala.core.domain.model.SocialAuthProvider
import com.ecodala.core.domain.repository.AuthRepository
import com.ecodala.core.session.SessionManager

class DemoAuthRepository(
    private val sessionManager: SessionManager = SessionManager
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<EcoUser> {
        val user = DummyEcoData.currentUser.copy(email = email.trim())
        sessionManager.saveSession(token = "demo-token-${user.id}", user = user)
        return Result.success(user)
    }

    override suspend fun register(fullName: String, email: String, password: String): Result<EcoUser> {
        val user = DummyEcoData.currentUser.copy(
            fullName = fullName.trim(),
            email = email.trim()
        )
        sessionManager.saveSession(token = "demo-token-${user.id}", user = user)
        return Result.success(user)
    }

    override suspend fun socialLogin(provider: SocialAuthProvider): Result<EcoUser> {
        val user = DummyEcoData.currentUser.copy(
            fullName = when (provider) {
                SocialAuthProvider.Google -> "Google Eco User"
                SocialAuthProvider.Apple -> "Apple Eco User"
            },
            email = when (provider) {
                SocialAuthProvider.Google -> "google.user@ecodala.com"
                SocialAuthProvider.Apple -> "apple.user@ecodala.com"
            }
        )
        sessionManager.saveSession(token = "demo-token-${provider.name.lowercase()}", user = user)
        return Result.success(user)
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }
}
