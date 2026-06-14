package com.ecodala.core.state

sealed interface AsyncUiState<out T> {
    data object Loading : AsyncUiState<Nothing>
    data class Success<T>(val data: T) : AsyncUiState<T>
    data class Empty(val message: String = "No data found") : AsyncUiState<Nothing>
    data class Error(val message: String, val retryable: Boolean = true) : AsyncUiState<Nothing>
}
