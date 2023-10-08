package com.casecode.domain.entity

data class Store(
    val basicItems: List<BasicItem>? = null,
    val storeCode: Int? = null,
    val storeType: String? = null
) {
    // Add any additional properties or methods here
    constructor() : this(basicItems = null, storeCode = 0, storeType = null)
}