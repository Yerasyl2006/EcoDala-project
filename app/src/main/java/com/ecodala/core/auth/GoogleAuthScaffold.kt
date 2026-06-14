package com.ecodala.core.auth

data class SocialUser(
    val id: String,
    val email: String,
    val displayName: String,
    val provider: AuthProvider
)

enum class AuthProvider {
    Google,
    Apple
}

interface GoogleAuthClient {
    suspend fun signIn(): Result<SocialUser>
    suspend fun signOut()
}

class DemoGoogleAuthClient : GoogleAuthClient {
    override suspend fun signIn(): Result<SocialUser> {
        return Result.success(
            SocialUser(
                id = "demo-google-user",
                email = "google.user@ecodala.com",
                displayName = "Google Eco User",
                provider = AuthProvider.Google
            )
        )
    }

    override suspend fun signOut() = Unit
}
