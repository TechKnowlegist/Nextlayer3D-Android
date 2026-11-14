package com.nextlayer3d.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.nextlayer3d.app.ui.feed.FeedScreen
import com.nextlayer3d.app.ui.layerai.LayerAiScreen
import com.nextlayer3d.app.ui.onboarding.AuthGateScreen
import com.nextlayer3d.app.ui.orders.OrdersScreen
import com.nextlayer3d.app.ui.shop.ProductDetailScreen
import com.nextlayer3d.app.ui.shop.ProductListScreen
import com.nextlayer3d.app.ui.theme.NextlayerTheme

private data class TopLevelRoute(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

// Left to right: Home (Shop), Cart, Feed (the website's "Build Plate"),
// LayerAI, Hub (Account + order history + settings-to-come). Orders isn't
// its own tab anymore — it's reachable from Hub instead, folded in along
// with everything else account-related.
private val topLevelRoutes = listOf(
    TopLevelRoute("home", "Home", Icons.Filled.Home),
    TopLevelRoute("cart", "Cart", Icons.Filled.ShoppingCart),
    TopLevelRoute("feed", "Feed", Icons.Filled.DynamicFeed),
    TopLevelRoute("layerai", "LayerAI", Icons.Filled.AutoAwesome),
    TopLevelRoute("hub", "Hub", Icons.Filled.Hub),
)

@Composable
fun NextlayerApp() {
    NextlayerTheme {
        val session: SessionViewModel = viewModel()

        when {
            session.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            // Everything below session.isSignedIn being true — the whole
            // tabbed app is gated behind it, and AuthGateScreen (Welcome +
            // AuthScreen) is the only thing a signed-out user can see.
            // Signing out from AccountScreen flips this back automatically,
            // no navigation bookkeeping needed for that transition.
            !session.isSignedIn -> AuthGateScreen(session = session)
            else -> MainScaffold(session = session)
        }
    }
}

@Composable
private fun MainScaffold(session: SessionViewModel) {
    val navController = rememberNavController()
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
            startDestination = "home",
            modifier = Modifier.padding(padding),
        ) {
            composable("home") {
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
            composable("feed") {
                FeedScreen()
            }
            composable("layerai") {
                LayerAiScreen()
            }
            composable("hub") {
                AccountScreen(
                    session = session,
                    onAddAddress = { navController.navigate("addAddress") },
                    onViewOrders = { navController.navigate("orders") },
                )
            }
            // Not a bottom tab — reached from the "My Orders" row inside Hub.
            composable("orders") {
                OrdersScreen(session = session, onBack = { navController.popBackStack() })
            }
            composable("addAddress") {
                AddressEditScreen(onSaved = { navController.popBackStack() })
            }
        }
    }
}
