package com.casecode.domain.entity

data class Employee(
    val employeeId: Int,
    val employeeRole: EmployeeRole,
    val name: String,
    val password: String,
    val schedule: Schedule
)