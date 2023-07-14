package com.casecode.domain.model

data class Store(
    val basicItems: List<BasicItem>,
    val storeCode: Int,
    val storeType: String
)