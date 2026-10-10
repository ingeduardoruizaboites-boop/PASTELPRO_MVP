package com.pastelpro.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.ui.graphics.vector.ImageVector

object Routes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val SETUP = "setup"
    const val MAIN = "main"
    const val HOME = "home"
    const val RECIPES = "recipes"
    const val ORDERS = "orders"
    const val SHOPPING = "shopping"
    const val MORE = "more"
    const val INGREDIENTES = "ingredientes"

    // Ruta con argumento: /receta/{id}
    const val RECETA_DETALLE_BASE = "receta"
    const val RECETA_DETALLE_ARG = "id"
    const val RECETA_DETALLE = "$RECETA_DETALLE_BASE/{$RECETA_DETALLE_ARG}"
    fun recetaDetalle(id: String) = "$RECETA_DETALLE_BASE/$id"

    // Ruta: /receta/{id}/costo-precio
    const val COSTO_PRECIO_BASE = "costo-precio"
    const val COSTO_PRECIO = "$RECETA_DETALLE/$COSTO_PRECIO_BASE"
    fun costoPrecio(id: String) = "$RECETA_DETALLE_BASE/$id/$COSTO_PRECIO_BASE"

    // Wizard de nuevo pedido
    const val NUEVO_PEDIDO = "nuevo-pedido"
    const val NOTIFICACIONES = "notificaciones"
    const val COSTOS_AVANZADOS = "costos-avanzados"
}

data class BottomNavItem(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, com.pastelpro.R.string.tab_home, Icons.Filled.Home),
    BottomNavItem(Routes.RECIPES, com.pastelpro.R.string.tab_recipes, Icons.AutoMirrored.Filled.MenuBook),
    BottomNavItem(Routes.ORDERS, com.pastelpro.R.string.tab_orders, Icons.Filled.Receipt),
    BottomNavItem(Routes.SHOPPING, com.pastelpro.R.string.tab_shopping, Icons.Filled.ShoppingCart),
    BottomNavItem(Routes.MORE, com.pastelpro.R.string.tab_more, Icons.Filled.MoreHoriz)
)
