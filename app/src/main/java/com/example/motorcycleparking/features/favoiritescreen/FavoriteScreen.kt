package com.example.motorcycleparking.features.favoiritescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.motorcycleparking.core.core_ui.composable.PageHeading
import com.example.motorcycleparking.core.core_ui.composable.ParkingSpotCard
import com.example.motorcycleparking.domain.models.ParkingSpot

/**
 * Displays users favorite parkignspots.
 */
@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    cs: ColorScheme
){
    Column {
        PageHeading(
            modifier = Modifier,
            text = "Favorites"
        )

        ParkingSpotCard(
            parkingSpot = ParkingSpot(
                name = "Downtown Parking",
                id = null,
                displayAddress = "123 Main Street, City Center",
                free = true,
                note = null
            )
        )

        ParkingSpotCard(
            parkingSpot = ParkingSpot(
                name = "Park & Ride Lot",
                id = null,
                displayAddress = "456 Oak Avenue, North Side",
                free = false,
                note = null
            )

        )
    }
}
