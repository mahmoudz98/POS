package com.casecode.domain.repository

import com.casecode.domain.model.users.Employee
import com.casecode.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

typealias AddEmployees = Resource<Boolean>

interface EmployeesBusinessRepository {
     fun getEmployees(): Flow<Resource<List<Employee>>>
    suspend fun setEmployees(employees: MutableList<Employee>, uid: String): AddEmployees
    suspend fun addEmployee(employees: Employee): Resource<Boolean>
    suspend fun updateEmployee(employees: Employee, oldEmployee: Employee): Resource<Boolean>
}