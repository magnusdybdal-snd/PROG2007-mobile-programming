package com.example.motorcycleparking.domain.usecases.authentication

import com.example.motorcycleparking.domain.repositories.AuthenticationRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SignInWithEmailPasswordUseCase
 *
 * Tests email/password authentication business logic
 */
class SignInWithEmailPasswordUseCaseTest {

    // System under test
    private lateinit var useCase: SignInWithEmailPasswordUseCase

    // Dependencies (mocked)
    private lateinit var mockRepository: AuthenticationRepository

    @Before
    fun setup() {
        mockRepository = mockk()
        useCase = SignInWithEmailPasswordUseCase(mockRepository)
    }

    @Test
    fun `invoke should return userId on successful sign in`() = runTest {
        // Given: Mock repository returns success with user ID
        coEvery {
            mockRepository.signInWithEmailPassword("test@example.com", "password123")
        } returns Result.success("user123")

        // When: Use case is invoked
        val result = useCase("test@example.com", "password123")

        // Then: Should return user ID successfully
        assertTrue(result.isSuccess)
        assertEquals("user123", result.getOrNull())
    }

    @Test
    fun `invoke should return failure on invalid credentials`() = runTest {
        // Given: Mock repository returns failure
        val exception = Exception("Invalid email or password")
        coEvery {
            mockRepository.signInWithEmailPassword("wrong@example.com", "wrongpass")
        } returns Result.failure(exception)

        // When: Use case is invoked
        val result = useCase("wrong@example.com", "wrongpass")

        // Then: Should return failure with exception
        assertTrue(result.isFailure)
        assertEquals("Invalid email or password", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should return failure on network error`() = runTest {
        // Given: Mock repository throws network exception
        val exception = Exception("Network error")
        coEvery {
            mockRepository.signInWithEmailPassword(any(), any())
        } returns Result.failure(exception)

        // When: Use case is invoked
        val result = useCase("test@example.com", "password123")

        // Then: Should return failure
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network") == true)
    }

    @Test
    fun `invoke should handle empty email`() = runTest {
        // Given: Mock repository returns validation error
        val exception = Exception("Email cannot be empty")
        coEvery {
            mockRepository.signInWithEmailPassword("", "password123")
        } returns Result.failure(exception)

        // When: Use case is invoked with empty email
        val result = useCase("", "password123")

        // Then: Should return failure
        assertTrue(result.isFailure)
        assertEquals("Email cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should handle empty password`() = runTest {
        // Given: Mock repository returns validation error
        val exception = Exception("Password cannot be empty")
        coEvery {
            mockRepository.signInWithEmailPassword("test@example.com", "")
        } returns Result.failure(exception)

        // When: Use case is invoked with empty password
        val result = useCase("test@example.com", "")

        // Then: Should return failure
        assertTrue(result.isFailure)
        assertEquals("Password cannot be empty", result.exceptionOrNull()?.message)
    }
}
