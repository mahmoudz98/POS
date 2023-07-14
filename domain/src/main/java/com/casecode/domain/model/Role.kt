package com.casecode.domain.model

data class Role(
    val modules: List<Module>,
    val roleDescription: String,
    val roleName: String
)