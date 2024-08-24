package com.casecode.pos.core.domain.repository

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Employee
import kotlinx.coroutines.flow.Flow

typealias AddEmployees = Resource<Boolean>
typealias ResourceEmployees = Resource<List<Employee>>

interface EmployeesBusinessRepository {
    fun getEmployees(): Flow<ResourceEmployees>

    suspend fun setEmployees(employees: MutableList<Employee>): AddEmployees

    suspend fun addEmployee(employees: Employee): Resource<Boolean>

    suspend fun updateEmployee(
        employees: Employee,
        oldEmployee: Employee,
    ): Resource<Boolean>
}