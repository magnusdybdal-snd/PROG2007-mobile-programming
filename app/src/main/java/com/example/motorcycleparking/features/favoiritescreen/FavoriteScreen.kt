package com.example.motorcycleparking.features.favoiritescreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.motorcycleparking.core.core_ui.composable.PageHeading

/**
 * Displays Page3 page
 * TODO rename and utilize
 */
@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    navController: NavController
){
    PageHeading(
        modifier = Modifier,
        text = "Favorites"
    )
}
