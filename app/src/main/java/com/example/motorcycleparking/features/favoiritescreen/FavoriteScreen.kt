package com.example.motorcycleparking.features.favoiritescreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.motorcycleparking.core.core_ui.composable.BasicScreen

/**
 * Displays Page3 page
 * TODO rename and utilize
 */
@Composable
fun Screen2(
    modifier: Modifier = Modifier,
    navController: NavController
){
    BasicScreen(
        text = "Screen 2",
        color = Color.Blue
    )
}
