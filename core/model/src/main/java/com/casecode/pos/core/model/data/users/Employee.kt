package com.casecode.pos.core.model.data.users

data class Employee(
    val name: String = "",
    val phoneNumber: String = "",
    val password: String? = null,
    val branchName: String? = null,
    val permission: String = "",
)