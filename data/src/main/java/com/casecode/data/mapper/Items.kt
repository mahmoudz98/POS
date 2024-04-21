package com.casecode.data.mapper

import com.casecode.domain.model.users.Item

fun Item.asExternalModel() = mapOf(
    "name" to this.name,
    "price" to this.price,
    "quantity" to this.quantity,
    "sku" to this.sku,
    "unit_of_measurement" to this.unitOfMeasurement,
    "image_url" to this.imageUrl,
)