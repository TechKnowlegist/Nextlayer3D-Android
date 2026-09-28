package com.nextlayer3d.app.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nextlayer3d.app.state.CartViewModel

@Composable
fun CartScreen(cart: CartViewModel, onCheckout: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Cart") }) }) { padding ->
        if (cart.items.isEmpty()) {
            Column(
                Modifier.padding(padding).fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                Spacer(Modifier.height(8.dp))
                Text("Your cart is empty")
                Text("Browse Pre-Built to add something.", style = MaterialTheme.typography.bodySmall)
            }
        } else {
            Column(Modifier.padding(padding).fillMaxSize()) {
                LazyColumn(Modifier.weight(1f)) {
                    items(cart.items, key = { it.productId }) { item ->
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.titleMedium)
                                Text("$" + "%.2f".format(item.price) + " × ${item.quantity}", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { cart.updateQuantity(item.productId, item.quantity - 1) }) {
                                Icon(Icons.Filled.Remove, contentDescription = "Decrease")
                            }
                            Text("${item.quantity}")
                            IconButton(onClick = { cart.updateQuantity(item.productId, item.quantity + 1) }) {
                                Icon(Icons.Filled.Add, contentDescription = "Increase")
                            }
                            IconButton(onClick = { cart.remove(item.productId) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Remove")
                            }
                        }
                        HorizontalDivider()
                    }
                }
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", style = MaterialTheme.typography.titleMedium)
                        Text("$" + "%.2f".format(cart.total), style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) {
                        Text("Checkout")
                    }
                }
            }
        }
    }
}
