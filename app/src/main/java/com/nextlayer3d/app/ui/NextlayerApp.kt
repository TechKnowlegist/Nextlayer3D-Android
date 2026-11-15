package com.nextlayer3d.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
import com.nextlayer3d.app.ui.account.AuthMode
import com.nextlayer3d.app.ui.account.AuthScreen
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
import com.nextlayer3d.app.ui.theme.NlGreen
import com.nextlayer3d.app.ui.theme.NlSky

private data class TopLevelRoute(val route: String, val label: String, val icon: ImageVector)

// Left to right: Home, Feed (next to Home, same as the website's "Build
// Plate" gallery sitting close to Shop), Hub (the middle tab), Cart,
// LayerAI. Orders isn't its own tab — it's reachable from Hub instead,
// folded in along with everything else account-related.
private val topLevelRoutes = listOf(
    TopLevelRoute("home", "Home", Icons.Filled.Home),
    TopLevelRoute("feed", "Feed", Icons.Filled.DynamicFeed),
    TopLevelRoute("hub", "Hub", Icons.Filled.Hub),
    TopLevelRoute("cart", "Cart", Icons.Filled.ShoppingCart),
    TopLevelRoute("layerai", "LayerAI", Icons.Filled.AutoAwesome),
)

@Composable
fun NextlayerApp() {
    NextlayerTheme {
        val session: SessionViewModel = viewModel()

        when {
            session.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            // "Continue as Guest" on the welcome screen sets isGuest and
            // skips straight to MainScaffold without an account — Hub
            // shows a sign-in prompt instead of real account data in that
            // case (see AccountScreen's GuestAccount branch).
            !session.isSignedIn && !session.isGuest -> AuthGateScreen(
                session = session,
                onContinueAsGuest = { session.continueAsGuest() },
            )
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
                        // M3's default selected-tab indicator falls back to
                        // secondaryContainer, which reads as generic pastel
                        // blue — this paints the brand's own green->sky
                        // gradient pill behind the icon instead.
                        icon = {
                            Box(
                                modifier = (
                                    if (selected) {
                                        Modifier
                                            .width(56.dp)
                                            .height(32.dp)
                                            .background(
                                                brush = Brush.horizontalGradient(listOf(NlGreen, NlSky)),
                                                shape = RoundedCornerShape(16.dp),
                                            )
                                    } else {
                                        Modifier
                                    }
                                ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                        label = {
                            Text(item.label, color = if (selected) NlGreen else MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent),
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
                ProductDetailScreen(
                    productId = productId,
                    cart = cart,
                    onBack = { navController.popBackStack() },
                )
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
                    onSignIn = { navController.navigate("signIn") },
                    onSignUp = { navController.navigate("signUp") },
                )
            }
            // Not a bottom tab — reached from the "My Orders" row inside Hub.
            composable("orders") {
                OrdersScreen(session = session, onBack = { navController.popBackStack() })
            }
            composable("addAddress") {
                AddressEditScreen(onSaved = { navController.popBackStack() })
            }
            // Reached only from a guest's Hub tab (GuestAccount's Sign In /
            // Create Account buttons) — signing in here flips
            // session.isSignedIn, which Hub picks up automatically since it
            // reads that state directly rather than a one-time nav arg.
            composable("signIn") {
                AuthScreen(session = session, initialMode = AuthMode.SIGN_IN, onBack = { navController.popBackStack() })
            }
            composable("signUp") {
                AuthScreen(session = session, initialMode = AuthMode.SIGN_UP, onBack = { navController.popBackStack() })
            }
        }
    }
}
