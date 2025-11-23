package com.example.motorcycleparking.core.core_ui.composable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun NavigationConfirmationDialog(
    parkingSpotName: String? = "this parking spot",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Start Navigation?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text ="Navigate to $parkingSpotName?"
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Navigate")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}