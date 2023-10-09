package com.casecode.domain.model.users

data class Item(
     val name: String,
     val price: Double,
     val quantity: Double,
     val sku: String,
     val unitOfMeasurement: String,
     val image: String) {
   constructor() : this("", 0.0, 0.0, "", "", "")
}