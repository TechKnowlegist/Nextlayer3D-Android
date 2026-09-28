package com.nextlayer3d.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nextlayer3d.app.state.CartViewModel
import com.nextlayer3d.app.state.SessionViewModel
import com.nextlayer3d.app.ui.account.AccountScreen
import com.nextlayer3d.app.ui.account.AddressEditScreen
import com.nextlayer3d.app.ui.cart.CartScreen
import com.nextlayer3d.app.ui.checkout.CheckoutScreen
import com.nextlayer3d.app.ui.checkout.OrderConfirmationScreen
import com.nextlayer3d.app.ui.shop.ProductDetailScreen
import com.nextlayer3d.app.ui.shop.ProductListScreen
import com.nextlayer3d.app.ui.track.TrackOrderScreen

private data class TopLevelRoute(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val topLevelRoutes = listOf(
    TopLevelRoute("shop", "Shop", Icons.Filled.Widgets),
    TopLevelRoute("cart", "Cart", Icons.Filled.ShoppingCart),
    TopLevelRoute("track", "Track", Icons.Filled.LocalShipping),
    TopLevelRoute("account", "Account", Icons.Filled.AccountCircle),
)

@Composable
fun NextlayerApp() {
    MaterialTheme {
        val navController = rememberNavController()
        val session: SessionViewModel = viewModel()
        val cart: CartViewModel = viewModel()

        Scaffold(
            bottomBar = {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination
                NavigationBar {
                    topLevelRoutes.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                        )
                    }
                }
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "shop",
                modifier = Modifier.padding(padding),
            ) {
                composable("shop") {
                    ProductListScreen(onProductClick = { id -> navController.navigate("product/$id") })
                }
                composable("product/{productId}") { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
                    ProductDetailScreen(productId = productId, cart = cart)
                }
                composable("cart") {
                    CartScreen(cart = cart, onCheckout = { navController.navigate("checkout") })
                }
                composable("checkout") {
                    CheckoutScreen(
                        cart = cart,
                        session = session,
                        onOrderPlaced = { orderNumber ->
                            navController.navigate("confirmation/$orderNumber") {
                                popUpTo("cart") { inclusive = true }
                            }
                        },
                    )
                }
                composable("confirmation/{orderNumber}") { backStackEntry ->
                    val orderNumber = backStackEntry.arguments?.getString("orderNumber") ?: return@composable
                    OrderConfirmationScreen(orderNumber = orderNumber)
                }
                composable("track") {
                    TrackOrderScreen()
                }
                composable("account") {
                    AccountScreen(session = session, onAddAddress = { navController.navigate("addAddress") })
                }
                composable("addAddress") {
                    AddressEditScreen(onSaved = { navController.popBackStack() })
                }
            }
        }
    }
}
