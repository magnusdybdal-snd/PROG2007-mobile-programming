package com.example.motorcycleparking.domain.usecases.authentication

import com.example.motorcycleparking.domain.repositories.AuthenticationRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import javax.inject.Inject

/**
 * Use case for Google sign in authentication
 */
class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthenticationRepository
) {
    /**
     * Executes the Google sign in
     *
     * @param - The Google account to sign in
     * @return - Result with user ID on success or exception on failure
     */
    suspend operator fun invoke(account: GoogleSignInAccount): Result<String> {
        return repository.signInWithGoogle(account)
    }
}