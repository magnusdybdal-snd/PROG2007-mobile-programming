package com.example.motorcycleparking.features.mapscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.motorcycleparking.core.core_navigation.Routes
import com.example.motorcycleparking.core.core_ui.GreenBackground
import com.example.motorcycleparking.core.core_ui.composable.BasicScreen

/**
 * Displays Map screen
 */
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    navController: NavController
){
    BasicScreen(
        text = "Map Screen",
        color = GreenBackground,
    )

    Button(
        onClick = {
            // 👇 Navigate to your login route
            navController.navigate(route = Routes.LOGIN)
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Go to Google Login")
    }
}
