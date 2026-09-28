package com.nextlayer3d.app.ui.shop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
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
import kotlinx.coroutines.launch

@Composable
fun ProductListScreen(onProductClick: (String) -> Unit) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun load() {
        scope.launch {
            isLoading = true
            runCatching { ProductService.listProducts() }
                .onSuccess { products = it; errorMessage = null }
                .onFailure { errorMessage = it.message ?: "Failed to load products" }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { load() }

    Scaffold(topBar = { TopAppBar(title = { Text("Pre-Built") }) }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Column(
                    Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(Icons.Filled.WifiOff, contentDescription = null)
                    Spacer(Modifier.height(8.dp))
                    Text(errorMessage ?: "")
                }
                else -> LazyColumn {
                    items(products, key = { it.id }) { product ->
                        ProductRow(product = product, onClick = { onProductClick(product.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductRow(product: Product, onClick: () -> Unit) {
    var imageUrl by remember(product.id) { mutableStateOf<String?>(null) }
    LaunchedEffect(product.id) { imageUrl = ImageResolver.resolve(product.image) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (imageUrl != null) {
                AsyncImage(model = imageUrl, contentDescription = product.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Text(product.icon ?: "📦", style = MaterialTheme.typography.headlineMedium)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(product.name, style = MaterialTheme.typography.titleMedium)
            Text(product.formattedPrice, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (product.isOutOfStock) {
                Text("Out of Stock", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
