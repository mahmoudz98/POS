package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.model.data.users.Employee
import com.casecode.pos.core.domain.utils.EmptyType
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.domain.R
import com.casecode.pos.core.data.R.string as dataString
import com.casecode.pos.core.testing.repository.TestEmployeesBusinessRepository
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test


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
      val resultAddEmployeesBusiness = setEmployeesBusinessUseCase(employees)
      
      // Then
      assertThat(exceptedResultAddEmployees, `is`(resultAddEmployeesBusiness))
   }
   
   @Test
   fun `invoke with empty UID return resource with UID empty`() = runTest {
      // When uid is empty
      val resultAddEmployeesBusiness =
         setEmployeesBusinessUseCase(employees, )
      
      // Then - return Resource of empty uid.
      assertThat(resultAddEmployeesBusiness,
         `is`(Resource.empty(EmptyType.DATA,  dataString.core_data_uid_empty)))
      
   }

   @Test
   fun `invoke with empty Business return resource with employees empty`() = runTest {
      // When subscription business fields is empty
      val resultEmptySubscriptionBusiness =
         setEmployeesBusinessUseCase(arrayListOf())
      
      // Then - return Resource of empty data.
      assertThat(resultEmptySubscriptionBusiness,
         `is`(Resource.empty(EmptyType.DATA,  R.string.employees_empty)))
   }
   
   
}