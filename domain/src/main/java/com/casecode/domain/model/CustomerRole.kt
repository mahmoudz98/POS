package com.casecode.domain.model

data class CustomerRole(
    val modules: List<Module>,
    val roleDescription: String,
    val roleName: String
)