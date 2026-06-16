package com.ecodala.feature.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ecodala.core.data.repository.ApiAuthRepository
import com.ecodala.core.domain.model.SocialAuthProvider
import com.ecodala.core.domain.repository.AuthRepository
import com.ecodala.core.domain.usecase.LoginUseCase
import com.ecodala.core.domain.usecase.RegisterUseCase
import com.ecodala.core.domain.usecase.SocialLoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullName: String = "",
    val acceptedTerms: Boolean = false,
    val rememberMe: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    authRepository: AuthRepository = ApiAuthRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    private val loginUseCase = LoginUseCase(authRepository)
    private val registerUseCase = RegisterUseCase(authRepository)
    private val socialLoginUseCase = SocialLoginUseCase(authRepository)

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value, errorMessage = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null) }
    }

    fun onAcceptedTermsChange(value: Boolean) {
        _uiState.update { it.copy(acceptedTerms = value, errorMessage = null) }
    }

    fun onRememberMeChange(value: Boolean) {
        _uiState.update { it.copy(rememberMe = value) }
    }

    fun onLoginSubmit(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            loginUseCase(state.email, state.password)
                .onSuccess {
                    _uiState.update { current -> current.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to sign in"
                        )
                    }
                }
        }
    }

    fun onRegisterSubmit(onSuccess: () -> Unit) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            registerUseCase(
                fullName = state.fullName,
                email = state.email,
                password = state.password,
                confirmPassword = state.confirmPassword,
                acceptedTerms = state.acceptedTerms
            )
                .onSuccess {
                    _uiState.update { current -> current.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to create account"
                        )
                    }
                }
        }
    }

    fun onSocialLoginSubmit(
        provider: SocialAuthProvider,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null, isLoading = true) }
            socialLoginUseCase(provider)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            email = user.email,
                            fullName = user.fullName
                        )
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Social sign in failed"
                        )
                    }
                }
        }
    }
}
