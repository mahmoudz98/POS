package com.casecode.domain.usecase

import com.casecode.domain.model.users.Employee
import com.casecode.domain.utils.EmptyType
import com.casecode.domain.utils.Resource
import com.casecode.testing.repository.TestEmployeesBusinessRepository
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


class SetEmployeesBusinessUseCaseTest{
   
   // Given uid and employees
   private val uid = "test"
   private val employees = arrayListOf(Employee())
   
   // Subject under test
   private  val testEmployeesBusinessRepository: TestEmployeesBusinessRepository = TestEmployeesBusinessRepository()
   private val setEmployeesBusinessUseCase: SetEmployeesBusinessUseCase = SetEmployeesBusinessUseCase(testEmployeesBusinessRepository)
   
   @Test
   fun setEmployeesBusinessUseCase_shouldAddNewEmployees_returnResourceOfTrue() = runTest {
      val exceptedResultAddEmployees = Resource.success(true)
      // When
      val resultAddEmployeesBusiness = setEmployeesBusinessUseCase(employees, uid)
      
      // Then
      assertThat(exceptedResultAddEmployees, `is`(resultAddEmployeesBusiness))
   }
   
   @Test
   fun setEmployeesBusinessUseCase_emptyUid_returnEmptyUid() = runTest {
      // When uid is empty
      val resultAddEmployeesBusiness =
         setEmployeesBusinessUseCase(employees, "")
      
      // Then - return Resource of empty uid.
      val exceptedResultAddEmployees = Resource.empty<Boolean>(EmptyType.DATA, "uid is empty")
      
      assertThat(exceptedResultAddEmployees, `is`(resultAddEmployeesBusiness))
      
      
   }
   
   @Test
   fun setEmployeesBusinessUseCase_emptyBusiness_returnEmptyTypeOfSubscription() = runTest {
      // When subscription business fields is empty
      val resultEmptySubscriptionBusiness =
         setEmployeesBusinessUseCase(arrayListOf(), uid)
      
      // Then - return Resource of empty data.
      assertThat(resultEmptySubscriptionBusiness,
         `is`(Resource.empty(EmptyType.DATA, "employees are empty")))
   }
   
   
}