package com.example.motorcycleparking.data.api

import android.service.controls.ControlsProviderService
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
class FirebaseRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val auth: FirebaseAuth = Firebase.auth

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
            Log.d(ControlsProviderService.TAG, "Successfully created account")
            return result.user
    }
    fun signOut(){
        auth.signOut()
    }
}
