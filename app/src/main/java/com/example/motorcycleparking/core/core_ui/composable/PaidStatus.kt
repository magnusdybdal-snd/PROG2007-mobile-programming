package com.example.motorcycleparking.core.core_ui.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.motorcycleparking.domain.models.ParkingSpot

@Composable
fun PaidStatus(parkingSpot: ParkingSpot) {
    val cs = MaterialTheme.colorScheme
    Surface(
        color = if (parkingSpot.free == false)
            cs.errorContainer
        else
            cs.primaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = when (parkingSpot.free) {
                false -> "Paid"
                null -> "Unknown"
                else -> "Free"
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = when (parkingSpot.free) {
                false -> cs.onErrorContainer
                null -> cs.onPrimaryContainer
                else -> cs.onPrimaryContainer
            }
        )
    }
}