package com.casecode.domain.model.users

data class SubscriptionBusiness(
     val type: String,
     val cost: Long,
     val duration: Long,
     val permissions: List<String>
                               ) {
   // Add a no-argument constructor
   constructor() : this("", 0, 0, emptyList())
}