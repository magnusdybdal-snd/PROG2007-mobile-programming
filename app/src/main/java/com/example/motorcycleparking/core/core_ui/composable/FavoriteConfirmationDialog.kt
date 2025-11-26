package com.example.motorcycleparking.core.core_ui.composable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
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
    val cs = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cs.surface,
        title = {
            Text(
                text = if (isFavorite) {
                    "Remove from Favorites?"
                } else {
                    "Add to Favorites?"
                },
                fontWeight = FontWeight.Bold,
                color = cs.onSurface
            )
        },
        text = {
            Text(
                text = if (isFavorite) {
                    "Remove $parkingSpotName from your favorites?"
                } else {
                    "Add $parkingSpotName to your favorites?"
                },
                color = cs.onSurface
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = if (isFavorite) "Remove" else "Add",
                    color = cs.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = cs.onSurface)
            }
        }
    )
}