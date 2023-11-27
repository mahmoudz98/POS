package com.casecode.domain.model.users

data class Item(
    val name: String,
    val price: Double,
    val quantity: Double,
    val sku: String,
    val unitOfMeasurement: String,
    val image: String
) {
    // Add a no-argument constructor
    constructor() : this("", 0.0, 0.0, "", "", "")
}