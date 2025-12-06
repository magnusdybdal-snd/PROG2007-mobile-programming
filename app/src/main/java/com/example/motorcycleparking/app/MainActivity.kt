package com.example.motorcycleparking.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.motorcycleparking.core.core_ui.MotorcycleParkingTheme
import com.google.android.gms.maps.MapsInitializer
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point activity for the Motorcycle Parking application.
 *
 * Initializes the Compose UI with navigation, theme, Firebase Firestore, and Google Maps.
 * Serves as the host for [MainScreen] which manages the bottom navigation and screen routing.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Initializes the activity with edge-to-edge display, Firebase Firestore logging,
     * Google Maps, and sets up the Compose content with navigation and theming.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state, or null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseFirestore.setLoggingEnabled(true)
        MapsInitializer.initialize(this)


        setContent {

            val navController = rememberNavController()
            MotorcycleParkingTheme {
                MainScreen(
                    modifier = Modifier,
                    navController = navController)
            }
        }
    }
}