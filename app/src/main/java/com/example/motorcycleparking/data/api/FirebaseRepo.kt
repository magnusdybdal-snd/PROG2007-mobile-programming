package com.example.motorcycleparking.data.api

import android.util.Log
import com.example.motorcycleparking.core.utils.GeoUtils
import com.example.motorcycleparking.core.utils.GeoUtils.encodeGeohash
import com.example.motorcycleparking.core.utils.GeoUtils.getNeighbors
import com.example.motorcycleparking.data.api.dto.FavoritesDto
import com.example.motorcycleparking.data.api.dto.HistoryDto
import com.example.motorcycleparking.data.api.dto.ParkingSpotDto
import com.example.motorcycleparking.data.api.dto.SearchDto
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

/**
 * Repository responsible for handling Firebase Authentication and Firestore interactions.
 *
 * Provides:
 * - Google & Email authentication
 * - Parking spot geolocation queries using geohashes
 * - Search history & favorites management
 * - Firestore CRUD helpers for parking spots
 */
class FirebaseRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    /**
     * Signs in the user using their Google account.
     *
     * @param account The Google account returned by the Google Sign-In intent.
     * @return The authenticated Firebase user ID.
     * @throws Exception if the sign-in fails.
     */
    suspend fun signInWithGoogle(account: GoogleSignInAccount): String {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val uid = auth.signInWithCredential(credential)
            .await()
            .user
            ?.uid
            ?: throw Exception("Google sign-in failed")

        // Initialize user document if it doesn't exist
        initializeUserDocument(uid)

        return uid
    }

    /**
     * Signs in a user using an email/password combination.
     *
     * @return The user's Firebase UID.
     * @throws Exception on sign-in failure.
     */
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

    /**
     * Creates a user using email/password and sets up their Firestore profile document.
     *
     * Initializes:
     *  - favorites = empty list
     *
     * @return The newly created FirebaseUser, or null if creation failed.
     */
    suspend fun createUser(email: String, password: String): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: return null

        try {
            val userData = hashMapOf(
                "favorites" to emptyList<String>()
            )

            db.collection("users").document(user.uid).set(userData).await()
        } catch (e: Exception) {
            Timber.tag("UserCreation").e(e, "Failed to create user Firestore document")
        }
        return user
    }

    /** Signs out the currently authenticated user. */
    fun signOut() {
        auth.signOut()
    }


    private suspend fun initializeUserDocument(uid: String) {
        try {
            val userRef = db.collection("users").document(uid)
            val snapshot = userRef.get().await()

            if (!snapshot.exists()) {
                val userData = hashMapOf(
                    "favorites" to emptyList<String>()
                )
                userRef.set(userData).await()
                Timber.tag("UserInit").d("Created user document for Google Sign-In user: $uid")
            } else {
                Timber.tag("UserInit").d("User document already exists for: $uid")
            }
        } catch (e: Exception) {
            Timber.tag("UserInit").e(e, "Failed to initialize user document for $uid")
        }
    }

    /** Returns the currently logged-in user's UID, or null if not authenticated. */
    suspend fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Toggles a parking spot in the user's favorites list.
     *
     * Uses a Firestore transaction to ensure safe concurrent updates.
     */
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
            Timber.tag("UpdatedFaves").e(e, "Failed to update favorites")
        }
    }

    /**
     * Retrieves all parking spots that the user has marked as favorites.
     *
     * Handles Firestore’s limitation of 10 IDs per `whereIn` call by chunking.
     */
    suspend fun getFavorites(): Result<List<ParkingSpot>> {
        return try {
            val userId =
                auth.currentUser?.uid ?: return Result.failure(Exception("Unable to fetch user"))

            val snapshot = db.collection("users")
                .document(userId)
                .get()
                .await()

            val favoriteIds = snapshot.toObject(FavoritesDto::class.java)?.favorites.orEmpty()

            if (favoriteIds.isEmpty()) return Result.success(emptyList())

            // Chunk into groups of 10 (Firestore limit)
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
            Result.failure(e)
        }
    }

    /**
     * Fetch a single parking spot by its Firestore ID.
     */
    suspend fun getParkingById(spotId: String): Result<ParkingSpot> {
        return try {
            val doc = db.collection("parkingspots")
                .document(spotId)
                .get()
                .await()

            val geo = doc.getGeoPoint("geo_point")
                ?: return Result.failure(Exception("GeoPoint missing"))

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

    /**
     * Returns the user's search history (max 20 most recent).
     */
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
                .documents.mapNotNull { it.toObject(HistoryDto::class.java) }

            Result.success(history)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Adds a parking spot search to the history.
     *
     * - Keeps only the 10 newest entries.
     * - If a spot is already in the history, its timestamp is updated instead.
     */
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
            // Remove old entries (keep newest 9)
            val docsToRemove = if (docs.size >= 10) docs.drop(9) else emptyList()
            docsToRemove.forEach { tx.delete(it.reference) }

            // Update or create entry
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

    /**
     * Searches for parking spots by name (prefix search).
     *
     * Uses:
     *  where name_lowercase >= search
     *  where name_lowercase <= search + "\uf8ff"
     */
    suspend fun getTitlesInSearch(search: String, limit: Long = 10): Result<List<SearchDto>> {
        return try {
            val lowerSearch = search.lowercase()

            val snapshot = db.collection("parkingspots")
                .whereGreaterThanOrEqualTo("name_lowercase", lowerSearch)
                .whereLessThanOrEqualTo("name_lowercase", lowerSearch + "\uf8ff")
                .limit(limit)
                .get()
                .await()

            Result.success(snapshot.documents.mapNotNull { it.toObject(SearchDto::class.java) })

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Returns all parking spots within visible map bounds.
     *
     * Uses geohash-based filtering from the map center's precision.
     * This reduces Firestore reads compared to fetching everything.
     *
     * @param zoom Map zoom level → used to determine geohash precision.
     */
    suspend fun getParkingSpotsInBounds(
        zoom: Float,
        center: LatLng
    ): Result<List<ParkingSpotDto>> {
        return try {
            val precision = GeoUtils.zoomLevelToPrecision(zoom)
            val centerHash = encodeGeohash(center.latitude, center.longitude, precision)
            val searchHashes = getNeighbors(centerHash, includeCenter = true)

            val results = mutableListOf<ParkingSpotDto>()

            for (hash in searchHashes) {
                try {
                    val snapshot = db.collection("parkingspots")
                        .orderBy("geo_hash")
                        .startAt(hash)
                        .endAt(hash + "\uf8ff")
                        .get()
                        .await()

                    // Convert Firestore docs to DTOs manually to include id
                    for (doc in snapshot.documents) {
                        val dto = ParkingSpotDto(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            note = doc.getString("note") ?: "",
                            displayAddress = doc.getString("display_address") ?: "",
                            geoPoint = doc.getGeoPoint("geo_point") ?: GeoPoint(0.0, 0.0),
                            free = doc.getBoolean("free") ?: false
                        )
                        results.add(dto)
                    }

                } catch (e: Exception) {
                    Log.e("GeoSearch", "Error fetching hash $hash", e)
                }
            }

            Result.success(results)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Adds a new parking spot to Firestore.
     *
     * Automatically sets:
     *  - lowercase name (for search)
     *  - geohash & geopoint
     */
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

            val parkingSpotData = hashMapOf(
                "name" to name,
                "name_lowercase" to name.lowercase(),
                "note" to note,
                "display_address" to displayAddress,
                "geo_point" to GeoPoint(latitude, longitude),
                "geo_hash" to geoHash,
                "free" to isFree
            )

            val docRef = db.collection("parkingspots")
                .add(parkingSpotData)
                .await()

            Result.success(docRef.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
