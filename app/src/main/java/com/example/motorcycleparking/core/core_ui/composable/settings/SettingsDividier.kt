package com.example.motorcycleparking.core.core_ui.composable.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A horizontal divider component for visually separating settings sections or items.
 *
 * This composable provides consistent spacing and styling for dividers within settings UI
 * for thickness and color.
 */
@Composable
fun SettingsDivider(){
    HorizontalDivider(
    modifier = Modifier.padding(vertical = 8.dp),
    thickness = DividerDefaults.Thickness,
    color = DividerDefaults.color
)}