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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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