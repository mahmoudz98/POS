package com.casecode.domain.model.users

data class Invoice(
    val number: Int,
    val date: String,
    val createdBy: Int,
    val customer: Customer,
    val items: List<Item>
) {
    // Add a no-argument constructor
    constructor() : this(0, "", 0, Customer(), emptyList())
}