package com.example.motorcycleparking.data.repositories

import android.util.Log
import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.domain.repositories.AuthenticationRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import javax.inject.Inject

/**
 * Implementation of the AuthenticationRepository
 *
 * This class acts as the data layer implementation of the repository and delegates authentication
 * operations to the FirebaseRepo.
 */
class AuthenticationRepositoryImpl @Inject constructor(
    private val firebaseRepo: FirebaseRepo
): AuthenticationRepository {

    companion object {
        private const val TAG = "AuthenticationRepositoryImpl"
    }

    override suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<String> {
        return try {
            val userId = firebaseRepo.signInWithGoogle(account)
            Result.success(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Google sign in failed")
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): Result<String> {
        return try {
            val userId = firebaseRepo.signInWithEmailPassword(email, password)
            Result.success(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Email sign in failed")
            Result.failure(e)
        }
    }

    override suspend fun createUser(email: String, password: String): Result<String> {
        return try {
            val user = firebaseRepo.createUser(email, password)
            val userId = user?.uid ?: throw Exception("Failed to create user")
            Result.success(userId)
        } catch (e: Exception) {
            Log.e(TAG, "User creation failed")
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            firebaseRepo.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Sign out failed")
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUserId(): String? {
        return firebaseRepo.getCurrentUserId()
    }
}