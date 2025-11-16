package com.example.motorcycleparking.data.api

import com.example.motorcycleparking.core.utils.encodeGeohash
import com.example.motorcycleparking.core.utils.getNeighbors
import com.example.motorcycleparking.data.api.dto.FavoritesDto
import com.example.motorcycleparking.data.api.dto.ParkingSpotDto
import com.example.motorcycleparking.data.api.dto.SearchDto
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
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
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

        try {
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val user = snapshot.toObject<FavoritesDto>() ?: FavoritesDto()
                val favorites = user.ids.toMutableList()

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

    suspend fun getFavorites(userId: String): List<ParkingSpot> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .get()
                .await()

            val favoriteIds = snapshot.toObject(FavoritesDto::class.java)?.ids.orEmpty()
            if (favoriteIds.isEmpty()) return emptyList()

            // Firestore allows 10 items per whereIn query
            favoriteIds.chunked(10).flatMap { chunk ->
                db.collection("parkingspots")
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()
                    .documents.mapNotNull { doc ->
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

        } catch (e: Exception) {
            Timber.tag("FirestoreError").e(e, "Error fetching favorites for user: $userId")
            emptyList()
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
}
