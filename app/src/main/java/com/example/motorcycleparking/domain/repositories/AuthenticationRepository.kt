package com.example.motorcycleparking.domain.repositories

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * Repository interface for authentication operations.
 */
interface AuthenticationRepository {

    /**
     * Sign in using a Google account
     *
     * @param account - The Google account from Google sign-in
     * @return - Result with the users unique ID on success or exception on failure
     */
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<String>

    /**
     * Sign in using email and password
     *
     * @param email - Users email address
     * @param password - Users password
     * @return - Result with the users unique ID on success or exception on failure
     */
    suspend fun signInWithEmailPassword(email: String, password: String): Result<String>

    /**
     * Create a new user account with username and password
     *
     * @param email - Users email address
     * @param password - Users password
     * @return - Result with the users unique ID on success or exception on failure
     */
    suspend fun createUser(email: String, password: String): Result<String>

    /**
     * Sign out the currently authenticated user
     *
     * @return - Result indicating failure or success on the sign out
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Gets the currently authenticated user
     *
     * @return - The users unique ID if authenticated, null otherwise
     */
    suspend fun getCurrentUserId(): String?
}