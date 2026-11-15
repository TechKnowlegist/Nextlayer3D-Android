package com.nextlayer3d.app.ui.shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import com.nextlayer3d.app.ui.theme.GradientText
import com.nextlayer3d.app.ui.theme.NlSky
import kotlinx.coroutines.launch

// Cart is its own bottom tab again (not a top-bar badge icon here) —
// see the tab-bar rework in NextlayerApp.kt.
@Composable
fun ProductListScreen(onProductClick: (String) -> Unit) {
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var query by remember { mutableStateOf("") }
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

    // Filters the catalog already fetched above rather than hitting a
    // separate search endpoint — same real result as the website's
    // homepage search (Nextlayer.css's .nl-home-search-*), just scoped to
    // this list instead of a sitewide search-everything box.
    val filtered = remember(products, query) {
        if (query.isBlank()) {
            products
        } else {
            products.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.description?.contains(query, ignoreCase = true) == true
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { GradientText("Pre-Built") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search products…") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NlSky),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
            Box(modifier = Modifier.fillMaxSize()) {
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
                    filtered.isEmpty() -> Column(
                        Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("No products match \"$query\".")
                    }
                    else -> LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(filtered, key = { it.id }) { product ->
                            ProductRow(product = product, onClick = { onProductClick(product.id) })
                        }
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

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp)),
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
}
