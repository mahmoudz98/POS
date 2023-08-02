package com.casecode.domain.entity

data class Plan(
    val documentsCore: DocumentsCore,
    val duration: Int,
    val planCode: Int,
    val planName: String,
    val price: Double
)