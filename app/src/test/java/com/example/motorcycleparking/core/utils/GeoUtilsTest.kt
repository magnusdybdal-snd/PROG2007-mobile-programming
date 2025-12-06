package com.example.motorcycleparking.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for GeoUtils geohash operations
 *
 * Tests the core geospatial functionality including:
 * - Geohash encoding from lat/lng coordinates
 * - Neighbor calculation (8 surrounding cells)
 * - Adjacent cell calculation (cardinal directions)
 * - Zoom level to precision mapping
 */
class GeoUtilsTest {

    // ========================================
    // encodeGeohash() Tests
    // ========================================

    @Test
    fun `encodeGeohash should encode known coordinates correctly`() {
        // Given: Known latitude/longitude pairs

        // Trondheim, Norway (NTNU area)
        val trondheim = GeoUtils.encodeGeohash(63.4305, 10.3951, 6)
        assertEquals("Trondheim geohash should have correct length", 6, trondheim.length)
        assertTrue("Trondheim geohash should be non-empty", trondheim.isNotEmpty())

        // Oslo, Norway
        val oslo = GeoUtils.encodeGeohash(59.9139, 10.7522, 6)
        assertEquals("Oslo geohash should have correct length", 6, oslo.length)
        assertTrue("Oslo geohash should be non-empty", oslo.isNotEmpty())

        // Different cities should have different geohashes
        assertTrue("Different cities should have different geohashes", trondheim != oslo)
    }

    @Test
    fun `encodeGeohash should respect precision parameter`() {
        // Given: Same coordinates with different precisions
        val lat = 63.4305
        val lon = 10.3951

        // When: Encoding with various precisions
        val precision1 = GeoUtils.encodeGeohash(lat, lon, 1)
        val precision5 = GeoUtils.encodeGeohash(lat, lon, 5)
        val precision12 = GeoUtils.encodeGeohash(lat, lon, 12)

        // Then: Length should match requested precision
        assertEquals(1, precision1.length)
        assertEquals(5, precision5.length)
        assertEquals(12, precision12.length)
    }

    @Test
    fun `encodeGeohash should encode equator correctly`() {
        // Given: Coordinates on the equator (lat = 0)
        val equatorHash = GeoUtils.encodeGeohash(0.0, 0.0, 5)

        // Then: Should produce valid geohash with correct length
        assertEquals(5, equatorHash.length)
        assertTrue("Equator geohash should be non-empty", equatorHash.isNotEmpty())

        // Should be deterministic - encoding same coords should give same result
        val equatorHash2 = GeoUtils.encodeGeohash(0.0, 0.0, 5)
        assertEquals("Same coordinates should produce same geohash", equatorHash, equatorHash2)
    }

    @Test
    fun `encodeGeohash should encode prime meridian correctly`() {
        // Given: Coordinates on prime meridian (lon = 0)
        val meridianHash = GeoUtils.encodeGeohash(51.5074, 0.0, 5)

        // Then: Should produce valid geohash for London area
        assertEquals(5, meridianHash.length)
        assertTrue("Prime meridian geohash should be non-empty", meridianHash.isNotEmpty())

        // Should differ from a location on different meridian
        val differentMeridian = GeoUtils.encodeGeohash(51.5074, 10.0, 5)
        assertTrue("Different meridians should produce different geohashes", meridianHash != differentMeridian)
    }

    @Test
    fun `encodeGeohash should handle northern hemisphere extremes`() {
        // Given: Near north pole
        val northPole = GeoUtils.encodeGeohash(89.0, 0.0, 5)

        // Then: Should produce valid geohash
        assertEquals(5, northPole.length)
    }

    @Test
    fun `encodeGeohash should handle southern hemisphere extremes`() {
        // Given: Near south pole
        val southPole = GeoUtils.encodeGeohash(-89.0, 0.0, 5)

        // Then: Should produce valid geohash
        assertEquals(5, southPole.length)
    }

    @Test
    fun `encodeGeohash should produce different hashes for nearby locations`() {
        // Given: Two locations 100 meters apart in Trondheim
        val location1 = GeoUtils.encodeGeohash(63.4305, 10.3951, 7)
        val location2 = GeoUtils.encodeGeohash(63.4315, 10.3961, 7)

        // Then: Hashes should differ (at least in later characters)
        // They may share a common prefix but should not be identical
        assertTrue("Nearby locations should have different geohashes", location1 != location2)
    }

    @Test
    fun `encodeGeohash should produce identical hashes for same location`() {
        // Given: Same coordinates encoded twice
        val hash1 = GeoUtils.encodeGeohash(63.4305, 10.3951, 7)
        val hash2 = GeoUtils.encodeGeohash(63.4305, 10.3951, 7)

        // Then: Hashes should be identical (deterministic)
        assertEquals(hash1, hash2)
    }

    @Test
    fun `encodeGeohash should handle negative coordinates`() {
        // Given: Coordinates in southern and western hemispheres
        val southAmerica = GeoUtils.encodeGeohash(-23.5505, -46.6333, 6) // São Paulo

        // Then: Should produce valid geohash
        assertEquals(6, southAmerica.length)
    }

