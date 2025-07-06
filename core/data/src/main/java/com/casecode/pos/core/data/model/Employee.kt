package com.casecode.pos.core.data.model

import com.casecode.pos.core.firebase.model.NetworkEmployee
import com.casecode.pos.core.model.data.business.Employee


fun NetworkEmployee.asExternalModel(businessId: String): Employee = Employee(
    id = this.id,
    employeeId = this.employeeId ?: "",
    businessId = businessId,
    name = this.name ?: "",
    role = this.role ?: "CASHIER",
    assignedBranchIds = this.assignedBranchIds ?: emptyList()
)
