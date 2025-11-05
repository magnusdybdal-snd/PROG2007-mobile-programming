package com.example.motorcycleparking.features.settingsscreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.motorcycleparking.core.core_ui.composable.BasicScreen

/**
 * Displays Screen 3
 * TODO rename and utilize
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController
){
    BasicScreen(
        text = "Settings",
        color = Color.Red
    )
}
