package com.example.motorcycleparking.features.mapscreen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.motorcycleparking.core.core_ui.composable.GoogleMapView


/**
 * Displays Map screen
 */
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val cs = MaterialTheme.colorScheme

    GoogleMapView()
//    Button( // TODO remove, this is a temporary button to get to the loginscreen.
//        onClick = {
//            // 👇 Navigate to your login route
//            navController.navigate(route = Routes.LOGIN)
//        },
//        modifier = Modifier.padding(16.dp)
//    ) {
//        Text("Go to Google Login")
//    }
}