    // ========================================
    // getNeighbors() Tests
    // ========================================

    @Test
    fun `getNeighbors should return 8 neighbors without center`() {
        // Given: A geohash
        val geoHash = "u4vug"

        // When: Getting neighbors without center
        val neighbors = GeoUtils.getNeighbors(geoHash, includeCenter = false)

        // Then: Should return exactly 8 neighbors (N, S, E, W, NE, NW, SE, SW)
        assertEquals(8, neighbors.size)
    }

    @Test
    fun `getNeighbors should return 9 cells including center`() {
        // Given: A geohash
        val geoHash = "u4vug"

        // When: Getting neighbors with center
        val neighbors = GeoUtils.getNeighbors(geoHash, includeCenter = true)

        // Then: Should return 9 cells (8 neighbors + center)
        assertEquals(9, neighbors.size)
        assertTrue("Should include the center geohash", neighbors.contains(geoHash))
    }

    @Test
    fun `getNeighbors should return empty list for empty geohash`() {
        // Given: Empty geohash
        val geoHash = ""

        // When: Getting neighbors
        val neighbors = GeoUtils.getNeighbors(geoHash)

        // Then: Should return empty list
        assertTrue(neighbors.isEmpty())
    }

    @Test
    fun `getNeighbors should return valid geohashes of same length`() {
        // Given: A geohash of length 5
        val geoHash = "u4vug"

        // When: Getting neighbors
        val neighbors = GeoUtils.getNeighbors(geoHash, includeCenter = false)

        // Then: All neighbors should have the same length
        neighbors.forEach { neighbor ->
            assertEquals("All neighbors should have same length as input", geoHash.length, neighbor.length)
        }
    }

    @Test
    fun `getNeighbors should produce unique neighbors`() {
        // Given: A geohash
        val geoHash = "u4vug"

        // When: Getting neighbors
        val neighbors = GeoUtils.getNeighbors(geoHash, includeCenter = false)

        // Then: All neighbors should be unique
        val uniqueNeighbors = neighbors.toSet()
        assertEquals("All neighbors should be unique", 8, uniqueNeighbors.size)
    }

    @Test
    fun `getNeighbors should work with single character geohash`() {
        // Given: Single character geohash
        val geoHash = "u"

        // When: Getting neighbors
        val neighbors = GeoUtils.getNeighbors(geoHash, includeCenter = false)

        // Then: Should return 8 neighbors
        assertEquals(8, neighbors.size)
    }

    // ========================================
    // calculateAdjacent() Tests
    // ========================================

    @Test
    fun `calculateAdjacent should calculate north neighbor`() {
        // Given: A geohash
        val geoHash = "u4vug"

        // When: Calculating north neighbor
        val north = GeoUtils.calculateAdjacent(geoHash, "n")

        // Then: Should return valid geohash of same length
        assertEquals(geoHash.length, north.length)
        assertTrue("North neighbor should differ from original", north != geoHash)
    }

    @Test
    fun `calculateAdjacent should calculate south neighbor`() {
        // Given: A geohash
        val geoHash = "u4vug"

        // When: Calculating south neighbor
        val south = GeoUtils.calculateAdjacent(geoHash, "s")

        // Then: Should return valid geohash of same length
        assertEquals(geoHash.length, south.length)
        assertTrue("South neighbor should differ from original", south != geoHash)
    }

    @Test
    fun `calculateAdjacent should calculate east neighbor`() {
        // Given: A geohash
        val geoHash = "u4vug"

        // When: Calculating east neighbor
        val east = GeoUtils.calculateAdjacent(geoHash, "e")

        // Then: Should return valid geohash of same length
        assertEquals(geoHash.length, east.length)
        assertTrue("East neighbor should differ from original", east != geoHash)
    }

    @Test
    fun `calculateAdjacent should calculate west neighbor`() {
        // Given: A geohash
        val geoHash = "u4vug"

        // When: Calculating west neighbor
        val west = GeoUtils.calculateAdjacent(geoHash, "w")

        // Then: Should return valid geohash of same length
        assertEquals(geoHash.length, west.length)
        assertTrue("West neighbor should differ from original", west != geoHash)
    }

    @Test
    fun `calculateAdjacent should return empty string for empty geohash`() {
        // Given: Empty geohash
        val geoHash = ""

        // When: Calculating any neighbor
        val north = GeoUtils.calculateAdjacent(geoHash, "n")

        // Then: Should return empty string
        assertEquals("", north)
    }

    @Test
    fun `calculateAdjacent should throw exception for invalid direction`() {
        // Given: Valid geohash but invalid direction
        val geoHash = "u4vug"

        // When/Then: Should throw IllegalArgumentException
        assertThrows(IllegalArgumentException::class.java) {
            GeoUtils.calculateAdjacent(geoHash, "invalid")
        }
    }

