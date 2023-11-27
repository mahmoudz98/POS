package com.casecode.domain.model.users

data class Business(
    val type: String,
    val email: String,
    val branches: List<Branch>
) {
    // Add a no-argument constructor
    constructor() : this("", "", emptyList())
}