package com.ecodala.core.data.repository

import com.ecodala.core.data.remote.EcoDalaApi
import com.ecodala.core.data.remote.NetworkModule
import com.ecodala.core.data.remote.dto.LoginRequestDto
import com.ecodala.core.data.remote.dto.RegisterRequestDto
import com.ecodala.core.data.remote.dto.toDomain
import com.ecodala.core.domain.model.EcoUser
import com.ecodala.core.domain.model.SocialAuthProvider
import com.ecodala.core.domain.repository.AuthRepository
import com.ecodala.core.session.SessionManager

class ApiAuthRepository(
    private val api: EcoDalaApi = NetworkModule.api
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<EcoUser> {
        return runCatching {
            val token = api.login(LoginRequestDto(email = email.trim(), password = password))
            SessionManager.saveRawSession(accessToken = token.access, refreshToken = token.refresh)
            val user = token.user?.toDomain() ?: api.getMe().toDomain()
            SessionManager.saveSession(token = token.access, refreshToken = token.refresh, user = user)
            user
        }
    }

    override suspend fun register(fullName: String, email: String, password: String): Result<EcoUser> {
        return runCatching {
            api.register(
                RegisterRequestDto(
                    email = email.trim(),
                    password = password,
                    fullName = fullName.trim().ifBlank { "EcoDala User" },
                    city = "Almaty"
                )
            )
            login(email, password).getOrThrow()
        }
    }

    override suspend fun socialLogin(provider: SocialAuthProvider): Result<EcoUser> {
        return DemoAuthRepository().socialLogin(provider)
    }

    override suspend fun logout() {
        SessionManager.clearSession()
    }
}
