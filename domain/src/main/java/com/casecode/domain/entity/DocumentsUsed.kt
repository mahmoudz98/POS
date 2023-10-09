package com.casecode.domain.entity

data class DocumentsUsed(
     val deletes: Long? = null,
     val reads: Long? = null,
     val writes: Long? = null,
                        )