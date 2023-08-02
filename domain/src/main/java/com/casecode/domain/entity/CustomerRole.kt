package com.casecode.domain.entity

data class CustomerRole(
    val modules: List<Module>,
    val roleDescription: String,
    val roleName: String
)