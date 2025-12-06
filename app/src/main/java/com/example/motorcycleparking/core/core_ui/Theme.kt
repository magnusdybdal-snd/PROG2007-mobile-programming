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

private val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = AppColor.Light.primary,
    onPrimary = AppColor.Light.onPrimary,
    primaryContainer = AppColor.Light.primaryContainer,
    onPrimaryContainer = AppColor.Light.onPrimaryContainer,
    secondary = AppColor.Light.secondary,
    onSecondary = AppColor.Light.onSecondary,
    secondaryContainer = AppColor.Light.secondaryContainer,
    onSecondaryContainer = AppColor.Light.onSecondaryContainer,
    tertiary = AppColor.Light.tertiary,
    onTertiary = AppColor.Light.onTertiary,
    tertiaryContainer = AppColor.Light.tertiaryContainer,
    onTertiaryContainer = AppColor.Light.onTertiaryContainer,
    error = AppColor.Light.error,
    onError = AppColor.Light.onError,
    errorContainer = AppColor.Light.errorContainer,
    onErrorContainer = AppColor.Light.onErrorContainer,
    background = AppColor.Light.background,
    onBackground = AppColor.Light.onBackground,
    surface = AppColor.Light.surface,
    onSurface = AppColor.Light.onSurface,
    surfaceVariant = AppColor.Light.surfaceVariant,
    onSurfaceVariant = AppColor.Light.onSurfaceVariant,
    outline = AppColor.Light.outline,
    outlineVariant = AppColor.Light.outlineVariant,
    scrim = AppColor.Light.scrim,
    inverseSurface = AppColor.Light.inverseSurface,
    inverseOnSurface = AppColor.Light.inverseOnSurface,
    inversePrimary = AppColor.Light.inversePrimary,
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColor.Dark.primary,
    onPrimary = AppColor.Dark.onPrimary,
    primaryContainer = AppColor.Dark.primaryContainer,
    onPrimaryContainer = AppColor.Dark.onPrimaryContainer,
    secondary = AppColor.Dark.secondary,
    onSecondary = AppColor.Dark.onSecondary,
    secondaryContainer = AppColor.Dark.secondaryContainer,
    onSecondaryContainer = AppColor.Dark.onSecondaryContainer,
    tertiary = AppColor.Dark.tertiary,
    onTertiary = AppColor.Dark.onTertiary,
    tertiaryContainer = AppColor.Dark.tertiaryContainer,
    onTertiaryContainer = AppColor.Dark.onTertiaryContainer,
    error = AppColor.Dark.error,
    onError = AppColor.Dark.onError,
    errorContainer = AppColor.Dark.errorContainer,
    onErrorContainer = AppColor.Dark.onErrorContainer,
    background = AppColor.Dark.background,
    onBackground = AppColor.Dark.onBackground,
    surface = AppColor.Dark.surface,
    onSurface = AppColor.Dark.onSurface,
    surfaceVariant = AppColor.Dark.surfaceVariant,
    onSurfaceVariant = AppColor.Dark.onSurfaceVariant,
    outline = AppColor.Dark.outline,
    outlineVariant = AppColor.Dark.outlineVariant,
    scrim = AppColor.Dark.scrim,
    inverseSurface = AppColor.Dark.inverseSurface,
    inverseOnSurface = AppColor.Dark.inverseOnSurface,
    inversePrimary = AppColor.Dark.inversePrimary,
)

/**
 * Main theme composable that wraps the app content with Material3 theming.
 *
 * @param darkTheme Whether to use dark color scheme
 * @param dynamicColor Whether to use dynamic colors (Android 12+)
 * @param content The app content to be themed
 */
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
 * Provides styled NavigationBarItem colors for the app's bottom navigation.
 */
object AppNavBar {
    /**
     * Returns NavigationBarItemColors with app-specific color scheme.
     */
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

/**
 * Provides styled SearchBar colors for the app's search functionality.
 */
object AppSearchBar {

    /**
     * Returns input field colors for SearchBar text and icons.
     */
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

    /**
     * Returns container and divider colors for SearchBar.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun colors() = SearchBarDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface,
        dividerColor = MaterialTheme.colorScheme.outline
    )
}

/**
 * Provides styled Card colors for the app's card components.
 */
object AppCard {
    /**
     * Returns CardColors with app-specific color scheme for enabled and disabled states.
     */
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