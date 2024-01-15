package com.casecode.domain.usecase

import com.casecode.domain.model.users.Employee
import com.casecode.domain.repository.AddEmployees
import com.casecode.domain.repository.EmployeesBusinessRepository
import com.casecode.domain.utils.EmptyType
import com.casecode.domain.utils.Resource
import com.casecode.pos.domain.R
import javax.inject.Inject

class SetEmployeesBusinessUseCase @Inject constructor(private val employeesRepo: EmployeesBusinessRepository)
{
   suspend operator fun invoke(employees: ArrayList<Employee>, uid: String): AddEmployees
   {
      if (uid.isEmpty())
      {
         return Resource.empty(EmptyType.DATA,  R.string.uid_empty)
      }
      if (employees.isEmpty())
      {
         return Resource.empty(EmptyType.DATA,  R.string.employees_empty)
      }
      return employeesRepo.setEmployees(employees, uid)
   }
}