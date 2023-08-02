package com.casecode.domain.entity

data class DocumentsCore(
    val deletes: Int,
    val reads: Int,
    val writes: Int
)