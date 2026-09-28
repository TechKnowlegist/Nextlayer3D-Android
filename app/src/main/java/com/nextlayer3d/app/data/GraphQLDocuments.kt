package com.nextlayer3d.app.data

/** Raw GraphQL documents against the shared AppSync API, hand-written
 * instead of codegen'd (no Amplify CLI in this build environment). Field
 * selections and operation names follow AppSync's standard naming for an
 * `a.model()` schema, same as the iOS app's GraphQLDocuments.swift. */
object GraphQLDocuments {

    const val LIST_PRODUCTS = """
        query ListProducts {
          listProducts {
            items {
              id
              name
              description
              price
              category
              rating
              icon
              image
              stock
              isSamplePack
            }
            nextToken
          }
        }
    """

    val GET_PRODUCT = """
        query GetProduct(${'$'}id: ID!) {
          getProduct(id: ${'$'}id) {
            id
            name
            description
            price
            category
            rating
            icon
            image
            stock
            isSamplePack
          }
        }
    """

    private const val ORDER_FIELDS = """
        id
        orderNumber
        items
        total
        paid
        status
        customerEmail
        fulfillmentMethod
        paymentMethod
        tipAmount
        progressPhotoKeys
        shippingAddress
        createdAt
    """

    val CREATE_ORDER = """
        mutation CreateOrder(${'$'}input: CreateOrderInput!) {
          createOrder(input: ${'$'}input) {
            $ORDER_FIELDS
          }
        }
    """

    /** No secondary index on orderNumber in the schema, so this looks it up
     * the same way the web/iOS apps do: a filtered list query. */
    val ORDERS_BY_NUMBER = """
        query OrdersByNumber(${'$'}orderNumber: String!) {
          listOrders(filter: { orderNumber: { eq: ${'$'}orderNumber } }) {
            items {
              $ORDER_FIELDS
            }
          }
        }
    """

    val ORDERS_BY_EMAIL = """
        query OrdersByEmail(${'$'}email: String!) {
          listOrders(filter: { customerEmail: { eq: ${'$'}email } }) {
            items {
              $ORDER_FIELDS
            }
          }
        }
    """

    private const val ADDRESS_FIELDS = """
        id
        label
        fullName
        street
        city
        state
        zip
        country
        isDefault
    """

    val LIST_ADDRESSES = """
        query ListAddresses {
          listAddresses {
            items {
              $ADDRESS_FIELDS
            }
          }
        }
    """

    val CREATE_ADDRESS = """
        mutation CreateAddress(${'$'}input: CreateAddressInput!) {
          createAddress(input: ${'$'}input) {
            $ADDRESS_FIELDS
          }
        }
    """

    val UPDATE_ADDRESS = """
        mutation UpdateAddress(${'$'}input: UpdateAddressInput!) {
          updateAddress(input: ${'$'}input) {
            $ADDRESS_FIELDS
          }
        }
    """

    val DELETE_ADDRESS = """
        mutation DeleteAddress(${'$'}input: DeleteAddressInput!) {
          deleteAddress(input: ${'$'}input) {
            id
          }
        }
    """

    val CREATE_PAYMENT_INTENT = """
        mutation CreatePaymentIntent(${'$'}amount: Float!) {
          createPaymentIntent(amount: ${'$'}amount)
        }
    """

    val DECREMENT_STOCK = """
        mutation DecrementStock(${'$'}productId: String!, ${'$'}quantity: Int!) {
          decrementStock(productId: ${'$'}productId, quantity: ${'$'}quantity)
        }
    """

    val SEND_ORDER_EMAIL = """
        mutation SendOrderEmail(${'$'}to: String!, ${'$'}subject: String!, ${'$'}heading: String!, ${'$'}message: String!, ${'$'}orderNumber: String!) {
          sendOrderEmail(to: ${'$'}to, subject: ${'$'}subject, heading: ${'$'}heading, message: ${'$'}message, orderNumber: ${'$'}orderNumber)
        }
    """
}

/** Generic paginated-list envelope AppSync wraps `listX` responses in. */
data class ModelConnection<T>(
    val items: List<T> = emptyList(),
    val nextToken: String? = null,
)
