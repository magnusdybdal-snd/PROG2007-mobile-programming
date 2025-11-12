package com.example.motorcycleparking.core.core_ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.getValue
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motorcycleparking.core.core_ui.AppCard
import com.example.motorcycleparking.core.utils.openGoogleMapsNavigation
import com.example.motorcycleparking.domain.models.ParkingSpot


@Composable
fun ParkingSpotCard(
    modifier: Modifier = Modifier,
    parkingSpot: ParkingSpot,
    onFavoriteClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    cs: ColorScheme = MaterialTheme.colorScheme
) {
    val context = LocalContext.current
    var showNavigationDialog by remember { mutableStateOf(false) }

    // Show confirmation dialog
    if (showNavigationDialog) {
        NavigationConfirmationDialog(
            parkingSpotName = parkingSpot.name,
            onConfirm = {
                showNavigationDialog = false
                val lat = parkingSpot.latitude
                val lon = parkingSpot.longitude
                if (lat != null && lon != null) {
                    openGoogleMapsNavigation(
                        context = context,
                        latitude = lat,
                        longitude = lon
                    )
                }
            },
            onDismiss = {
                showNavigationDialog = false
            }
        )
    }


    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = AppCard.colors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Parking spot details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                parkingSpot.name?.let {
                    Text(
                        text = it,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = cs.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                parkingSpot.displayAddress?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = cs.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                PaidStatus(parkingSpot)
            }

            IconButton(
                onClick = {
                    showNavigationDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Directions,
                    contentDescription = "Navigate to parking spot",
                    tint = cs.primary
                )
            }
            // Right side: Heart icon
            IconButton(
                onClick = onFavoriteClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorite",
                    tint = cs.primary
                )
            }
        }
    }
}