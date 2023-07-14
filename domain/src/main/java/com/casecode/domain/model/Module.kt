package com.casecode.domain.model

data class Module(
    val moduleDescription: String,
    val moduleName: String,
    val privileges: List<Privilege>
)