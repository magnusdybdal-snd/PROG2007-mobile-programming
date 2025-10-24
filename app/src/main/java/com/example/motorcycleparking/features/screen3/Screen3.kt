package com.example.motorcycleparking.features.screen3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.motorcycleparking.core.core_ui.composable.BasicScreen

/**
 * Displays Screen 3
 * TODO rename and utilize
 */
@Composable
fun Screen3(
    modifier: Modifier = Modifier,
    navController: NavController
){
    BasicScreen(
        text = "Settings",
        color = Color.Red
    )
}
