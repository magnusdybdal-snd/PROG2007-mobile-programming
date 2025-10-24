package com.example.motorcycleparking.core.core_ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AppColor.black,
    onPrimary = AppColor.white,
    secondary = AppColor.lightGrey,
    onSecondary = AppColor.darkGrey,
    tertiary = AppColor.lightTeal,
    onTertiary = AppColor.white,
    background = AppColor.black,
    onBackground = AppColor.white,
    surface = AppColor.teal,
    onSurface = AppColor.black,
    onSurfaceVariant = AppColor.white,
    secondaryContainer = AppColor.fadedTeal,
    onSecondaryContainer = AppColor.white,
    outline = AppColor.lightGrey
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
