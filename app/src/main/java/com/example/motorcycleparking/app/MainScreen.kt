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
import com.example.motorcycleparking.features.favoiritescreen.FavoriteScreen
import com.example.motorcycleparking.features.loginscreen.LoginScreen
import com.example.motorcycleparking.features.mapscreen.MapScreen
import com.example.motorcycleparking.features.registerscreen.RegisterScreen
import com.example.motorcycleparking.features.settingsscreen.SettingsScreen
import com.example.motorcycleparking.features.settingsscreen.SettingsViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val navItemList = listOf(
        NavItem("Map",      Routes.MAP,     Icons.Default.Home),
        NavItem("Screen 2", Routes.FAVORITES, Icons.Default.Favorite),
        NavItem("Screen 3", Routes.SETTINGS, Icons.Default.Settings)
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
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.MAP,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Routes.MAP)        { MapScreen(
                Modifier,
                navController,
                settingsViewModel =  settingsViewModel)
            }
            composable(Routes.FAVORITES)  { FavoriteScreen(Modifier, navController, cs = MaterialTheme.colorScheme ) }
            composable(Routes.SETTINGS)   { SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    // Add your sign-out logic here
                    // For example:
                    // - Clear user session
                    // - Sign out from Firebase Auth
                    // - Navigate to login screen
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }  // Clears entire back stack.
                    }
                },
                viewModel = settingsViewModel,
                )
            }
            composable(Routes.LOGIN)      { LoginScreen(Modifier, navController) }
            composable(Routes.REGISTER)   { RegisterScreen(Modifier, navController) }
        }
    }
}

private fun NavDestination?.isOnRoute(route: String): Boolean {
    if (this == null) return false
    return hierarchy.any { it.route == route }
}

