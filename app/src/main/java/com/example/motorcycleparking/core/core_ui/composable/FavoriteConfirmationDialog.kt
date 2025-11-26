package com.example.motorcycleparking.core.core_ui.composable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun FavoriteConfirmationDialog(
    parkingSpotName: String? = "this parking spot",
    isFavorite: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isFavorite) {
                    "Remove from Favorites?"
                } else {
                    "Add to Favorites?"
                },
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = if (isFavorite) {
                    "Remove $parkingSpotName from your favorites?"
                } else {
                    "Add $parkingSpotName to your favorites?"
                }
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(if (isFavorite) "Remove" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}