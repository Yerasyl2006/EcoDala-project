package com.ecodala.core.validation

object FormValidator {
    fun validateEmail(value: String): ValidationResult {
        val email = value.trim()
        return when {
            email.isBlank() -> ValidationResult.Invalid("Enter your email address")
            !email.contains("@") || !email.contains(".") -> ValidationResult.Invalid("Enter a valid email address")
            else -> ValidationResult.Valid
        }
    }

    fun validateLoginPassword(value: String): ValidationResult {
        return when {
            value.isBlank() -> ValidationResult.Invalid("Enter your password")
            value.length < 6 -> ValidationResult.Invalid("Password must be at least 6 characters")
            else -> ValidationResult.Valid
        }
    }

    fun validateFullName(value: String): ValidationResult {
        return if (value.trim().length < 2) {
            ValidationResult.Invalid("Enter your full name")
        } else {
            ValidationResult.Valid
        }
    }

    fun validateStrongPassword(value: String): ValidationResult {
        return if (value.length >= 8 && value.any { it.isDigit() } && value.any { it.isUpperCase() }) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("Use 8+ characters, a number and an uppercase letter")
        }
    }

    fun validatePasswordsMatch(password: String, confirmPassword: String): ValidationResult {
        return if (password == confirmPassword) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("Passwords do not match")
        }
    }

    fun validateTermsAccepted(value: Boolean): ValidationResult {
        return if (value) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("Accept the Terms and Privacy Policy")
        }
    }
}

sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(val message: String) : ValidationResult
}

fun List<ValidationResult>.firstErrorOrNull(): String? {
    return firstOrNull { it is ValidationResult.Invalid }
        ?.let { it as ValidationResult.Invalid }
        ?.message
}
