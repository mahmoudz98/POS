package com.casecode.data.model

import com.casecode.domain.entity.DocumentsCore
import com.casecode.domain.entity.DocumentsUsed
import com.casecode.domain.entity.Plan
import com.casecode.domain.entity.PlanUsed

fun Plan.toPlanUsed() = PlanUsed(
documentsUsed = documentsCore?.toDocumentUsed(),
    durationUsed = duration,
    planCode = planCode,
    planName = planName
)
fun DocumentsCore.toDocumentUsed() = DocumentsUsed(
    writes =  writes,
    reads = reads,
    deletes = deletes
)