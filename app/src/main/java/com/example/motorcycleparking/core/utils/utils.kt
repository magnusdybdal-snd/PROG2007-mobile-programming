package com.example.motorcycleparking.core.utils

import android.net.Uri
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.firestore.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.core.net.toUri

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

fun openGoogleMapsNavigation(
    context: Context,
    latitude: Double,
    longitude: Double,
    directNaviagtion: Boolean = true
) {
    try {
        // Create URI for navigation
        val uri = if (directNaviagtion) {
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
        try {
            val browserUri =
                "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude".toUri()
            val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
            context.startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open maps", Toast.LENGTH_SHORT).show()
        }
    }
}
