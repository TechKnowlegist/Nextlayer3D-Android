package com.nextlayer3d.app.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nextlayer3d.app.models.CartItem
import com.nextlayer3d.app.models.Product

class CartViewModel : ViewModel() {
    var items by mutableStateOf<List<CartItem>>(emptyList())
        private set

    val total: Double
        get() = items.sumOf { it.lineTotal }

    val itemCount: Int
        get() = items.sumOf { it.quantity }

    fun add(product: Product) {
        val existing = items.firstOrNull { it.productId == product.id }
        items = if (existing != null) {
            items.map { if (it.productId == product.id) it.copy(quantity = it.quantity + 1) else it }
        } else {
            items + CartItem(
                productId = product.id,
                name = product.name,
                price = product.price ?: 0.0,
                image = product.image,
                icon = product.icon,
                quantity = 1,
            )
        }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        items = if (quantity <= 0) {
            items.filterNot { it.productId == productId }
        } else {
            items.map { if (it.productId == productId) it.copy(quantity = quantity) else it }
        }
    }

    fun remove(productId: String) {
        items = items.filterNot { it.productId == productId }
    }

    fun clear() {
        items = emptyList()
    }
}
