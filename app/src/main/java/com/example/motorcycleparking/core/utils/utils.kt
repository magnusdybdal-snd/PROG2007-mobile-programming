package com.example.motorcycleparking.core.utils

import com.google.firebase.firestore.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

const val base32 = "0123456789bcdefghjkmnpqrstuvwxyz"

val neighborMap = mapOf(
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

val borderMap = mapOf(
    "even" to mapOf(
        "n" to "bcfguvyz",
        "s" to "0145hjnp",
        "e" to "prxz",
        "w" to "028b"
    ),
    "odd" to mapOf(
        "n" to "prxz",
        "s" to "028b",
        "e" to "bcfguvyz",
        "w" to "0145hjnp"
    )
)

fun getNeighbors(geoHash: String): List<String> {
    if (geoHash.isEmpty()) return emptyList()

    val neighbors = mutableListOf<String>()
    val dirs = listOf("n", "s", "e", "w")

    neighbors.add(geoHash)

    for (dir in dirs) {
        neighbors.add(calculateAdjacent(geoHash, dir))
    }

    neighbors.add(calculateAdjacent(calculateAdjacent(geoHash, "n"), "e")) // NE
    neighbors.add(calculateAdjacent(calculateAdjacent(geoHash, "n"), "w")) // NW
    neighbors.add(calculateAdjacent(calculateAdjacent(geoHash, "s"), "e")) // SE
    neighbors.add(calculateAdjacent(calculateAdjacent(geoHash, "s"), "w")) // SW

    return neighbors
}

fun calculateAdjacent(geoHash: String, dir: String): String {
    var lastChar = geoHash.last()
    val type = if (geoHash.length % 2 == 0) "even" else "odd"
    val border = borderMap[type]?.get(dir) ?: ""
    val neighbor = neighborMap[type]?.get(dir) ?: ""

    var parent = geoHash.dropLast(1)
    if (border.contains(lastChar) && parent.isNotEmpty()) {
        parent = calculateAdjacent(parent, dir)
    }
    val charIndex = neighbor.indexOf(lastChar)
    return parent + base32[charIndex]
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
            geohash.append(base32[ch])
            bit = 0
            ch = 0
        }
    }

    return geohash.toString()
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
