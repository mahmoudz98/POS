package com.casecode.domain.entity

data class PlanUsed(
    val documentsUsed: DocumentsUsed,
    val durationUsed: Int,
    val planCode: Int,
    val planName: String
)