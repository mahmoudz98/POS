package com.casecode.domain.entity

data class DocumentsUsed(
    val deletes: Int,
    val reads: Int,
    val writes: Int
)