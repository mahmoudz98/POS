package com.casecode.domain.model

data class DocumentsCore(
    val deletes: Int,
    val reads: Int,
    val writes: Int
)