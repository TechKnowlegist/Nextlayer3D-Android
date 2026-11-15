package com.nextlayer3d.app.ui.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nextlayer3d.app.data.OrderService
import com.nextlayer3d.app.models.Order
import com.nextlayer3d.app.state.SessionViewModel
import com.nextlayer3d.app.ui.theme.GradientText
import kotlinx.coroutines.launch

/** Order history for the signed-in account (via the existing-but-
 * previously-unused OrderService.myOrders), plus a "track by number"
 * dialog for an order not tied to this account (a gift, say). Reached by
 * tapping "My Orders" inside Hub rather than its own bottom tab, so it
 * takes a back arrow like any other pushed screen. */
@Composable
fun OrdersScreen(session: SessionViewModel, onBack: () -> Unit) {
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showTrackDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val email = session.email

    fun load() {
        val currentEmail = email
        if (currentEmail == null) {
            // Shouldn't happen — this screen only exists behind
            // session.isSignedIn — but don't leave the spinner running
            // forever if AuthService.currentUserEmail() ever comes back
            // null for a signed-in session.
            isLoading = false
            errorMessage = "No account email on this session."
            return
        }
        scope.launch {
            isLoading = true
            runCatching { OrderService.myOrders(currentEmail) }
                .onSuccess { orders = it; errorMessage = null }
                .onFailure { errorMessage = it.message ?: "Couldn't load your orders" }
            isLoading = false
        }
    }

    LaunchedEffect(email) { load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { GradientText("Orders") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { showTrackDialog = true }) {
                        Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Track by #")
                    }
                },
            )
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Column(
                    Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(Icons.Filled.WifiOff, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(errorMessage ?: "", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
                orders.isEmpty() -> Column(
                    Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(Icons.Filled.Inventory2, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("No orders yet", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Orders you place will show up here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(orders, key = { it.id }) { order -> OrderCard(order) }
                }
            }
        }
    }

    if (showTrackDialog) {
        TrackOrderDialog(onDismiss = { showTrackDialog = false })
    }
}

@Composable
private fun OrderCard(order: Order) {
    var expanded by remember { mutableStateOf(false) }

    Card(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(order.orderNumber, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        order.createdAt?.take(10) ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                StatusChip(order.statusDisplay)
                Icon(
                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (expanded) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(Modifier.height(12.dp))
                order.decodedItems.forEach { item ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${item.name} ×${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                        Text("$" + "%.2f".format(item.price * item.quantity), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("$" + "%.2f".format(order.total ?: 0.0), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (container, onContainer) = when (status.lowercase()) {
        "shipped", "delivered", "completed" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "pending", "processing" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
    }
    Surface(color = container, contentColor = onContainer, shape = MaterialTheme.shapes.small) {
        Text(status, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

@Composable
private fun TrackOrderDialog(onDismiss: () -> Unit) {
    var orderNumber by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<Order?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Track an Order") },
        text = {
            Column {
                OutlinedTextField(
                    value = orderNumber,
                    onValueChange = { orderNumber = it; result = null; errorMessage = null },
                    label = { Text("Order number") },
                    placeholder = { Text("ORD-1234567") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (isLoading) {
                    Spacer(Modifier.height(12.dp))
                    CircularProgressIndicator(Modifier.size(24.dp))
                }
                errorMessage?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                result?.let { order ->
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))
                    Text(order.statusDisplay, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    order.decodedItems.forEach { item ->
                        Text("${item.name} ×${item.quantity}", style = MaterialTheme.typography.bodySmall)
                    }
                    Text("Total: $" + "%.2f".format(order.total ?: 0.0), style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        result = null
                        try {
                            val found = OrderService.lookUpOrder(orderNumber.trim())
                            if (found == null) errorMessage = "No order found with that number." else result = found
                        } catch (e: Exception) {
                            errorMessage = e.message
                        }
                        isLoading = false
                    }
                },
                enabled = orderNumber.isNotBlank() && !isLoading,
            ) { Text("Look Up") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}
