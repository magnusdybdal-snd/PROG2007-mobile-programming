package com.example.motorcycleparking.domain.usecases.authentication

import com.example.motorcycleparking.domain.repositories.AuthenticationRepository
import javax.inject.Inject

/**
 * Use case for signing out the current user
 */
class SignOutUseCase @Inject constructor(
    private val repository: AuthenticationRepository
) {

    /**
     * Executes sign out flow
     *
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(): Result<Unit> {
        return repository.signOut()
    }
}