    @Test
    fun `calculateAdjacent north then south should return to original area`() {
        // Given: A geohash
        val geoHash = "u4vug"

        // When: Going north then south
        val north = GeoUtils.calculateAdjacent(geoHash, "n")
        val backToOriginal = GeoUtils.calculateAdjacent(north, "s")

        // Then: Should return to original geohash
        assertEquals("Going north then south should return to original", geoHash, backToOriginal)
    }

    @Test
    fun `calculateAdjacent east then west should return to original area`() {
        // Given: A geohash
        val geoHash = "u4vug"

        // When: Going east then west
        val east = GeoUtils.calculateAdjacent(geoHash, "e")
        val backToOriginal = GeoUtils.calculateAdjacent(east, "w")

        // Then: Should return to original geohash
        assertEquals("Going east then west should return to original", geoHash, backToOriginal)
    }

    // ========================================
    // zoomLevelToPrecision() Tests
    // ========================================

    @Test
    fun `zoomLevelToPrecision should map very low zoom to precision 1`() {
        // Given: Very low zoom levels (world view)
        // When/Then: Should return precision 1
        assertEquals(1, GeoUtils.zoomLevelToPrecision(0f))
        assertEquals(1, GeoUtils.zoomLevelToPrecision(1f))
        assertEquals(1, GeoUtils.zoomLevelToPrecision(2.9f))
    }

    @Test
    fun `zoomLevelToPrecision should map low zoom to precision 2`() {
        // Given: Low zoom levels (country view)
        // When/Then: Should return precision 2
        assertEquals(2, GeoUtils.zoomLevelToPrecision(3f))
        assertEquals(2, GeoUtils.zoomLevelToPrecision(4f))
        assertEquals(2, GeoUtils.zoomLevelToPrecision(5.9f))
    }

    @Test
    fun `zoomLevelToPrecision should map medium-low zoom to precision 3`() {
        // Given: Medium-low zoom levels (region view)
        // When/Then: Should return precision 3
        assertEquals(3, GeoUtils.zoomLevelToPrecision(6f))
        assertEquals(3, GeoUtils.zoomLevelToPrecision(7f))
        assertEquals(3, GeoUtils.zoomLevelToPrecision(8.9f))
    }

    @Test
    fun `zoomLevelToPrecision should map medium zoom to precision 4`() {
        // Given: Medium zoom levels (city view)
        // When/Then: Should return precision 4
        assertEquals(4, GeoUtils.zoomLevelToPrecision(9f))
        assertEquals(4, GeoUtils.zoomLevelToPrecision(10f))
        assertEquals(4, GeoUtils.zoomLevelToPrecision(11.9f))
    }

    @Test
    fun `zoomLevelToPrecision should map medium-high zoom to precision 5`() {
        // Given: Medium-high zoom levels (neighborhood view)
        // When/Then: Should return precision 5
        assertEquals(5, GeoUtils.zoomLevelToPrecision(12f))
        assertEquals(5, GeoUtils.zoomLevelToPrecision(13f))
        assertEquals(5, GeoUtils.zoomLevelToPrecision(14.9f))
    }

    @Test
    fun `zoomLevelToPrecision should map high zoom to precision 6`() {
        // Given: High zoom levels (street view)
        // When/Then: Should return precision 6
        assertEquals(6, GeoUtils.zoomLevelToPrecision(15f))
        assertEquals(6, GeoUtils.zoomLevelToPrecision(16f))
        assertEquals(6, GeoUtils.zoomLevelToPrecision(17.9f))
    }

    @Test
    fun `zoomLevelToPrecision should map very high zoom to precision 7`() {
        // Given: Very high zoom levels (building view)
        // When/Then: Should return precision 7
        assertEquals(7, GeoUtils.zoomLevelToPrecision(18f))
        assertEquals(7, GeoUtils.zoomLevelToPrecision(20f))
        assertEquals(7, GeoUtils.zoomLevelToPrecision(22f))
    }

    @Test
    fun `zoomLevelToPrecision should handle boundary values`() {
        // Given: Exact boundary values
        // When/Then: Should use correct precision for boundaries
        assertEquals(1, GeoUtils.zoomLevelToPrecision(2.99f))
        assertEquals(2, GeoUtils.zoomLevelToPrecision(3.0f))
        assertEquals(2, GeoUtils.zoomLevelToPrecision(5.99f))
        assertEquals(3, GeoUtils.zoomLevelToPrecision(6.0f))
        assertEquals(6, GeoUtils.zoomLevelToPrecision(17.99f))
        assertEquals(7, GeoUtils.zoomLevelToPrecision(18.0f))
    }

    @Test
    fun `zoomLevelToPrecision should handle negative zoom gracefully`() {
        // Given: Negative zoom (shouldn't happen but handle gracefully)
        // When/Then: Should return precision 1
        assertEquals(1, GeoUtils.zoomLevelToPrecision(-1f))
        assertEquals(1, GeoUtils.zoomLevelToPrecision(-100f))
    }
}
