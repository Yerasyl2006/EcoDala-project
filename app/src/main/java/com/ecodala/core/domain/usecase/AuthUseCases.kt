package com.ecodala.core.domain.usecase

import com.ecodala.core.domain.model.EcoUser
import com.ecodala.core.domain.model.SocialAuthProvider
import com.ecodala.core.domain.repository.AuthRepository
import com.ecodala.core.validation.FormValidator
import com.ecodala.core.validation.ValidationResult
import com.ecodala.core.validation.firstErrorOrNull

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<EcoUser> {
        val error = listOf(
            FormValidator.validateEmail(email),
            FormValidator.validateLoginPassword(password)
        ).firstErrorOrNull()

        if (error != null) return Result.failure(IllegalArgumentException(error))
        return repository.login(email, password)
    }
}

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        acceptedTerms: Boolean
    ): Result<EcoUser> {
        val error = listOf(
            FormValidator.validateFullName(fullName),
            FormValidator.validateEmail(email),
            FormValidator.validateStrongPassword(password),
            FormValidator.validatePasswordsMatch(password, confirmPassword),
            FormValidator.validateTermsAccepted(acceptedTerms)
        ).firstErrorOrNull()

        if (error != null) return Result.failure(IllegalArgumentException(error))
        return repository.register(fullName, email, password)
    }
}

class SocialLoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(provider: SocialAuthProvider): Result<EcoUser> {
        return repository.socialLogin(provider)
    }
}

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.logout()
}
