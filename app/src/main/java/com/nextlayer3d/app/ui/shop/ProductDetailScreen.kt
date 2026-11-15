package com.nextlayer3d.app.ui.shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nextlayer3d.app.data.ImageResolver
import com.nextlayer3d.app.data.ProductService
import com.nextlayer3d.app.models.Product
import com.nextlayer3d.app.state.CartViewModel
import com.nextlayer3d.app.ui.theme.GradientButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Used to be `val current = product ?: return` with no loading/error
 * state at all — if the fetch was slow or failed outright, the screen
 * rendered nothing: no Scaffold, no back button, no message. Tapping a
 * product looked like a dead click. Now it always renders a Scaffold
 * with a back button, and shows a spinner or a real error instead of
 * silently staying blank. */
@Composable
fun ProductDetailScreen(productId: String, cart: CartViewModel, onBack: () -> Unit) {
    var product by remember(productId) { mutableStateOf<Product?>(null) }
    var imageUrl by remember(productId) { mutableStateOf<String?>(null) }
    var isLoading by remember(productId) { mutableStateOf(true) }
    var errorMessage by remember(productId) { mutableStateOf<String?>(null) }
    var didAdd by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            isLoading = true
            errorMessage = null
            runCatching { ProductService.getProduct(productId) }
                .onSuccess { result ->
                    product = result
                    if (result == null) errorMessage = "This product couldn't be found."
                }
                .onFailure { errorMessage = it.message ?: "Couldn't load this product" }
            imageUrl = ImageResolver.resolve(product?.image)
            isLoading = false
        }
    }

    LaunchedEffect(productId) { load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            val current = product
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Column(
                    Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(Icons.Filled.WifiOff, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(errorMessage ?: "", textAlign = TextAlign.Center)
                }
                current == null -> {}
                else -> Column(Modifier.padding(16.dp)) {
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
                        GradientButton(
                            onClick = {
                                cart.add(current)
                                didAdd = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(if (didAdd) "Added ✓" else "Add to Cart", style = MaterialTheme.typography.titleMedium)
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
    }
}
