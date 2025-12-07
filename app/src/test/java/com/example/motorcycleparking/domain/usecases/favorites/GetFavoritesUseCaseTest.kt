package com.example.motorcycleparking.domain.usecases.favorites

import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.repositories.FavoritesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GetFavoritesUseCase
 *
 * Tests the business logic of retrieving favorite parking spots
 */
class GetFavoritesUseCaseTest {

    // System under test
    private lateinit var useCase: GetFavoritesUseCase

    // Dependencies (mocked)
    private lateinit var mockRepository: FavoritesRepository

    @Before
    fun setup() {
        // Create mock repository
        mockRepository = mockk()

        // Create use case with mocked dependency
        useCase = GetFavoritesUseCase(mockRepository)
    }

    @Test
    fun `invoke should return favorites when repository succeeds`() = runTest {
        // Given: Mock repository returns a list of favorites
        val expectedFavorites = listOf(
            ParkingSpot(
                id = "1",
                name = "Spot 1",
                note = "Test spot 1",
                displayAddress = "123 Test St",
                latitude = 63.4305,
                longitude = 10.3951,
                free = true
            ),
            ParkingSpot(
                id = "2",
                name = "Spot 2",
                note = "Test spot 2",
                displayAddress = "456 Test Ave",
                latitude = 63.4310,
                longitude = 10.3960,
                free = false
            )
        )
        coEvery { mockRepository.getFavoriteSpots() } returns Result.success(expectedFavorites)

        // When: Use case is invoked
        val result = useCase()

        // Then: Should return the same favorites
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        assertEquals("Spot 1", result.getOrNull()?.get(0)?.name)
        assertEquals("Spot 2", result.getOrNull()?.get(1)?.name)
    }

    @Test
    fun `invoke should return empty list when user has no favorites`() = runTest {
        // Given: Mock repository returns empty list
        coEvery { mockRepository.getFavoriteSpots() } returns Result.success(emptyList())

        // When: Use case is invoked
        val result = useCase()

        // Then: Should return empty list successfully
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrNull()?.size)
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given: Mock repository returns a failure
        val exception = Exception("Network error")
        coEvery { mockRepository.getFavoriteSpots() } returns Result.failure(exception)

        // When: Use case is invoked
        val result = useCase()

        // Then: Should return failure with exception
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
