package com.example.motorcycleparking.core.core_ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
        primary = AppColor.teal,
        onPrimary = AppColor.white,
        secondary = AppColor.darkGrey,
        onSecondary = AppColor.white,
        tertiary = AppColor.fadedTeal,
        onTertiary = AppColor.black,
        background = AppColor.white,
        onBackground = AppColor.black,
        surface = AppColor.lightGrey,
        onSurface = AppColor.black,
        onSurfaceVariant = AppColor.darkGrey,
        secondaryContainer = AppColor.lightTeal,
        onSecondaryContainer = AppColor.black,
        outline = AppColor.darkGrey
    )

// Only one theme in app.
private val LightColorScheme = DarkColorScheme

@Composable
fun MotorcycleParkingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Colors used in styling of NavigationBarItem.
 * @return NavigationBarIdemDefault with colors from appropriate theme.
 */
object AppNavBar {
    @Composable
    fun itemColors(): NavigationBarItemColors {
        val cs = MaterialTheme.colorScheme      // "cs" is short for "ColorScheme"
        return NavigationBarItemDefaults.colors(
            selectedIconColor = cs.onSecondaryContainer,
            selectedTextColor = cs.onSurfaceVariant,
            indicatorColor = cs.background,
            unselectedIconColor = cs.onSurfaceVariant,
            unselectedTextColor = cs.onSurfaceVariant,
            disabledIconColor = cs.onSurface.copy(alpha = 0.38f),
            disabledTextColor = cs.onSurface.copy(alpha = 0.38f)
        )
    }
}

object AppSearchBar {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun colors(): SearchBarColors {
        val cs = MaterialTheme.colorScheme
        SearchBarDefaults.inputFieldColors(
            focusedTextColor = cs.onSurfaceVariant, // white
            unfocusedTextColor = cs.onSurfaceVariant,
            focusedLeadingIconColor = cs.onSecondaryContainer, // white
            unfocusedLeadingIconColor = cs.onSecondaryContainer,
            focusedTrailingIconColor = cs.onSecondaryContainer,
            unfocusedTrailingIconColor = cs.onSecondaryContainer,
            cursorColor = cs.tertiary,              // lightTeal
            focusedPlaceholderColor = cs.outline,   // lightGrey
            unfocusedPlaceholderColor = cs.onSurface.copy(alpha = 0.6f)
        )
        return SearchBarDefaults.colors(
            containerColor = cs.surface,                 // teal
            dividerColor = cs.outline                  // lightGrey
        )
    }
}