package com.casecode.domain.model

data class EmployeeRole(
    val modules: List<Module>,
    val roleDescription: String,
    val roleName: String
)