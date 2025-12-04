package com.example.motorcycleparking.core.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri

// Base 32 encoding used to hash position
const val BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz"

// Map to get the neighbors of a geohash
private val NEIGHBORS = mapOf(
    "even" to mapOf(
        "n" to "p0r21436x8zb9dcf5h7kjnmqesgutwvy",
        "s" to "14365h7k9dcfesgujnmqp0r2twvyx8zb",
        "e" to "bc01fg45238967deuvhjyznpkmstqrwx",
        "w" to "238967debc01fg45kmstqrwxuvhjyznp"
    ),
    "odd" to mapOf(
        "n" to "bc01fg45238967deuvhjyznpkmstqrwx",
        "s" to "238967debc01fg45kmstqrwxuvhjyznp",
        "e" to "p0r21436x8zb9dcf5h7kjnmqesgutwvy",
        "w" to "14365h7k9dcfesgujnmqp0r2twvyx8zb"
    )
)

// The borders of a geohash square
private val BORDERS = mapOf(
    "even" to mapOf(
        "n" to "prxz",
        "s" to "028b",
        "e" to "bcfguvyz",
        "w" to "0145hjnp"
    ),
    "odd" to mapOf(
        "n" to "bcfguvyz",
        "s" to "0145hjnp",
        "e" to "prxz",
        "w" to "028b"
    )
)

fun openGoogleMapsNavigation(
    context: Context,
    latitude: Double,
    longitude: Double,
    directNavigation: Boolean = true
) {
    try {
        // Create URI for navigation
        val uri = if (directNavigation) {
            // Start navigation immediately (for favorites)
            "google.navigation:q=$latitude,$longitude".toUri()
        } else {
            // Show route preview first (for regular spots)
            "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude&travelmode=driving".toUri()
        }
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }

        context.startActivity(intent)
    } catch (e: Exception) {
        // Google maps app not installed, open in browser
        Log.w("Navigation", "Google Maps app not available, falling back to browser", e)
        try {
            val browserUri =
                "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude".toUri()
            val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            Log.e("Navigation", "Failed to open maps in browser", e)
            Toast.makeText(context, "Unable to open maps", Toast.LENGTH_SHORT).show()
        }
    }
}

object GeoUtils {
    /**
     * Convert Google Maps zoom level to geohash precision
     * Higher zoom = more precision = more geohash characters
     */
    fun zoomLevelToPrecision(zoom: Float): Int {
        return when {
            zoom < 3f -> 1   // as small as possible
            zoom < 5f -> 2   // world/continent
            zoom < 7f -> 3   // country
            zoom < 9f -> 3   // large region
            zoom < 11f -> 4  // region/state
            zoom < 13f -> 4  // city area
            zoom < 15f -> 5  // city/neighborhood
            zoom < 17f -> 6  // neighborhood/street
            zoom < 19f -> 7  // street/block
            else -> 7        // max out at storage precision
        }
    }

    /**
     * Get the neighbors of a given hash
     */
    fun getNeighbors(geoHash: String, includeCenter: Boolean = false): List<String> {
        if (geoHash.isEmpty()) return emptyList()

        val n = calculateAdjacent(geoHash, "n")
        val s = calculateAdjacent(geoHash, "s")
        val e = calculateAdjacent(geoHash, "e")
        val w = calculateAdjacent(geoHash, "w")

        val diagonals = listOf(
            calculateDiagonal(geoHash, "n", "e"), // NE
            calculateDiagonal(geoHash, "n", "w"), // NW
            calculateDiagonal(geoHash, "s", "e"), // SE
            calculateDiagonal(geoHash, "s", "w")  // SW
        )

        val neighbors = mutableListOf(n, s, e, w)
        neighbors.addAll(diagonals)

        if (includeCenter) neighbors.add(geoHash)

        return neighbors
    }

    /**
     * Calculate the adjacent border for a given geohash
     */
    fun calculateAdjacent(geoHash: String, dir: String): String {
        if (geoHash.isEmpty()) return ""

        val lastChar = geoHash.last()
        val type = if (geoHash.length % 2 == 0) "even" else "odd"

        var parent = geoHash.dropLast(1)

        // If last character is at the border, calculate parent's adjacent
        if (BORDERS[type]?.get(dir)?.contains(lastChar) == true && parent.isNotEmpty()) {
            parent = calculateAdjacent(parent, dir)
        }

        val neighborStr =
            NEIGHBORS[type]?.get(dir) ?: throw IllegalArgumentException("Invalid direction")
        val index = neighborStr.indexOf(lastChar)
        if (index == -1) throw IllegalArgumentException("Invalid geohash character: $lastChar")

        return parent + BASE32[index]
    }

    /**
     * Calculate the diagonal point
     */
    private fun calculateDiagonal(geoHash: String, dir1: String, dir2: String): String {
        val first = calculateAdjacent(geoHash, dir1)
        return calculateAdjacent(first, dir2)
    }

    /**
     * Encode a simple lat and lon point and generate a square with the point
     */
    fun encodeGeohash(lat: Double, lon: Double, precision: Int = 12): String {
        var isEven = true
        var bit = 0
        var ch = 0
        val geohash = StringBuilder()

        val latRange = doubleArrayOf(-90.0, 90.0)
        val lonRange = doubleArrayOf(-180.0, 180.0)

        // Loop until the string is as long as precision
        while (geohash.length < precision) {
            val mid: Double
            if (isEven) {
                mid = (lonRange[0] + lonRange[1]) / 2
                if (lon > mid) {
                    ch = ch or (1 shl (4 - bit))
                    lonRange[0] = mid
                } else {
                    lonRange[1] = mid
                }
            } else {
                mid = (latRange[0] + latRange[1]) / 2
                if (lat > mid) {
                    ch = ch or (1 shl (4 - bit))
                    latRange[0] = mid
                } else {
                    latRange[1] = mid
                }
            }

            // Swap the direction
            isEven = !isEven

            if (bit < 4) {
                bit++
            } else {
                geohash.append(BASE32[ch])
                bit = 0
                ch = 0
            }
        }

        return geohash.toString()
    }
}
