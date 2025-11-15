package com.example.motorcycleparking.data.api

import android.util.Log
import com.example.motorcycleparking.core.utils.encodeGeohash
import com.example.motorcycleparking.data.api.dto.ParkingSpotDto
import com.example.motorcycleparking.data.api.dto.toDomain
import com.example.motorcycleparking.core.utils.getNeighbors
import com.example.motorcycleparking.data.api.dto.Favorites
import com.example.motorcycleparking.domain.models.ParkingSpot
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

            val geoHashNeighbors = getNeighbors(geoHash) + geoHash

            val results = mutableListOf<ParkingSpot>()

            val docs = db.collection("parkingspots")
                .orderBy("geo_hash")
                .startAt(geoHash)
                .endAt(geoHash + "\uf8ff")
                .get()
                .await()

            for (doc in docs) {
                val dto = ParkingSpotDto(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    note = doc.getString("note") ?: "",
                    displayAddress = doc.getString("display_address") ?: "",
                    geoPoint = doc.getGeoPoint("geo_point") ?: GeoPoint(0.0, 0.0),
                )
                dtoList.add(dto)
                Log.d(
                    "FirebaseRepo",
                    "   Added spot: ${dto.name} at (${dto.geoPoint.latitude}, ${dto.geoPoint.longitude})"
                )
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

    suspend fun toggleFavorite(userId: String, spotId: String) {
        val userRef = db.collection("users").document(userId)
        val snapshot = userRef.get().await()

        val user = snapshot.toObject(Favorites::class.java) ?: Favorites()
        val favorites = user.favorites?.toMutableList() ?: mutableListOf<String>()

        if (favorites.contains(spotId)) {
            favorites.remove(spotId)
        } else {
            favorites.add(spotId)
        }

        userRef.set(mapOf("favorites" to favorites), SetOptions.merge()).await()
        Timber.tag("UpdatedFaves").d("a new version of objects just dropped $favorites")
    }

    suspend fun getFavorites(userId: String): List<ParkingSpot> = coroutineScope {
        try {
            // Step 1: Fetch user's favorites
            val snapshot = db.collection("users")
                .document(userId)
                .get()
                .await()

            val favoriteIds = snapshot.toObject(Favorites::class.java)
                ?.favorites
                .orEmpty()

            if (favoriteIds.isEmpty()) return@coroutineScope emptyList()

            // Step 2: Firestore supports 10 items per whereIn query
            val chunks = favoriteIds.chunked(10)

            // Step 3: Run chunk fetches concurrently
            val results = chunks.map { chunk ->
                async {
                    db.collection("parkingspots")
                        .whereIn(FieldPath.documentId(), chunk)
                        .get()
                        .await()
                        .documents
                        .mapNotNull { doc ->
                            val geo = doc.getGeoPoint("geo_point") ?: return@mapNotNull null
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
            }.awaitAll().flatten()

            // Step 4: Deduplicate and return
            results.distinctBy { it.id }

        } catch (e: Exception) {
            Timber.tag("FirestoreError").e(e, "Error fetching favorites for user: $userId")
            emptyList()
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
        neLng: Double
    ): Result<List<ParkingSpot>> {
        Log.d("FirebaseRepo", "📡 Getting all spots in bounds")
        Log.d("FirebaseRepo", "   SW: ($swLat, $swLng)")
        Log.d("FirebaseRepo", "   NE: ($neLat, $neLng)")

        return try {
            val allDocs = db.collection("parkingspots")
                .get()
                .await()

            Log.d("FirebaseRepo", "📦 Found ${allDocs.size()} total spots in database")

            val spotsInBounds = mutableListOf<ParkingSpotDto>()

            for (doc in allDocs) {
                val point = doc.getGeoPoint("geo_point") ?: continue

                // Check if spot is within bounds
                if (point.latitude >= swLat &&
                    point.latitude <= neLat &&
                    point.longitude >= swLng &&
                    point.longitude <= neLng) {

                    val dto = ParkingSpotDto(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        note = doc.getString("note") ?: "",
                        displayAddress = doc.getString("display_address") ?: "",
                        geoPoint = point,
                    )
                    spotsInBounds.add(dto)
                    Log.d("FirebaseRepo", "   ✅ ${dto.name} is in bounds")
                }
            }

            Log.d("FirebaseRepo", "✅ ${spotsInBounds.size} spots within visible area")
            Result.success(spotsInBounds.toDomain())

        } catch (e: Exception) {
            Log.e("FirebaseRepo", "❌ Error: ${e.message}", e)
            Result.failure(e)
        }
    }
}

