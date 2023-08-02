package com.casecode.domain.entity

data class Customer(
    val address: Address,
    val business: Business,
    val customerCode: String,
    val customerItems: List<CustomerItem>,
    val customerRole: CustomerRole,
    val email: String,
    val employees: List<Employee>,
    val invoices: List<Invoice>,
    val name: String,
    val password: String,
    val phoneNumber: String,
    val planUsed: PlanUsed,
    val settings: Settings
)