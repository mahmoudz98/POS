package com.casecode.pos.core.model.data

import com.casecode.pos.core.model.data.permissions.Permission

data class EmployeeLoginData(
    val name: String,
    val uid: String,
    val phoneNumber: String,
    val password: String,
    val branch: String,
    val permission: Permission = Permission.NONE,
)