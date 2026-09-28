package com.nextlayer3d.app.data

import com.google.gson.Gson
import com.nextlayer3d.app.models.CartItem
import com.nextlayer3d.app.models.Order
import com.nextlayer3d.app.models.OrderItem
import com.nextlayer3d.app.models.ShippingAddress

private data class CreatePaymentIntentData(val createPaymentIntent: String?)
private data class DecrementStockData(val decrementStock: Boolean?)
private data class SendOrderEmailData(val sendOrderEmail: Boolean?)
private data class CreateOrderData(val createOrder: Order)
private data class ListOrdersData(val listOrders: ModelConnection<Order>)

class OrderServiceException(message: String) : Exception(message)

object OrderService {

    /** Calls the same `createPaymentIntent` Amplify Function the web/iOS
     * apps use — it talks to Stripe with the secret key server-side, and
     * returns a PaymentIntent client secret for the Stripe Android SDK. */
    suspend fun createPaymentIntent(amount: Double): String {
        val result: CreatePaymentIntentData = GraphQLClient.mutate(
            GraphQLDocuments.CREATE_PAYMENT_INTENT,
            mapOf("amount" to amount)
        )
        return result.createPaymentIntent ?: throw OrderServiceException("No client secret returned")
    }

    suspend fun decrementStock(productId: String, quantity: Int) {
        runCatching {
            GraphQLClient.mutate<DecrementStockData>(
                GraphQLDocuments.DECREMENT_STOCK,
                mapOf("productId" to productId, "quantity" to quantity)
            )
        }
    }

    suspend fun sendOrderEmail(to: String, subject: String, heading: String, message: String, orderNumber: String) {
        runCatching {
            GraphQLClient.mutate<SendOrderEmailData>(
                GraphQLDocuments.SEND_ORDER_EMAIL,
                mapOf("to" to to, "subject" to subject, "heading" to heading, "message" to message, "orderNumber" to orderNumber)
            )
        }
    }

    suspend fun createOrder(
        orderNumber: String,
        items: List<CartItem>,
        total: Double,
        customerEmail: String?,
        paymentMethod: String,
        fulfillmentMethod: String,
        shippingAddress: ShippingAddress?,
    ): Order {
        val lineItems = items.map {
            OrderItem(productId = it.productId, name = it.name, price = it.price, quantity = it.quantity, image = it.image, icon = it.icon)
        }
        val gson = Gson()
        val input = mutableMapOf<String, Any>(
            "orderNumber" to orderNumber,
            "items" to gson.toJson(lineItems),
            "total" to total,
            "paid" to true,
            "status" to "pending",
            "paymentMethod" to paymentMethod,
            "fulfillmentMethod" to fulfillmentMethod,
        )
        customerEmail?.let { input["customerEmail"] = it }
        shippingAddress?.let { input["shippingAddress"] = gson.toJson(it) }

        val result: CreateOrderData = GraphQLClient.mutate(GraphQLDocuments.CREATE_ORDER, mapOf("input" to input))
        return result.createOrder
    }

    suspend fun lookUpOrder(orderNumber: String): Order? {
        val result: ListOrdersData = GraphQLClient.query(GraphQLDocuments.ORDERS_BY_NUMBER, mapOf("orderNumber" to orderNumber))
        return result.listOrders.items.firstOrNull()
    }

    suspend fun myOrders(email: String): List<Order> {
        val result: ListOrdersData = GraphQLClient.query(GraphQLDocuments.ORDERS_BY_EMAIL, mapOf("email" to email))
        return result.listOrders.items.sortedByDescending { it.createdAt ?: "" }
    }
}
