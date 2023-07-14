package com.casecode.domain.model

data class Item(
    val image: String,
    val name: String,
    val price: Int,
    val quantity: Int,
    val sku: String,
    val unitOfMeasurement: String
)