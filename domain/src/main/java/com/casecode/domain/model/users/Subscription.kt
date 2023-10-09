package com.casecode.domain.model.users

data class Subscription(
     val type: String,
     val cost: Double,
     val duration: Int,
     val permissions: List<String>
                       ) {
   // Add a no-argument constructor
   constructor() : this("", 0.0, 0, emptyList())
}