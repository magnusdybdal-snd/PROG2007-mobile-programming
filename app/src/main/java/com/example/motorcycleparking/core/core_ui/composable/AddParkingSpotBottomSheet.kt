package com.example.motorcycleparking.core.core_ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Modal bottom sheet for adding a new parking spot with form validation.
 *
 * Validation: Name and address are required fields. Save button is enabled only when
 * both fields contain non-blank input. All text fields are trimmed before saving.
 *
 * Animation: Sheet hide animation is triggered before invoking callbacks for smooth UX.
 *
 * @param onDismiss Invoked when dismissed via cancel, outside tap, or after save
 * @param onSave Invoked on valid save with trimmed values: name, note, address, isFree
 * @param latitude Parking spot latitude (displayed as read-only, formatted to 4 decimals)
 * @param longitude Parking spot longitude (displayed as read-only, formatted to 4 decimals)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddParkingSpotBottomSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, note: String, address: String, isFree: Boolean) -> Unit,
    latitude: Double,
    longitude: Double
) {
    val cs = MaterialTheme.colorScheme
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isFree by remember { mutableStateOf(true) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Add Parking Spot",
                style = MaterialTheme.typography.headlineSmall,
                color = cs.onSurface
            )

            Text(
                text = "Location: ${String.format("%.4f", latitude)}, ${String.format("%.4f", longitude)}",
                style = MaterialTheme.typography.bodySmall,
                color = cs.onSurfaceVariant
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Notes (optional)") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isFree,
                    onCheckedChange = { isFree = it }
                )
                Text(
                    text = "Free parking",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp),
                    color = cs.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (name.isNotBlank() && address.isNotBlank()) {
                            scope.launch {
                                sheetState.hide()
                                onSave(name.trim(), note.trim(), address.trim(), isFree)
                                onDismiss()
                            }
                        }
                    },
                    enabled = name.isNotBlank() && address.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }
        }
    }
}
