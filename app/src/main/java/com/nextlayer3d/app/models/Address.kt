package com.nextlayer3d.app.models

/** Mirrors the owner-scoped `Address` model — each signed-in account only
 * ever sees/edits its own, enforced backend-side. */
data class Address(
    val id: String,
    val label: String? = null,
    val fullName: String? = null,
    val street: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val country: String? = null,
    val isDefault: Boolean? = null,
) {
    val displayLine: String
        get() = listOfNotNull(street, city, state, zip).joinToString(", ")
}

data class AddressInput(
    val label: String,
    val fullName: String,
    val street: String,
    val city: String,
    val state: String,
    val zip: String,
    val country: String,
    val isDefault: Boolean,
)
