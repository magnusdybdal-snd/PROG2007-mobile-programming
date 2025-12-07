package com.example.motorcycleparking.domain.usecases.authentication

import com.example.motorcycleparking.domain.repositories.AuthenticationRepository
import javax.inject.Inject

/**
 * Use case for retrieving the current authenticated users ID
 */
class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthenticationRepository
) {

    /**
     * Gets the current users ID
     *
     * @return User ID if authenticated, null otherwise
     */
    suspend operator fun invoke(): String? {
        return repository.getCurrentUserId()
    }
}