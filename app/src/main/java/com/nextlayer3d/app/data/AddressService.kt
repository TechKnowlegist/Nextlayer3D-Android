package com.nextlayer3d.app.data

import com.nextlayer3d.app.models.Address
import com.nextlayer3d.app.models.AddressInput

private data class ListAddressesData(val listAddresses: ModelConnection<Address>)
private data class CreateAddressData(val createAddress: Address)
private data class UpdateAddressData(val updateAddress: Address)
private data class DeleteAddressData(val deleteAddress: Address)

/** Address is owner-scoped (`allow.owner()`), so every call here implicitly
 * only ever touches the signed-in user's own addresses — the backend
 * enforces that, not this client code. */
object AddressService {

    suspend fun list(): List<Address> {
        val result: ListAddressesData = GraphQLClient.query(GraphQLDocuments.LIST_ADDRESSES)
        return result.listAddresses.items
    }

    suspend fun create(input: AddressInput): Address {
        val result: CreateAddressData = GraphQLClient.mutate(GraphQLDocuments.CREATE_ADDRESS, mapOf("input" to inputMap(input)))
        return result.createAddress
    }

    suspend fun update(id: String, input: AddressInput): Address {
        val map = inputMap(input).toMutableMap()
        map["id"] = id
        val result: UpdateAddressData = GraphQLClient.mutate(GraphQLDocuments.UPDATE_ADDRESS, mapOf("input" to map))
        return result.updateAddress
    }

    suspend fun delete(id: String) {
        GraphQLClient.mutate<DeleteAddressData>(GraphQLDocuments.DELETE_ADDRESS, mapOf("input" to mapOf("id" to id)))
    }

    private fun inputMap(input: AddressInput): Map<String, Any> = mapOf(
        "label" to input.label,
        "fullName" to input.fullName,
        "street" to input.street,
        "city" to input.city,
        "state" to input.state,
        "zip" to input.zip,
        "country" to input.country,
        "isDefault" to input.isDefault,
    )
}
