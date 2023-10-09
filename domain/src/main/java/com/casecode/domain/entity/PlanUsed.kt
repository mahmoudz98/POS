package com.casecode.domain.entity

data class PlanUsed(
     val documentsUsed: DocumentsUsed? = null,
     val durationUsed: Long? = null,
     val planCode: Long? = null,
     val planName: String? = null,
                   )