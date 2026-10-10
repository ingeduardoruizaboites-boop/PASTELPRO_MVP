package com.pastelpro

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pastelpro.ui.navigation.Routes
import com.pastelpro.ui.navigation.bottomNavItems
import com.pastelpro.ui.screens.HomeScreen
import com.pastelpro.ui.screens.MoreScreen
import com.pastelpro.ui.screens.OrdersScreen
import com.pastelpro.ui.screens.SetupScreen
import com.pastelpro.ui.screens.ShoppingScreen
import com.pastelpro.ui.screens.SplashScreen
import com.pastelpro.ui.screens.WelcomeScreen
import com.pastelpro.ui.screens.ingredientes.IngredientesScreen
import com.pastelpro.ui.screens.mas.CostosAvanzadosScreen
import com.pastelpro.ui.screens.mas.CostosAvanzadosViewModel
import com.pastelpro.ui.screens.mas.MasScreen
import com.pastelpro.ui.screens.mas.NotificacionesScreen
import com.pastelpro.ui.screens.mas.NotificacionesViewModel
import com.pastelpro.ui.screens.pedidos.NuevoPedidoScreen
import com.pastelpro.ui.screens.pedidos.PedidosScreen
import com.pastelpro.ui.screens.recetas.CostoPrecioScreen
import com.pastelpro.ui.screens.recetas.RecetaDetalleScreen
import com.pastelpro.ui.screens.recetas.RecetasScreen
import com.pastelpro.ui.theme.Berry
import com.pastelpro.ui.theme.Cocoa
import com.pastelpro.ui.theme.Cream
import com.pastelpro.ui.theme.Neutral700

@Composable
fun PastelProApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth / 4 },
                animationSpec = tween(280, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(280))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = tween(280, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(200))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = tween(280, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(280))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth / 4 },
                animationSpec = tween(280, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(200))
        }
    ) {
        composable(
            route = Routes.SPLASH,
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
            SplashScreen(onFinished = {
                navController.navigate(Routes.WELCOME) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(
            route = Routes.WELCOME,
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }
        ) {
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
            MainScaffold(
                onIrIngredientes = { navController.navigate(Routes.INGREDIENTES) },
                onIrRecetaDetalle = { id -> navController.navigate(Routes.recetaDetalle(id)) },
                onIrNuevoPedido = { navController.navigate(Routes.NUEVO_PEDIDO) },
                onIrNotificaciones = { navController.navigate(Routes.NOTIFICACIONES) },
                onIrCostosAvanzados = { navController.navigate(Routes.COSTOS_AVANZADOS) }
            )
        }

        composable(Routes.INGREDIENTES) {
            IngredientesScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.RECETA_DETALLE,
            arguments = listOf(navArgument(Routes.RECETA_DETALLE_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString(Routes.RECETA_DETALLE_ARG).orEmpty()
            RecetaDetalleScreen(
                recetaId = id,
                onBack = { navController.popBackStack() },
                onVerCostoPrecio = { navController.navigate(Routes.costoPrecio(id)) }
            )
        }

        composable(
            route = Routes.COSTO_PRECIO,
            arguments = listOf(navArgument(Routes.RECETA_DETALLE_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString(Routes.RECETA_DETALLE_ARG).orEmpty()
            CostoPrecioScreen(
                recetaId = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.COSTOS_AVANZADOS) {
            CostosAvanzadosScreen(
                onBack = { navController.popBackStack() },
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = CostosAvanzadosViewModel.Factory
                )
            )
        }

        composable(Routes.NOTIFICACIONES) {
            val app = LocalContext.current.applicationContext as android.app.Application
            NotificacionesScreen(
                onBack = { navController.popBackStack() },
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                    factory = NotificacionesViewModel.factory(app)
                )
            )
        }

        composable(Routes.NUEVO_PEDIDO) {
            NuevoPedidoScreen(
                onBack = { navController.popBackStack() },
                onGuardado = {
                    // Vuelve a Main y navega al tab Pedidos
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun MainScaffold(
    onIrIngredientes: () -> Unit,
    onIrRecetaDetalle: (String) -> Unit,
    onIrNuevoPedido: () -> Unit,
    onIrNotificaciones: () -> Unit,
    onIrCostosAvanzados: () -> Unit
) {
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
            composable(Routes.HOME) {
                HomeScreen(
                    onNuevoPastel = onIrNuevoPedido,
                    onVerRecetas = { innerNav.navigate(Routes.RECIPES) },
                    onVerIngredientes = onIrIngredientes
                )
            }
            composable(Routes.RECIPES) {
                RecetasScreen(onRecetaClick = onIrRecetaDetalle)
            }
            composable(Routes.ORDERS) {
                PedidosScreen(onNuevoPedido = onIrNuevoPedido)
            }
            composable(Routes.SHOPPING) { ShoppingScreen() }
            composable(Routes.MORE) {
                MasScreen(
                    onIrNotificaciones = onIrNotificaciones,
                    onIrCostosAvanzados = onIrCostosAvanzados
                )
            }
        }
    }
}
