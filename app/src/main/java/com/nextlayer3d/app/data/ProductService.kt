package com.nextlayer3d.app.data

import com.nextlayer3d.app.models.Product

private data class ListProductsData(val listProducts: ModelConnection<Product>)
private data class GetProductData(val getProduct: Product?)

object ProductService {

    suspend fun listProducts(): List<Product> {
        val result: ListProductsData = GraphQLClient.query(GraphQLDocuments.LIST_PRODUCTS)
        return result.listProducts.items
    }

    suspend fun getProduct(id: String): Product? {
        val result: GetProductData = GraphQLClient.query(GraphQLDocuments.GET_PRODUCT, mapOf("id" to id))
        return result.getProduct
    }
}
