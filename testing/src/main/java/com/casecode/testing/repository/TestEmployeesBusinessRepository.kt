package com.casecode.testing.repository

import com.casecode.domain.model.users.Employee
import com.casecode.domain.repository.AddEmployees
import com.casecode.domain.repository.EmployeesBusinessRepository
import com.casecode.domain.utils.Resource
import com.casecode.testing.util.MainDispatcherRule
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestEmployeesBusinessRepository@Inject constructor() : EmployeesBusinessRepository
{
   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()
   
   
   private var shouldReturnError = false
   private var shouldReturnEmpty = false
   
   
   @BeforeEach
   fun setup()
   {
      shouldReturnError = false
      shouldReturnEmpty = false
   }
   
   override suspend fun getEmployees(uid: String): List<Employee>
   {
      TODO("Not yet implemented")
   }
   
   override suspend fun setEmployees(employees: ArrayList<Employee>, uid: String): AddEmployees
   {
      if (shouldReturnError)
      {
         return Resource.Error("Exception")
      } else if (shouldReturnEmpty)
      {
         return Resource.Empty()
      }
      return Resource.Success(true)
   }
   
   fun setReturnError(value: Boolean)
   {
      shouldReturnError = value
   }
   
   fun setReturnEmpty(value: Boolean)
   {
      shouldReturnEmpty = value
   }
}