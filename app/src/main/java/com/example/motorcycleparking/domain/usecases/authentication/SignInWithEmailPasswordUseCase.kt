package com.example.motorcycleparking.domain.usecases.authentication

import com.example.motorcycleparking.domain.repositories.AuthenticationRepository
import javax.inject.Inject

/**
 * Use case for email and password authentication
 */
class SignInWithEmailPasswordUseCase @Inject constructor(
    private val repository: AuthenticationRepository
) {
    /**
     * Executes the email and password sign in
     *
     * @param email - The users email address
     * @param password - The users password
     * @return - Result with user ID on success or exception on failure
     */
    suspend operator fun invoke(email: String, password: String): Result<String> {
        return repository.signInWithEmailPassword(email, password)
    }
}