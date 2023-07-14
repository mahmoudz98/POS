package com.casecode.domain.model

data class Business(
    val branches: List<Branches>,
    val email: String,
    val storeType: String
)