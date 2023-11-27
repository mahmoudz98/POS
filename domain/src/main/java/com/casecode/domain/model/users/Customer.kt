package com.casecode.domain.model.users

data class Customer(
    val name: String,
    val phoneNumber: String,
    val email: String
) {
    // Add a no-argument constructor
    constructor() : this("", "", "")
}