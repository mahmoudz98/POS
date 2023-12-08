package com.casecode.domain.model.subscriptions

data class Subscription(
     val cost: Long,
     val duration: Long,
     val permissions: List<String>,
     val type: String
                       )