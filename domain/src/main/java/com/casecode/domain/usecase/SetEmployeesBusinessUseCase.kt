package com.casecode.domain.usecase

import com.casecode.domain.model.users.Employee
import com.casecode.domain.repository.EmployeesBusinessRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetEmployeesBusinessUseCase @Inject constructor(private val employeesRepo: EmployeesBusinessRepository)
{
   suspend operator fun invoke(employees: ArrayList<Employee>, uid: String) = employeesRepo.setEmployees(employees, uid)
}