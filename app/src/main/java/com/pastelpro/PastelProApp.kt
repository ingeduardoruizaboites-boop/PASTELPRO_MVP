package com.pastelpro

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pastelpro.ui.navigation.Routes
import com.pastelpro.ui.navigation.bottomNavItems
import com.pastelpro.ui.screens.HomeScreen
import com.pastelpro.ui.screens.MoreScreen
import com.pastelpro.ui.screens.OrdersScreen
import com.pastelpro.ui.screens.RecipesScreen
import com.pastelpro.ui.screens.SetupScreen
import com.pastelpro.ui.screens.ShoppingScreen
import com.pastelpro.ui.screens.SplashScreen
import com.pastelpro.ui.screens.WelcomeScreen
import com.pastelpro.ui.theme.Berry
import com.pastelpro.ui.theme.Cocoa
import com.pastelpro.ui.theme.Cream
import com.pastelpro.ui.theme.Neutral700

@Composable
fun PastelProApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(Routes.WELCOME) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onContinue = { navController.navigate(Routes.SETUP) },
                onHaveAccount = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SETUP) {
            SetupScreen(onFinished = {
                navController.navigate(Routes.MAIN) {
                    popUpTo(Routes.SETUP) { inclusive = true }
                }
            })
        }

        composable(Routes.MAIN) {
            MainScaffold()
        }
    }
}

@Composable
private fun MainScaffold() {
    val innerNav = rememberNavController()
    val backStack by innerNav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Routes.HOME

    Scaffold(
        containerColor = Cream,
        bottomBar = {
            NavigationBar(containerColor = Cream) {
                bottomNavItems.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            innerNav.navigate(item.route) {
                                popUpTo(innerNav.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(text = stringResource(item.labelRes))
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Cocoa,
                            selectedTextColor = Cocoa,
                            unselectedIconColor = Neutral700,
                            unselectedTextColor = Neutral700,
                            indicatorColor = Berry.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = innerNav,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.HOME) { HomeScreen() }
            composable(Routes.RECIPES) { RecipesScreen() }
            composable(Routes.ORDERS) { OrdersScreen() }
            composable(Routes.SHOPPING) { ShoppingScreen() }
            composable(Routes.MORE) { MoreScreen() }
        }
    }
}
