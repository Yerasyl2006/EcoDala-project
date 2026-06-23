package com.ecodala.core.validation

import org.junit.Assert.assertTrue
import org.junit.Test

class FormValidatorTest {
    @Test
    fun validateEmail_rejectsInvalidEmail() {
        assertTrue(FormValidator.validateEmail("wrong-email") is ValidationResult.Invalid)
    }

    @Test
    fun validateEmail_acceptsTrimmedEmail() {
        assertTrue(FormValidator.validateEmail("  user@ecodala.kz ") is ValidationResult.Valid)
    }

    @Test
    fun validateStrongPassword_requiresLengthNumberAndUppercase() {
        assertTrue(FormValidator.validateStrongPassword("password") is ValidationResult.Invalid)
        assertTrue(FormValidator.validateStrongPassword("Password1") is ValidationResult.Valid)
    }

    @Test
    fun firstErrorOrNull_returnsFirstValidationError() {
        val error = listOf(
            ValidationResult.Valid,
            ValidationResult.Invalid("first"),
            ValidationResult.Invalid("second")
        ).firstErrorOrNull()

        assertTrue(error == "first")
    }
}
