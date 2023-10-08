package com.casecode.domain.entity

data class BasicItem(
    val image: String? = null,
    val name: String? = null,
    val sku: String? = null,
    val unitOfMeasurement: String? = null
) {
    // Add any additional properties or methods here
    constructor() : this(image = null, name = null, sku = null, unitOfMeasurement = null)
}