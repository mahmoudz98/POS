package com.casecode.domain.entity

data class Store(
    val basicItems: List<BasicItem>,
    val storeCode: Int,
    val storeType: String
)