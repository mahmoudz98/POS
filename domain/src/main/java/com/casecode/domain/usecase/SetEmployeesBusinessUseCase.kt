package com.casecode.domain.usecase

import com.casecode.domain.model.users.Employee
import com.casecode.domain.repository.AddEmployees
import com.casecode.domain.repository.EmployeesBusinessRepository
import com.casecode.domain.utils.EmptyType
import com.casecode.domain.utils.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetEmployeesBusinessUseCase @Inject constructor(private val employeesRepo: EmployeesBusinessRepository)
{
   suspend operator fun invoke(employees: ArrayList<Employee>, uid: String): AddEmployees
   {
      if(uid.isEmpty()){
         return Resource.empty( EmptyType.DATA, "uid is empty")
      }
      if(employees.isEmpty())
      {
         return Resource.empty( EmptyType.DATA, "employees are empty")
      }
        return employeesRepo.setEmployees(employees, uid)
   }
}