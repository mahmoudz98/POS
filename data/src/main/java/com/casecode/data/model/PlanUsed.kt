package com.casecode.data.model

import com.casecode.domain.entity.Plan
import com.casecode.domain.entity.PlanUsed

fun MutableSet<PlanUsed>?.addPlanUsed(plan: Plan): MutableSet<PlanUsed>{
   val newPlanUsed = this?: mutableSetOf()
   newPlanUsed.add(plan.toPlanUsed())
   return newPlanUsed
}