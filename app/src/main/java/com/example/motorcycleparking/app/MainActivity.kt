package com.example.motorcycleparking.app

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.motorcycleparking.core.core_ui.MotorcycleParkingTheme
import com.example.motorcycleparking.data.FirebaseRepo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseFirestore.setLoggingEnabled(true)

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