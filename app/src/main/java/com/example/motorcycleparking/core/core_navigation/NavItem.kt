package com.example.motorcycleparking.core.core_navigation
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data used in navigation-bar
 * @see com.example.motorcycleparking.app.MainActivity
 */
data class NavItem(
    val label : String,
    val route : String,
    val icon : ImageVector
)
