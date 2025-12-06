package com.example.motorcycleparking.core.core_ui

import androidx.compose.ui.graphics.Color

/**
 * Application color palette for light and dark themes.
 */
object AppColor {
    /**
     * Color definitions for light theme using IBM Blue as primary brand color.
     */
    object Light {
        // Primary - IBM Blue (main brand color)
        val primary = Color(0xFF0F62FE)           // color-blue-600
        val onPrimary = Color(0xFFFFFFFF)         // White text on primary
        val primaryContainer = Color(0xFFD0E2FF)  // color-blue-100
        val onPrimaryContainer = Color(0xFF001D6C) // color-blue-900

        // Secondary - Lighter blue for variety
        val secondary = Color(0xFF4589FF)         // color-blue-500
        val onSecondary = Color(0xFFFFFFFF)       // White text
        val secondaryContainer = Color(0xFFEDF5FF) // color-blue-50
        val onSecondaryContainer = Color(0xFF002D9C) // color-blue-800

        // Tertiary - Accent blue shade
        val tertiary = Color(0xFF78A9FF)          // color-blue-400
        val onTertiary = Color(0xFF001141)        // color-blue-950
        val tertiaryContainer = Color(0xFFBBD5FF) // color-blue-200
        val onTertiaryContainer = Color(0xFF001D6C) // color-blue-900

        // Error colors - Red palette for errors
        val error = Color(0xFFDA1E28)             // color-red-600
        val onError = Color(0xFFFFFFFF)           // White text
        val errorContainer = Color(0xFFFFD7D9)    // color-red-100
        val onErrorContainer = Color(0xFF2D0709)  // color-red-950

        // Background & Surface - Gray palette
        val background = Color(0xFFF4F4F4)        // color-gray-50
        val onBackground = Color(0xFF161616)      // color-gray-950
        val surface = Color(0xFFFFFFFF)           // Pure white surface
        val onSurface = Color(0xFF161616)         // color-gray-950
        val surfaceVariant = Color(0xFFE0E0E0)    // color-gray-100
        val onSurfaceVariant = Color(0xFF525252)  // color-gray-700

        // Outline & other
        val outline = Color(0xFF8D8D8D)           // color-gray-500
        val outlineVariant = Color(0xFFC6C6C6)    // color-gray-300
        val scrim = Color(0xFF000000)             // Black overlay
        val inverseSurface = Color(0xFF262626)    // color-gray-900
        val inverseOnSurface = Color(0xFFF4F4F4)  // color-gray-50
        val inversePrimary = Color(0xFF78A9FF)    // color-blue-400
    }

    /**
     * Color definitions for dark theme using IBM Blue as primary brand color.
     */
    object Dark {
        // Primary - IBM Blue (main brand color)
        val primary = Color(0xFF78A9FF)           // color-blue-400
        val onPrimary = Color(0xFF001D6C)         // color-blue-900
        val primaryContainer = Color(0xFF0143CE)  // color-blue-700
        val onPrimaryContainer = Color(0xFFD0E2FF) // color-blue-100

        // Secondary - Lighter blue for variety
        val secondary = Color(0xFFA6C8FF)         // color-blue-300
        val onSecondary = Color(0xFF002D9C)       // color-blue-800
        val secondaryContainer = Color(0xFF002D9C) // color-blue-800
        val onSecondaryContainer = Color(0xFFD0E2FF) // color-blue-100

        // Tertiary - Accent blue shade
        val tertiary = Color(0xFFBBD5FF)          // color-blue-200
        val onTertiary = Color(0xFF001D6C)        // color-blue-900
        val tertiaryContainer = Color(0xFF0F62FE) // color-blue-600
        val onTertiaryContainer = Color(0xFFEDF5FF) // color-blue-50

        // Error colors - Red palette for errors
        val error = Color(0xFFFF8389)             // color-red-400
        val onError = Color(0xFF520408)           // color-red-900
        val errorContainer = Color(0xFFA2191F)    // color-red-300
        val onErrorContainer = Color(0xFFFFD7D9)  // color-red-100

        // Background & Surface - Gray palette
        val background = Color(0xFF161616)        // color-gray-950
        val onBackground = Color(0xFFF4F4F4)      // color-gray-50
        val surface = Color(0xFF393939)           // color-gray-900
        val onSurface = Color(0xFFE0E0E0)         // color-gray-100
        val surfaceVariant = Color(0xFF393939)    // color-gray-800
        val onSurfaceVariant = Color(0xFFC6C6C6)  // color-gray-300

        // Outline & other
        val outline = Color(0xFF6F6F6F)           // color-gray-600
        val outlineVariant = Color(0xFF525252)    // color-gray-700
        val scrim = Color(0xFF000000)             // Black overlay
        val inverseSurface = Color(0xFFE0E0E0)    // color-gray-100
        val inverseOnSurface = Color(0xFF161616)  // color-gray-950
        val inversePrimary = Color(0xFF0F62FE)    // color-blue-600
    }
}
