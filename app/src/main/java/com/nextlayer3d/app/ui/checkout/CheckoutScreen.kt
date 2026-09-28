package com.nextlayer3d.app.ui.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nextlayer3d.app.data.OrderService
import com.nextlayer3d.app.models.ShippingAddress
import com.nextlayer3d.app.state.CartViewModel
import com.nextlayer3d.app.state.SessionViewModel
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.launch

@Composable
fun CheckoutScreen(cart: CartViewModel, session: SessionViewModel, onOrderPlaced: (String) -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var zip by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("United States") }

    var clientSecret by remember { mutableStateOf<String?>(null) }
    var isPreparing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val formIsValid = fullName.isNotBlank() && street.isNotBlank() && city.isNotBlank() && state.isNotBlank() && zip.isNotBlank()

    suspend fun placeOrder() {
        val address = ShippingAddress(fullName, street, city, state, zip, country)
        val orderNumber = "ORD-${System.currentTimeMillis() / 1000}"
        try {
            val order = OrderService.createOrder(
                orderNumber = orderNumber,
                items = cart.items,
                total = cart.total,
                customerEmail = session.email,
                paymentMethod = "stripe",
                fulfillmentMethod = "shipping",
                shippingAddress = address,
            )
            cart.items.forEach { OrderService.decrementStock(it.productId, it.quantity) }
            session.email?.let {
                OrderService.sendOrderEmail(
                    to = it,
                    subject = "Order Confirmed — ${order.orderNumber}",
                    heading = "Thanks for your order!",
                    message = "We're getting started on it.",
                    orderNumber = order.orderNumber,
                )
            }
            cart.clear()
            onOrderPlaced(order.orderNumber)
        } catch (e: Exception) {
            errorMessage = "Payment succeeded but we couldn't save your order — contact support with this: ${e.message}"
        }
    }

    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Completed -> scope.launch { placeOrder() }
            is PaymentSheetResult.Canceled -> {}
            is PaymentSheetResult.Failed -> errorMessage = "Payment failed: ${result.error.message}"
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Checkout") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(fullName, { fullName = it }, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(street, { street = it }, label = { Text("Street") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(city, { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(state, { state = it }, label = { Text("State") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(zip, { zip = it }, label = { Text("ZIP") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(country, { country = it }, label = { Text("Country") }, modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", style = MaterialTheme.typography.titleMedium)
                Text("$" + "%.2f".format(cart.total), style = MaterialTheme.typography.titleMedium)
            }

            errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(16.dp))
            if (clientSecret == null) {
                Button(
                    onClick = {
                        scope.launch {
                            isPreparing = true
                            errorMessage = null
                            try {
                                clientSecret = OrderService.createPaymentIntent(cart.total)
                            } catch (e: Exception) {
                                errorMessage = "Couldn't start checkout: ${e.message}"
                            }
                            isPreparing = false
                        }
                    },
                    enabled = formIsValid && !isPreparing,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (isPreparing) "Preparing payment…" else "Continue to Payment")
                }
            } else {
                Button(
                    onClick = {
                        paymentSheet.presentWithPaymentIntent(
                            clientSecret!!,
                            PaymentSheet.Configuration(merchantDisplayName = "Nextlayer3D"),
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Pay $" + "%.2f".format(cart.total))
                }
            }
        }
    }
}
