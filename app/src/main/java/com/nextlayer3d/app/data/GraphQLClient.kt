package com.nextlayer3d.app.data

import com.amplifyframework.api.aws.GsonVariablesSerializer
import com.amplifyframework.api.graphql.SimpleGraphQLRequest
import com.amplifyframework.kotlin.core.Amplify
import com.google.gson.Gson

class GraphQLDataException(message: String) : Exception(message)

/** Amplify Android's raw-document GraphQL pattern (distinct from iOS's
 * decodePath approach): the request's response type is always String, and
 * `.data` comes back as the whole "data" object as a JSON string, which
 * gets Gson-parsed into a wrapper matching that exact shape (see the
 * `*Data` classes in ProductService/OrderService/AddressService). */
object GraphQLClient {

    suspend inline fun <reified T> query(document: String, variables: Map<String, Any> = emptyMap()): T {
        val request = SimpleGraphQLRequest(document, variables, String::class.java, GsonVariablesSerializer())
        val response = Amplify.API.query(request)
        val data = response.data ?: throw GraphQLDataException("No data in response: ${response.errors}")
        return Gson().fromJson(data, T::class.java)
    }

    suspend inline fun <reified T> mutate(document: String, variables: Map<String, Any> = emptyMap()): T {
        val request = SimpleGraphQLRequest(document, variables, String::class.java, GsonVariablesSerializer())
        val response = Amplify.API.mutate(request)
        val data = response.data ?: throw GraphQLDataException("No data in response: ${response.errors}")
        return Gson().fromJson(data, T::class.java)
    }
}
