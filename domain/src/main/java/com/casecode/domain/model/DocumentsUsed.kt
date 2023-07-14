package com.casecode.domain.model

data class DocumentsUsed(
    val deletes: Int,
    val reads: Int,
    val writes: Int
)