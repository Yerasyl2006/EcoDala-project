package com.ecodala.core.domain.repository

import com.ecodala.core.domain.model.EcoUser
import com.ecodala.core.domain.model.SocialAuthProvider

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<EcoUser>
    suspend fun register(fullName: String, email: String, password: String): Result<EcoUser>
    suspend fun socialLogin(provider: SocialAuthProvider): Result<EcoUser>
    suspend fun logout()
}
