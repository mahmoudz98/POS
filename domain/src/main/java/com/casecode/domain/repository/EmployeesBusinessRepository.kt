package com.casecode.domain.repository

import com.casecode.domain.model.users.Business
import com.casecode.domain.model.users.Employee
import com.casecode.domain.utils.Resource
import javax.inject.Singleton
typealias AddEmployees = Resource<Boolean>

@Singleton
interface EmployeesBusinessRepository
{
   suspend fun getEmployees(uid: String): List<Employee>
   suspend fun setEmployees(employees:ArrayList<Employee>, uid: String): AddEmployees
}
