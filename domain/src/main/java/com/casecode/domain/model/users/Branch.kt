package com.casecode.domain.model.users

data class Branch(
    val name: String,
    val phoneNumber: String
) {
    // Add a no-argument constructor
    constructor() : this("", "")
}