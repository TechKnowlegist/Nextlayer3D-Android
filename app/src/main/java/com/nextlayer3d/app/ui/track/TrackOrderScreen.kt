package com.nextlayer3d.app.ui.track

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nextlayer3d.app.data.OrderService
import com.nextlayer3d.app.models.Order
import kotlinx.coroutines.launch

@Composable
fun TrackOrderScreen() {
    var orderNumber by remember { mutableStateOf("") }
    var order by remember { mutableStateOf<Order?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(topBar = { TopAppBar(title = { Text("Track Order") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = orderNumber,
                onValueChange = { orderNumber = it },
                label = { Text("ORD-1234567") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null
                        order = null
                        try {
                            val found = OrderService.lookUpOrder(orderNumber.trim())
                            if (found == null) errorMessage = "No order found with that number." else order = found
                        } catch (e: Exception) {
                            errorMessage = e.message
                        }
                        isLoading = false
                    }
                },
                enabled = orderNumber.isNotBlank() && !isLoading,
            ) {
                Text("Track Order")
            }

            if (isLoading) {
                Spacer(Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            errorMessage?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            order?.let { current ->
                Spacer(Modifier.height(16.dp))
                Text(current.statusDisplay, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LazyColumn {
                    items(current.decodedItems) { item ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.name)
                            Text("×${item.quantity}")
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total")
                    Text("$" + "%.2f".format(current.total ?: 0.0))
                }
            }
        }
    }
}
