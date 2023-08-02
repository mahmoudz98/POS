package com.casecode.domain.entity

data class Business(
    val branches: List<Branches>,
    val email: String,
    val storeType: String
)