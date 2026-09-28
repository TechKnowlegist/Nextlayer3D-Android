package com.nextlayer3d.app.ui.shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nextlayer3d.app.data.ImageResolver
import com.nextlayer3d.app.data.ProductService
import com.nextlayer3d.app.models.Product
import com.nextlayer3d.app.state.CartViewModel
import kotlinx.coroutines.delay

@Composable
fun ProductDetailScreen(productId: String, cart: CartViewModel) {
    var product by remember(productId) { mutableStateOf<Product?>(null) }
    var imageUrl by remember(productId) { mutableStateOf<String?>(null) }
    var didAdd by remember { mutableStateOf(false) }

    LaunchedEffect(productId) {
        product = ProductService.getProduct(productId)
        imageUrl = ImageResolver.resolve(product?.image)
    }

    val current = product ?: return

    Scaffold(topBar = { TopAppBar(title = { Text(current.name) }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (imageUrl != null) {
                    AsyncImage(model = imageUrl, contentDescription = current.name, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize())
                } else {
                    Text(current.icon ?: "📦", style = MaterialTheme.typography.displayLarge)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(current.name, style = MaterialTheme.typography.headlineSmall)
            Text(current.formattedPrice, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

            current.rating?.takeIf { it > 0 }?.let { rating ->
                Spacer(Modifier.height(4.dp))
                Text("★".repeat(rating) + "☆".repeat((5 - rating).coerceAtLeast(0)), color = MaterialTheme.colorScheme.tertiary)
            }

            current.description?.takeIf { it.isNotEmpty() }?.let {
                Spacer(Modifier.height(12.dp))
                Text(it)
            }

            Spacer(Modifier.height(20.dp))
            if (current.isOutOfStock) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(8.dp))
                    Text("Out of Stock", color = MaterialTheme.colorScheme.error)
                }
            } else {
                Button(
                    onClick = {
                        cart.add(current)
                        didAdd = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (didAdd) "Added ✓" else "Add to Cart")
                }
                if (didAdd) {
                    LaunchedEffect(didAdd) {
                        delay(1200)
                        didAdd = false
                    }
                }
            }
        }
    }
}
