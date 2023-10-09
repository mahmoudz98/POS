package com.casecode.domain.entity

data class Role(
     val modules: List<Module>,
     val roleDescription: String,
     val roleName: String,
               )