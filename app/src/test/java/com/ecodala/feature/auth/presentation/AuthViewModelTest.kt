package com.ecodala.feature.auth.presentation

import com.ecodala.core.domain.model.EcoUser
import com.ecodala.core.domain.model.SocialAuthProvider
import com.ecodala.core.domain.repository.AuthRepository
import com.ecodala.core.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loginSubmit_setsErrorWhenValidationFails() = runTest {
        val viewModel = AuthViewModel(FakeAuthRepository())

        viewModel.onEmailChange("bad-email")
        viewModel.onPasswordChange("123")
        viewModel.onLoginSubmit(onSuccess = {})
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.errorMessage?.isNotBlank() == true)
    }

    @Test
    fun loginSubmit_updatesLoadingAndCallsSuccess() = runTest {
        var successCalled = false
        val viewModel = AuthViewModel(FakeAuthRepository())

        viewModel.onEmailChange("user@ecodala.kz")
        viewModel.onPasswordChange("Password1")
        viewModel.onLoginSubmit(onSuccess = { successCalled = true })
        advanceUntilIdle()

        assertTrue(successCalled)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.errorMessage)
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String): Result<EcoUser> {
            return Result.success(fakeUser.copy(email = email))
        }

        override suspend fun register(fullName: String, email: String, password: String): Result<EcoUser> {
            return Result.success(fakeUser.copy(fullName = fullName, email = email))
        }

        override suspend fun socialLogin(provider: SocialAuthProvider): Result<EcoUser> {
            return Result.success(fakeUser)
        }

        override suspend fun logout() = Unit
    }
}

private val fakeUser = EcoUser(
    id = "user-test",
    fullName = "Eco Tester",
    email = "user@ecodala.kz",
    ecoPoints = 120,
    globalRank = 1,
    level = 2,
    joinedAt = "2026"
)
