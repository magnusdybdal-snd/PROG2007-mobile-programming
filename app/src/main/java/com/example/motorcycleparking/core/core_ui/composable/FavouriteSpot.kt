package com.example.motorcycleparking.core.core_ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ParkingSpot(
    val name: String,
    val address: String,
    val isPaid: Boolean
)

@Composable
fun ParkingSpotCard(
    parkingSpot: ParkingSpot,
    onFavoriteClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    cs: ColorScheme = MaterialTheme.colorScheme
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Text(
                    text = parkingSpot.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = parkingSpot.address,
                    fontSize = 14.sp,
                    color = cs.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = if (parkingSpot.isPaid)
                        cs.errorContainer
                    else
                        cs.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (parkingSpot.isPaid) "Paid" else "Free",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (parkingSpot.isPaid)
                            cs.onErrorContainer
                        else
                            cs.onPrimaryContainer
                    )
                }
            }

            IconButton(
                onClick = onInfoClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "information",
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

@Preview(showBackground = true)
@Composable
fun ParkingSpotCardPreview() {
    MaterialTheme {
        Column {
            ParkingSpotCard(
                parkingSpot = ParkingSpot(
                    name = "Downtown Parking",
                    address = "123 Main Street, City Center",
                    isPaid = true,
                )
            )

            ParkingSpotCard(
                parkingSpot = ParkingSpot(
                    name = "Park & Ride Lot",
                    address = "456 Oak Avenue, North Side",
                    isPaid = false
                )
            )
        }
    }
}