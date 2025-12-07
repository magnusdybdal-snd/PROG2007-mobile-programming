package com.example.motorcycleparking.domain.usecases.authentication

import com.example.motorcycleparking.domain.repositories.AuthenticationRepository
import javax.inject.Inject

/**
 * Use case for creating a user with email and password
 */
class RegisterUserUseCase @Inject constructor(
    private val repository: AuthenticationRepository
) {

    /**
     * Executes user registration
     *
     * @param email Users email address
     * @param password Users password
     * @return Result with new users ID or exception on failure
     */
    suspend operator fun invoke(email: String, password: String): Result<String> {
        return repository.createUser(email, password)
    }
}