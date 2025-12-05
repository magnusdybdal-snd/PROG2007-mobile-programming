package com.example.motorcycleparking.core.core_ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
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
            selectedIconColor = cs.surface,
            selectedTextColor = cs.onSurface,
            indicatorColor = cs.primary,
            unselectedIconColor = cs.onSurface,
            unselectedTextColor = cs.onSurface,
            disabledIconColor = cs.onSurface.copy(alpha = 0.38f),
            disabledTextColor = cs.onSurface.copy(alpha = 0.38f)
        )
    }
}

object AppSearchBar {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun inputColors() = SearchBarDefaults.inputFieldColors(
        focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        focusedTrailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        cursorColor = MaterialTheme.colorScheme.tertiary,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,   // ← BLACK
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface  // ← BLACK
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun colors() = SearchBarDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface,
        dividerColor = MaterialTheme.colorScheme.outline
    )
}

object AppCard {
    @Composable
    fun colors(): CardColors {
        val cs = MaterialTheme.colorScheme
        return CardDefaults.cardColors(
            containerColor = cs.surface,
            contentColor = cs.onSurface,
            disabledContainerColor = cs.surface.copy(alpha = 0.38f),
            disabledContentColor = cs.onSurface.copy(alpha = 0.38f)
        )
    }
}