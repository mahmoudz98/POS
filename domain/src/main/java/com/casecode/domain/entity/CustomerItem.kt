package com.casecode.domain.entity

data class CustomerItem(
    val image: String,
    val name: String,
    val price: Int,
    val quantity: Int,
    val sku: String,
    val unitOfMeasurement: String
)