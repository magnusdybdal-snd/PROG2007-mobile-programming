package com.example.motorcycleparking.core.utils

fun encodeGeohash(lat: Double, lon: Double, precision: Int = 12): String {
    val base32 = "0123456789bcdefghjkmnpqrstuvwxyz"

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
