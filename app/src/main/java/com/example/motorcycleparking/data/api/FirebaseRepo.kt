package com.example.motorcycleparking.data.api

import com.example.motorcycleparking.core.utils.encodeGeohash
import com.example.motorcycleparking.core.utils.getNeighbors
import com.example.motorcycleparking.domain.models.Favorites
import com.example.motorcycleparking.domain.models.ParkingSpot
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
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

    fun singOut() {
        auth.signOut()
    }

    suspend fun getParkingSpotsNearYou(
        geoPoint: GeoPoint,
        precision: Int = 6
    ): MutableList<ParkingSpot> {
        val geoHash = encodeGeohash(geoPoint.latitude, geoPoint.longitude, precision)
        val geoHashNeighbors = getNeighbors(geoHash)

        val results = mutableListOf<ParkingSpot>()

        try {
            val docs = db.collection("parkingspots")
                .orderBy("geo_hash")
                .startAt(geoHash)
                .endAt(geoHash + "\uf8ff")
                .get()
                .await()

            for (doc in docs) {
                val point = doc.getGeoPoint("geo_point") ?: GeoPoint(0.0, 0.0)
                val name = doc.getString("name") ?: ""
                val note = doc.getString("note") ?: ""
                val free = doc.getBoolean("free") ?: true
                val addr = doc.getString("display_address") ?: ""
                val hash = doc.getString("geo_hash") ?: ""
                val spot = ParkingSpot(
                    name = name,
                    id = doc.id,
                    note = note,
                    displayAddress = addr,
                )
                results.add(spot)
            }
        } catch (e: Exception) {
            Timber.tag("FirestoreError").e(e, "Error fetching from Firestore")
        }

        return results
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

        userRef.update("favorites", favorites).await()
        Timber.tag("UpdatedFaves").d("a new version of objects just dropped")
    }

    suspend fun getFavorites(userId: String): List<ParkingSpot> {
        // TODO: Implement
        return emptyList()
    }
}
