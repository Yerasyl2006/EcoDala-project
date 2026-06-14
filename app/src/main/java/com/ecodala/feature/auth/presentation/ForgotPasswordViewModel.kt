package com.ecodala.feature.auth.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ForgotPasswordUiState(
    val email: String = "",
    val emailSent: Boolean = false,
    val errorMessage: String? = null
)

class ForgotPasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                emailSent = false,
                errorMessage = null
            )
        }
    }

    fun sendResetLink() {
        val email = _uiState.value.email.trim()

        _uiState.update {
            if (email.contains("@") && email.contains(".")) {
                it.copy(email = email, emailSent = true, errorMessage = null)
            } else {
                it.copy(emailSent = false, errorMessage = "invalid_email")
            }
        }
    }
}
