package com.example.motorcycleparking.data

import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import com.example.motorcycleparking.core.utils.encodeGeohash
import com.example.motorcycleparking.core.utils.getNeighbors
import com.example.motorcycleparking.domain.models.ParkingSpot
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

object FirebaseRepo {
    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun signInWithGoogle(account: GoogleSignInAccount): String {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        return auth.signInWithCredential(credential)
            .await()
            .user
            ?.uid
            ?: throw Exception("Google sing-in failed")
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
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Log.d(TAG, "Successfully created account")
            result.user
        } catch (e: Exception) {
            Log.w(TAG, "Creation failed: ", e)
            null
        }
    }

    fun singOut() {
        auth.signOut()
    }

    suspend fun getParkingspotsNearYou(
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
                .await() // ✅ Waits for Firestore result

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
                    geoPoint = point,
                    geoHash = hash,
                    paid = free
                )
                results.add(spot)
            }
        } catch (e: Exception) {
            Log.e("FirestoreError", "Error fetching from Firestore", e)
        }

        return results
    }
}