package com.example.motorcycleparking.app

import android.R
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
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.motorcycleparking.core.core_navigation.NavItem
import com.example.motorcycleparking.core.core_navigation.Routes
import com.example.motorcycleparking.features.loginscreen.LoginScreen
import com.example.motorcycleparking.features.mapscreen.MapScreen
import com.example.motorcycleparking.features.screen2.Screen2
import com.example.motorcycleparking.features.screen3.Screen3

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val navItemList = listOf(
        NavItem("Map",      Routes.MAP,     Icons.Default.Home),
        NavItem("Screen 2", Routes.SCREEN2, Icons.Default.Favorite),
        NavItem("Screen 3", Routes.SCREEN3, Icons.Default.Settings)
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
            composable(Routes.MAP)     { MapScreen(Modifier, navController) }
            composable(Routes.SCREEN2) { Screen2(  Modifier, navController) }
            composable(Routes.SCREEN3) { Screen3(  Modifier, navController) }
            composable(Routes.LOGIN)   { LoginScreen(Modifier, navController) }
        }
    }
}


private fun NavDestination?.isOnRoute(route: String): Boolean {
    if (this == null) return false
    return hierarchy.any { it.route == route }
}

