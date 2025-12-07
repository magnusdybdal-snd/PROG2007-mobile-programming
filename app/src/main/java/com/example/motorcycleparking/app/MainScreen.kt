package com.example.motorcycleparking.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.motorcycleparking.core.core_navigation.NavItem
import com.example.motorcycleparking.core.core_navigation.Routes
import com.example.motorcycleparking.core.core_ui.AppNavBar
import com.example.motorcycleparking.features.favoiritescreen.FavoriteScreen
import com.example.motorcycleparking.features.loginscreen.LoginScreen
import com.example.motorcycleparking.features.mapscreen.MapScreen
import com.example.motorcycleparking.features.registerscreen.RegisterScreen
import com.example.motorcycleparking.features.settingsscreen.SettingsScreen
import com.example.motorcycleparking.features.settingsscreen.SettingsViewModel

/**
 * Main navigation container for the motorcycle parking application.
 *
 * Manages the primary navigation flow between Map, Favorites, and Settings screens,
 * as well as authentication screens (Login/Register). The bottom navigation bar is
 * conditionally displayed based on the current route, hiding it for authentication
 * screens.
 *
 * Navigation behavior:
 * - Uses single-top launch mode to prevent duplicate destinations
 * - Preserves and restores navigation state on tab switches
 * - Clears back stack when navigating to login from settings (sign out)
 * - Start destination is the login screen
 *
 * @param modifier Modifier to be applied to the navigation host
 * @param navController Navigation controller managing the navigation stack and transitions
 * @param settingsViewModel Hilt-injected view model shared between MainScreen and MapScreen
 *   for settings state management
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val navItemList = listOf(
        NavItem("Map",      Routes.MAP,     Icons.Default.Home),
        NavItem("Favorites", Routes.FAVORITES, Icons.Default.Favorite),
        NavItem("Settings", Routes.SETTINGS, Icons.Default.Settings)
    )


    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // Show or hide the bottombar.
    val showBottomBar = navItemList.any { item ->
        currentDestination.isOnRoute(item.route)
    }


    Scaffold(
        bottomBar = {
            val cs = MaterialTheme.colorScheme
            if (showBottomBar) {
                NavigationBar(
                    containerColor = cs.surface,
                    contentColor = cs.onSurface
                ) {
                    navItemList.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination.isOnRoute(item.route),
                            onClick = {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = AppNavBar.itemColors()
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Routes.MAP)        { MapScreen(
                Modifier,
                navController,
                settingsViewModel =  settingsViewModel)
            }
            composable(Routes.FAVORITES)  { FavoriteScreen(Modifier, navController) }
            composable(Routes.SETTINGS)   { SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = { // Clears entire back stack with `popUpTo(0)`
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true }  }
                },
                viewModel = settingsViewModel,
                )
            }
            composable(Routes.LOGIN)      { LoginScreen(Modifier, navController) }
            composable(Routes.REGISTER)   { RegisterScreen(Modifier, navController) }
        }
    }
}

/**
 * Checks if this destination or any of its parent destinations matches the given route.
 *
 * Traverses the destination hierarchy to determine route matching, useful for
 * highlighting the correct navigation item when nested navigation is involved.
 *
 * @param route The route string to match against the destination hierarchy
 * @return true if this destination or any parent matches the route, false otherwise
 */
private fun NavDestination?.isOnRoute(route: String): Boolean {
    if (this == null) return false
    return hierarchy.any { it.route == route }
}

