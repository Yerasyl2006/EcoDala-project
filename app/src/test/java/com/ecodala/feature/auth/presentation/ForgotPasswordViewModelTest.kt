package com.ecodala.feature.auth.presentation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ForgotPasswordViewModelTest {
    @Test
    fun sendResetLink_rejectsInvalidEmail() {
        val viewModel = ForgotPasswordViewModel()

        viewModel.onEmailChange("invalid")
        viewModel.sendResetLink()

        assertFalse(viewModel.uiState.value.emailSent)
        assertEquals("invalid_email", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun sendResetLink_acceptsAndTrimsValidEmail() {
        val viewModel = ForgotPasswordViewModel()

        viewModel.onEmailChange("  user@ecodala.kz ")
        viewModel.sendResetLink()

        assertTrue(viewModel.uiState.value.emailSent)
        assertEquals("user@ecodala.kz", viewModel.uiState.value.email)
    }
}
