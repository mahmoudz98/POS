package com.casecode.pos.core.model.data.subscriptions

data class Subscription(
    val cost: Long = 0L,
    val duration: Long = 0L,
    val permissions: List<String> = emptyList(),
    val type: String = "",
)