package com.example.motorcycleparking.data.api

import android.util.Log
import com.example.motorcycleparking.core.utils.GeoUtils
import com.example.motorcycleparking.core.utils.GeoUtils.encodeGeohash
import com.example.motorcycleparking.core.utils.GeoUtils.getNeighbors
import com.example.motorcycleparking.data.api.dto.FavoritesDto
import com.example.motorcycleparking.data.api.dto.HistoryDto
import com.example.motorcycleparking.data.api.dto.ParkingSpotDto
import com.example.motorcycleparking.data.api.dto.SearchDto
import com.example.motorcycleparking.data.api.dto.toDomain
import com.example.motorcycleparking.domain.models.ParkingSpot
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FirebaseRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun signInWithGoogle(account: GoogleSignInAccount): String {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        return auth.signInWithCredential(credential)
            .await()
            .user
            ?.uid
            ?: throw Exception("Google sign-in failed")
    }

    suspend fun signInWithEmailPassword(
        email: String,
        password: String,
    ): String {
        return auth.signInWithEmailAndPassword(email, password)
            .await()
            .user
            ?.uid
            ?: throw Exception("Sign in failed")
    }

    suspend fun createUser(email: String, password: String): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: return null
        Timber.tag("UserCreation").d("Successfully created account")
        try {
            val userData = hashMapOf(
                "favorites" to emptyList<String>()
            )

            db.collection("users").document(user.uid).set(userData).await()
        } catch (e: Exception) {
            Timber.tag("UserCreation").e(e, "Failed to create user in Firestore document")
        }
        return user
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun getParkingspotsNearYou(
        geoPoint: GeoPoint,
        precision: Int = 6
    ): Result<List<ParkingSpot>> {
        return try {
            val geoHash = encodeGeohash(geoPoint.latitude, geoPoint.longitude, precision)
            val dtoList = mutableListOf<ParkingSpotDto>()

            val geoHashNeighbors = getNeighbors(geoHash, true)

            val results = mutableListOf<ParkingSpot>()

            for (hash in geoHashNeighbors) {
                val docs = db.collection("parkingspots")
                    .orderBy("geo_hash")
                    .startAt(hash)
                    .endAt(hash + "\uf8ff")
                    .get()
                    .await()

                for (doc in docs) {
                    val dto = ParkingSpotDto(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        note = doc.getString("note") ?: "",
                        displayAddress = doc.getString("display_address") ?: "",
                        geoPoint = doc.getGeoPoint("geo_point") ?: GeoPoint(0.0, 0.0),
                        free = doc.getBoolean("free") ?: false
                    )
                    dtoList.add(dto)
                    Log.d(
                        "FirebaseRepo",
                        "   Added spot: ${dto.name} at (${dto.geoPoint.latitude}, ${dto.geoPoint.longitude})"
                    )
                }
            }
            val domainList = dtoList.toDomain()
            Log.d("FirebaseRepo", "✅ Successfully fetched ${results.size} parking spots")
            Result.success(domainList)

        } catch (e: Exception) {
            Log.e("FirebaseRepo", "❌ Error fetching from Firestore: ${e.message}", e)
            Log.e("FirebaseRepo", "Error type: ${e.javaClass.simpleName}")
            Result.failure(e)
        }
    }

    suspend fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun toggleFavorite(spotId: String) {
        val userId = auth.currentUser?.uid ?: return

        val userRef = db.collection("users").document(userId)

        try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val user = snapshot.toObject<FavoritesDto>() ?: FavoritesDto()
                val favorites = user.favorites.toMutableList()

                if (favorites.contains(spotId)) {
                    favorites.remove(spotId)
                } else {
                    favorites.add(spotId)
                }
                transaction.update(userRef, "favorites", favorites)
            }.await()

        } catch (e: Exception) {
            Timber.tag("UpdatedFaves").e(e, "Failed to update favorites for $userId")
        }
    }

    suspend fun getFavorites(): Result<List<ParkingSpot>> {
        return try {
            val userId =
                auth.currentUser?.uid ?: return Result.failure(Exception("Unable to fetch user"))

            val snapshot = db.collection("users")
                .document(userId)
                .get()
                .await()

            val favoriteIds = snapshot.toObject(FavoritesDto::class.java)?.favorites.orEmpty()

            if (favoriteIds.isEmpty()) {
                return Result.success(emptyList())
            }

            // Firestore allows 10 items per whereIn query
            val spots = favoriteIds.chunked(10).flatMap { chunk ->
                val docs = db.collection("parkingspots")
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()

                docs.documents.mapNotNull { doc ->
                    doc.getGeoPoint("geo_point")?.let { geo ->
                        ParkingSpot(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            note = doc.getString("note") ?: "",
                            displayAddress = doc.getString("display_address") ?: "",
                            latitude = geo.latitude,
                            longitude = geo.longitude,
                            free = doc.getBoolean("free")
                        )
                    }
                }
            }.distinctBy { it.id }
            Result.success(spots)

        } catch (e: Exception) {
            Timber.tag("FirestoreError").e(e, "Error fetching favorites for user")
            Result.failure(e)
        }
    }

    suspend fun getParkingById(spotId: String): Result<ParkingSpot> {
        return try {
            val doc = db.collection("parkingspots")
                .document(spotId)
                .get()
                .await()

            val geo = doc.getGeoPoint("geo_point")
                ?: return Result.failure(Exception("GeoPoint missing for spot $spotId"))

            val spot = ParkingSpot(
                id = doc.id,
                name = doc.getString("name") ?: "",
                note = doc.getString("note") ?: "",
                displayAddress = doc.getString("display_address") ?: "",
                latitude = geo.latitude,
                longitude = geo.longitude,
                free = doc.getBoolean("free")
            )

            Result.success(spot)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSearchHistory(): Result<List<HistoryDto>> {
        return try {
            val userId =
                auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            val history = db.collection("users")
                .document(userId)
                .collection("search_history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents.mapNotNull { doc ->
                    doc.toObject(HistoryDto::class.java)
                }

            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToSearchHistory(spotId: String, searched: String) {
        val userId = auth.currentUser?.uid ?: return

        val userRef = db.collection("users").document(userId)
        val historyCol = userRef.collection("search_history")

        val snapshot = historyCol
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .await()

        val docs = snapshot.documents

        db.runTransaction { tx ->
            val docsToRemove = if (docs.size >= 10) {
                docs.drop(9)  // keep the newest 9, remove the rest
            } else {
                emptyList()
            }

            for (doc in docsToRemove) {
                tx.delete(doc.reference)
            }

            val existingDoc = docs.find { it.getString("parkingspot_id") == spotId }

            val docRef = existingDoc?.reference ?: historyCol.document()
            val data = HistoryDto(
                parkingId = spotId,
                search = searched,
                timestamp = Timestamp.now()
            )

            tx.set(docRef, data)
        }.await()
    }

    suspend fun getTitlesInSearch(search: String, limit: Long = 10): Result<List<SearchDto>> {
        return try {
            val lowerSearch: String = search.lowercase()

            val snapshot = db.collection("parkingspots")
                .whereGreaterThanOrEqualTo("name_lowercase", lowerSearch)
                .whereLessThanOrEqualTo("name_lowercase", lowerSearch + "\uf8ff")
                .limit(limit)
                .get()
                .await()

            val searchedResults = snapshot.documents.mapNotNull { doc ->
                doc.toObject(SearchDto::class.java)
            }

            Result.success(searchedResults)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /**
     * Returns all parking spots within the visible map bounds.
     *
     * Fetches all spots from Firestore and filters by lat/lng coordinates.
     * A spot is included if:
     * - `swLat <= spot.latitude <= neLat`
     * - `swLng <= spot.longitude <= neLng`
     *
     * @param swLat Bottom-left latitude
     * @param swLng Bottom-left longitude
     * @param neLat Top-right latitude
     * @param neLng Top-right longitude
     *
     * @return Success with spots list, or Failure with exception
     *
     * Note: Uses client-side filtering. Fine for small datasets.
     * Replace with geohash queries for production use with many spots.
     */
    suspend fun getParkingSpotsInBounds(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double,
        zoom: Float,
        center: LatLng
    ): Result<List<ParkingSpotDto>> {
        Log.d("FirebaseRepo", "📡 Getting all spots in bounds")
        Log.d("FirebaseRepo", "   SW: ($swLat, $swLng)")
        Log.d("FirebaseRepo", "   NE: ($neLat, $neLng)")

        return try {

            val STORAGE_PRECISION = 7


            val rawSearchPrecision = GeoUtils.zoomLevelToPrecision(zoom)

            // CRITICAL: Never search with precision higher than storage precision
            val searchPrecision = minOf(rawSearchPrecision, STORAGE_PRECISION)

            val centerHash = encodeGeohash(center.latitude, center.longitude, searchPrecision)
            val searchHashes = getNeighbors(centerHash, includeCenter = true)

            Log.d("SearchHashes", searchHashes.toString())

            val spotsInBounds = mutableListOf<ParkingSpotDto>()

            for (hash in searchHashes) {
                try {
                    val snapshot = db.collection("parkingspots")
                        .orderBy("geo_hash")
                        .startAt(hash)
                        .endAt(hash + "\uf8ff") // This finds all hashes starting with 'hash'
                        .get()
                        .await()

                    val spots = snapshot.documents.mapNotNull { doc ->
                        ParkingSpotDto(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            nameLowercase = doc.getString("name_lowercase") ?: "",
                            note = doc.getString("note") ?: "",
                            displayAddress = doc.getString("display_address") ?: "",
                            geoPoint = doc.getGeoPoint("geo_point") ?: GeoPoint(0.0, 0.0),
                            free = doc.getBoolean("free") ?: false
                        )
                    }

                    Log.d("GeoSearch", "Hash $hash found ${spots.size} spots")
                    spotsInBounds.addAll(spots)

                } catch (e: Exception) {
                    Log.e("GeoSearch", "Error fetching hash $hash", e)
                }
            }

            Log.d("GeoSearch", "Total spots before filtering: ${spotsInBounds.size}")

            return Result.success(spotsInBounds)

        } catch (e: Exception) {
            Log.e("FirebaseRepo", "❌ Error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun addParkingSpot(
        name: String,
        note: String,
        displayAddress: String,
        latitude: Double,
        longitude: Double,
        isFree: Boolean
    ): Result<String> {
        return try {
            val geoHash = encodeGeohash(latitude, longitude, precision = 7)
            val geoPoint = GeoPoint(latitude, longitude)

            val parkingSpotData = hashMapOf(
                "name" to name,
                "name_lowercase" to name.lowercase(),
                "note" to note,
                "display_address" to displayAddress,
                "geo_point" to geoPoint,
                "geo_hash" to geoHash,
                "free" to isFree
            )

            val docRef = db.collection("parkingspots")
                .add(parkingSpotData)
                .await()

            Timber.tag("FirebaseRepo").d("Successfully added parking spot with ID: ${docRef.id}")
            Result.success(docRef.id)

        } catch (e: Exception) {
            Timber.tag("FirebaseRepo").e(e, "Failed to add parking spot")
            Result.failure(e)
        }
    }
}

