package com.example.motorcycleparking.domain.usecases.favorites

import app.cash.turbine.test
import com.example.motorcycleparking.domain.repositories.FavoritesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ToggleFavoriteUseCase
 *
 * Tests both the toggle functionality and SharedFlow event emission
 * for cross-component communication
 */
class ToggleFavoriteUseCaseTest {

    // System under test
    private lateinit var useCase: ToggleFavoriteUseCase

    // Dependencies (mocked)
    private lateinit var mockRepository: FavoritesRepository

    @Before
    fun setup() {
        mockRepository = mockk()
        useCase = ToggleFavoriteUseCase(mockRepository)
    }

    @Test
    fun `invoke should toggle favorite and emit event when repository succeeds`() = runTest {
        // Given: Mock repository succeeds
        coEvery { mockRepository.toggleFavorite("spot123") } returns Result.success(Unit)

        // When: Use case is invoked
        val result = useCase("spot123")

        // Then: Should succeed
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should emit favoritesChanged event on successful toggle`() = runTest {
        // Given: Mock repository succeeds
        coEvery { mockRepository.toggleFavorite("spot123") } returns Result.success(Unit)

        // When: Collect events and invoke use case
        useCase.favoritesChanged.test {
            // Invoke the use case
            val result = useCase("spot123")

            // Then: Should emit exactly one event
            awaitItem() // Wait for the emitted event
            assertTrue(result.isSuccess)
        }
    }

    @Test
    fun `invoke should NOT emit event when toggle fails`() = runTest {
        // Given: Mock repository fails
        val exception = Exception("Network error")
        coEvery { mockRepository.toggleFavorite("spot123") } returns Result.failure(exception)

        // When: Invoke the use case (should fail)
        val result = useCase("spot123")

        // Then: Should fail without emitting event
        assertTrue(result.isFailure)

        // Verify no events are emitted by testing the flow
        useCase.favoritesChanged.test {
            expectNoEvents()
        }
    }

    @Test
    fun `invoke should return failure with exception when repository fails`() = runTest {
        // Given: Mock repository fails
        val exception = Exception("Database error")
        coEvery { mockRepository.toggleFavorite("spot456") } returns Result.failure(exception)

        // When: Use case is invoked
        val result = useCase("spot456")

        // Then: Should return failure with correct exception
        assertTrue(result.isFailure)
        assertEquals("Database error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `multiple toggles should emit multiple events`() = runTest {
        // Given: Mock repository succeeds for multiple calls
        coEvery { mockRepository.toggleFavorite(any()) } returns Result.success(Unit)

        // When: Collect events and invoke multiple times
        useCase.favoritesChanged.test {
            // Toggle first spot
            useCase("spot1")
            awaitItem() // First event

            // Toggle second spot
            useCase("spot2")
            awaitItem() // Second event

            // Toggle third spot
            useCase("spot3")
            awaitItem() // Third event

            // All three events should have been emitted
            expectNoEvents()
        }
    }
}
