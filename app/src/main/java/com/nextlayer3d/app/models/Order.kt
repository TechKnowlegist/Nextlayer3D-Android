package com.nextlayer3d.app.models

import com.google.gson.Gson

/** Mirrors the `Order` model — `items`/`shippingAddress` stay JSON-stringified,
 * same as the web app and the iOS app, rather than nested GraphQL types. */
data class Order(
    val id: String,
    val orderNumber: String,
    val items: String,
    val total: Double? = null,
    val paid: Boolean? = null,
    val status: String? = null,
    val customerEmail: String? = null,
    val fulfillmentMethod: String? = null,
    val paymentMethod: String? = null,
    val tipAmount: Double? = null,
    val progressPhotoKeys: String? = null,
    val shippingAddress: String? = null,
    val createdAt: String? = null,
) {
    val decodedItems: List<OrderItem>
        get() = runCatching {
            Gson().fromJson(items, Array<OrderItem>::class.java).toList()
        }.getOrDefault(emptyList())

    val statusDisplay: String
        get() = (status ?: "pending").replaceFirstChar { it.uppercase() }
}

data class OrderItem(
    val productId: String? = null,
    val name: String,
    val price: Double,
    val quantity: Int,
    val image: String? = null,
    val icon: String? = null,
)

data class ShippingAddress(
    val fullName: String,
    val street: String,
    val city: String,
    val state: String,
    val zip: String,
    val country: String,
)
