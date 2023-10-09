package com.casecode.domain.entity

data class Module(
     val moduleDescription: String,
     val moduleName: String,
     val privileges: List<Privilege>,
                 )