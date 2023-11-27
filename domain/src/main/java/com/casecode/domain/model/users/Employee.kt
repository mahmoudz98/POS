package com.casecode.domain.model.users

data class Employee(
    val name: String,
    val phoneNumber: String,
    val password: String,
    val branchName: String,
    val permission: String
) {
    // Add a no-argument constructor
    constructor() : this("", "", "", "", "")
}