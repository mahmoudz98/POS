package com.casecode.domain.model.users

data class User(
    val business: Business,
    val email: String,
    val employees: List<Employee>,
    val invoices: List<Invoice>,
    val items: List<Item>,
    val name: String,
    val password: String,
    val phoneNumber: String,
    val subscription: Subscription,
    val uid: String
) {
    // Add a no-argument constructor
    constructor() : this(
        Business(),
        "",
        emptyList(),
        emptyList(),
        emptyList(),
        "",
        "",
        "",
        Subscription(),
        ""
    )
}