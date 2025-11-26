package com.example.motorcycleparking.core.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.core.net.toUri


const val BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz"

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

private fun calculateDiagonal(geoHash: String, dir1: String, dir2: String): String {
    val first = calculateAdjacent(geoHash, dir1)
    return calculateAdjacent(first, dir2)
}

fun distanceInKm(point1: GeoPoint, point2: GeoPoint): Double {
    val R = 6371.0 // Earth radius in km
    val lat1 = point1.latitude.toRadians()
    val lat2 = point2.latitude.toRadians()
    val deltaLat = (point2.latitude - point1.latitude).toRadians()
    val deltaLon = (point2.longitude - point1.longitude).toRadians()

    val a = sin(deltaLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(deltaLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}

fun Double.toRadians() = this * Math.PI / 180

fun encodeGeohash(lat: Double, lon: Double, precision: Int = 12): String {
    var isEven = true
    var bit = 0
    var ch = 0
    val geohash = StringBuilder()

    val latRange = doubleArrayOf(-90.0, 90.0)
    val lonRange = doubleArrayOf(-180.0, 180.0)

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
            zoom < 6f -> 3   // Very zoomed out - city level
            zoom < 9f -> 4   // Zoomed out - district level
            zoom < 12f -> 5  // Medium zoom - neighborhood level
            zoom < 15f -> 6  // Zoomed in - street level
            zoom < 18f -> 7  // Very zoomed in - building level
            else -> 8        // Maximum zoom - precise location
        }
    }

    /**
     * Get approximate search radius in meters for a given precision
     * Useful for debugging or showing to users
     */
    fun precisionToRadius(precision: Int): Double {
        return when (precision) {
            3 -> 78000.0    // ~78 km
            4 -> 19500.0    // ~19.5 km
            5 -> 2440.0     // ~2.4 km
            6 -> 610.0      // ~610 m
            7 -> 76.0       // ~76 m
            8 -> 19.0       // ~19 m
            else -> 610.0   // Default to precision 6
        }
    }
}
