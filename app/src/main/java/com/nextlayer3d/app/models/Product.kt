package com.nextlayer3d.app.models

/** Mirrors the `Product` model in the web app's amplify/data/resource.ts. */
data class Product(
    val id: String,
    val name: String,
    val description: String? = null,
    val price: Double? = null,
    val category: String? = null,
    val rating: Int? = null,
    val icon: String? = null,
    val image: String? = null,
    val stock: Int? = null,
    val isSamplePack: Boolean? = null,
) {
    val isOutOfStock: Boolean
        get() = isSamplePack != true && (stock ?: 1) <= 0

    val formattedPrice: String
        get() = "$" + "%.2f".format(price ?: 0.0)
}
