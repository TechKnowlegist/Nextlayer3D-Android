package com.nextlayer3d.app.models

/** Local-only cart state, never synced to the backend until checkout
 * creates the Order — same model as the web app's CartContext. */
data class CartItem(
    val productId: String,
    val name: String,
    val price: Double,
    val image: String? = null,
    val icon: String? = null,
    val quantity: Int,
) {
    val lineTotal: Double
        get() = price * quantity
}
