package com.example.motorcycleparking.core.core_ui.composable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

/**
 * Confirmation dialog for initiating navigation to a parking spot.
 *
 * @param parkingSpotName Name displayed in the confirmation message
 * @param onConfirm Invoked when user confirms navigation
 * @param onDismiss Invoked when user cancels or dismisses the dialog
 */
@Composable
fun NavigationConfirmationDialog(
    parkingSpotName: String? = "this parking spot",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cs.surface,
        title = {
            Text(
                text = "Start Navigation?",
                fontWeight = FontWeight.Bold,
                color = cs.onSurface
            )
        },
        text = {
            Text(
                text = "Navigate to $parkingSpotName?",
                color = cs.onSurface
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Navigate", color = cs.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = cs.onSurface)
            }
        }
    )
}