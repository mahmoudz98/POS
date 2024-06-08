package com.casecode.domain.model.users

data class SubscriptionBusiness(
     val type: String? = "",
     val cost: Long? = 0L,
     val duration: Long? = 0L,
     val permissions: List<String>? = emptyList()
                               )