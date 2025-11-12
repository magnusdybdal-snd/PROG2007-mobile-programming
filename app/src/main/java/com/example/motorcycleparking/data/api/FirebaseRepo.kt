package com.example.motorcycleparking.data.api

import com.example.motorcycleparking.core.utils.encodeGeohash
import com.example.motorcycleparking.core.utils.getNeighbors
import com.example.motorcycleparking.data.api.dto.Favorites
import com.example.motorcycleparking.data.api.dto.ParkingSpotDto
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

    suspend fun getParkingSpotsNearYou(
        lat: Double,
        lon: Double,
        precision: Int = 6,
    ): Result<List<ParkingSpotDto>> = try {
        val geoHash = encodeGeohash(lat, lon, precision)
        val geoHashNeighbors = getNeighbors(geoHash) + geoHash

        val results = coroutineScope {
            geoHashNeighbors.map { hash ->
                async {
                    db.collection("parkingspots")
                        .orderBy("geo_hash")
                        .startAt(hash)
                        .endAt(hash + "\uf8ff")
                        .get()
                        .await()
                        .mapNotNull { doc ->
                            ParkingSpotDto(
                                name = doc.getString("name").orEmpty(),
                                id = doc.id,
                                note = doc.getString("note").orEmpty(),
                                displayAddress = doc.getString("display_address").orEmpty(),
                                geoPoint = doc.getGeoPoint("geo_point") ?: GeoPoint(0.0, 0.0),
                                free = doc.getBoolean("free") ?: false
                            )
                        }
                }
            }.awaitAll().flatten()
        }
        Result.success(results)
    } catch (e: Exception) {
        Timber.tag("ParkingFetch").e(e, "Error fetching parking spots")
        Result.failure(e)
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
}
