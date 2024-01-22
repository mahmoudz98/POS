package com.casecode.pos.viewmodel

import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.StoreType
import com.casecode.domain.repository.AddBusiness
import com.casecode.pos.R
import com.casecode.testing.BaseTest
import com.casecode.testing.util.MainDispatcherRule
import com.casecode.testing.util.getOrAwaitValue
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

/**
 * A JUnit test class for the [BusinessViewModel] class.
 *
 * Created by Mahmoud Abdalhafeez on 12/13/2023
 */
class BusinessViewModelTest : BaseTest()
{
   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()
   
   // subject under test
   private lateinit var businessViewModel: BusinessViewModel
   private val firebaseAuth: FirebaseAuth = mockk()
   override fun init()
   {
       every { firebaseAuth.currentUser?.uid } returns "test"
        businessViewModel =
            BusinessViewModel(testNetworkMonitor,firebaseAuth, setBusinessUseCase, getSubscriptionsUseCase,
               setSubscriptionBusinessUseCase, setEmployeesBusinessUseCase)
      
   }
   
   @Test
   fun setStoreType_whenStoreTypeArabic_returnStoreTypeEnglish()
   {
      // Given store type arabic
      val storeTypeAr = "قهوة"
      
      // When sent store type arabic
      businessViewModel.setStoreType(storeTypeAr)
      val storeTypeEn = businessViewModel.addBusiness().storeType
      
      // Then result is name of store english only
      assertThat(storeTypeEn, `is`(StoreType.Coffee))
      
   }
   
   
   @Test
   fun addBranch_WhenHasBranch_returnsTrueAndMessageSuccess()
   {
      // Given - add Branch
      businessViewModel.setBranchName("branch1")
      businessViewModel.setBranchPhone("123456")
      
      // When - add branch
      businessViewModel.addBranch()
      // Then - returns string branch success and true for is add branch.
      assertThat(R.string.add_branch_success, `is`(businessViewModel.userMessage.value?.peekContent()))
      assertThat(businessViewModel.isAddBranch.value?.peekContent(), `is`(true))
   }
   
   @Test
   fun updateBranch_whenChangeName_returnUpdateBranchIsTrueAndSuccess()
   {
      // Given
      val branch = Branch(1, "branch1", "1234")
      businessViewModel.branches.value = arrayListOf(branch)
      businessViewModel.setBranchSelected(branch)
      businessViewModel.setBranchName("branch2")
      
      // When
      businessViewModel.updateBranch()
      
      // Then
      assertThat(R.string.update_branch_success,
         `is`(businessViewModel.userMessage.value?.peekContent()))
      assertThat(businessViewModel.isUpdateBranch.value?.peekContent(), `is`(true))
      
   }
   
   @Test
   fun updateBranch_whenNotChange_returnUpdateBranchIsFalseAndFailed()
   {
      // Given
      val branch = Branch(1, "branch1", "1234")
      businessViewModel.branches.value = arrayListOf(branch)
      businessViewModel.setBranchSelected(branch)
      businessViewModel.setBranchName(branch.branchName !!)
      businessViewModel.setBranchPhone(branch.phoneNumber !!)
      
      // When
      businessViewModel.updateBranch()
      
      // Then
      assertThat(R.string.update_branch_fail,
         `is`(businessViewModel.userMessage.value?.peekContent()))
      assertThat(businessViewModel.isUpdateBranch.value?.peekContent(), `is`(false))
      
   }
   
   @Test
   fun `updateBranch when branches is out of index return failed and false`()
   {
      // Given
      val branch = Branch(1, "branch1", "1234")
      businessViewModel.setBranchSelected(branch)
      
      // When
      businessViewModel.updateBranch()
      
      // Then
      assertThat(R.string.update_branch_fail,
         `is`(businessViewModel.userMessage.value?.peekContent()))
      assertThat(businessViewModel.isUpdateBranch.value?.peekContent(), `is`(false))
      
      
   }
   
   @Test
   fun addBusiness_shouldReturnSuccessBusiness() = runTest {
      // Given
      val storeType = StoreType.Clothes.toString()
      val email = "test@email.com"
      val phone = "1234567890"
      val branches =
         arrayListOf(Branch(1, "Branch 1", "1234567890"), Branch(2, "Branch 2", "9876543210"))
      // handle network monitor available to true
      testNetworkMonitor.setConnected(true)
      businessViewModel.setNetworkMonitor()
      businessViewModel.setStoreType(storeType)
      businessViewModel.setEmail(email)
      businessViewModel.setPhoneBusiness(phone)
      businessViewModel.branches.value = branches
      businessViewModel.setCurrentUid("test")
      
      // When - add new business
      businessViewModel.setBusiness()
      
      var isSuccess = AddBusiness.success(false)
      val job = launch {
         isSuccess = businessViewModel.isAddBusiness.value as AddBusiness
      }
      
      // Execute pending coroutines actions
      advanceUntilIdle()
      
      // Then -
      assertThat(isSuccess, equalTo(AddBusiness.success(true)))
      job.cancel()
   }
   
   @Test
   fun setBusinessUseCase_whenNetworkIsUnavailable_thenReturnsSuccessFalseAndMessageNetworkError()
   {
      // Given - network unAvailable
      testNetworkMonitor.setConnected(false)
      businessViewModel.setNetworkMonitor()
      
      // When - add new business
      businessViewModel.setBusiness()
      
      
      // Then
      assertThat(businessViewModel.userMessage.value?.peekContent(), `is`(R.string.network_error))
   }
   
   @Test
   fun getSubscriptionBusiness_WhenHasListOfSubscription_thenReturnSuccess() = runTest {
      // Given
      testNetworkMonitor.setConnected(true)
      businessViewModel.setNetworkMonitor()
      
      val actualSubscriptions = subscriptionsFake()
      testSubscriptionsRepository.sendSubscriptions(actualSubscriptions)
      
      // When
      businessViewModel.getSubscriptionsBusiness()
      var result = emptyList<Subscription>()
      val job = launch {
         result = businessViewModel.subscriptions.value !!
      }
      
      // Execute pending coroutines actions
      advanceUntilIdle()
      // Then
      assertThat(result, `is`(actualSubscriptions))
      job.cancel()
   }
   
   @Test
   fun getSubscriptionsBusiness_whenHasError_thenReturnEmptyList() = runTest {
      // Given
      testSubscriptionsRepository.setReturnError(true)
      
      // When
      businessViewModel.getSubscriptionsBusiness()
      val result = businessViewModel.subscriptions.value
      
      // Then
      assertThat(result, `is`(emptyList()))
   }
   
   @Test
   fun getSubscriptionBusiness_whenNetworkUnavailable_thenReturnEmptyList() = runTest {
      // Given
      testNetworkMonitor.setConnected(false)
      
      // When
      businessViewModel.getSubscriptionsBusiness()
      val result = businessViewModel.subscriptions.value
      advanceUntilIdle()
      // Then
      assertThat(result, `is`(emptyList()))
      
   }
   
   
   @Test
   fun getSubscriptionBusiness_whenEmptyList_thenReturnEmptyList() = runTest {
      // Given
      testNetworkMonitor.setConnected(true)
      businessViewModel.setNetworkMonitor()
      testSubscriptionsRepository.setReturnEmpty(true)
      // When
      businessViewModel.getSubscriptionsBusiness()
      
      val result = businessViewModel.subscriptions.value
      // Then
      assertThat(result, `is`(emptyList()))
   }
   
   private fun subscriptionsFake(): List<Subscription>
   {
      return listOf(Subscription(duration = 30,
         cost = 0, type = "basic", permissions = listOf("write", "read", "admin")),
         Subscription(duration = 30,
            cost = 20, type = "pro", permissions = listOf("write", "read", "admin")),
         Subscription(duration = 90,
            cost = 60, type = "premium", permissions = listOf("write", "read", "admin")))
   }
   
   @Test
   fun setEmployee_validate_updateEmployeeList()
   {
      // Given
      val name = "mahmoud"
      val phone = "12345"
      val password = "12345m"
      val branchName = "Branch 1"
      val permission = "Admin"
      
      // When
      businessViewModel.newEmployee(name, phone, password, branchName, permission)
      businessViewModel.addEmployee()
      
      val expectedEmployee = businessViewModel.employees.getOrAwaitValue().last()
      println(expectedEmployee)
      
      // Then - Assert the expected outcomes
      assertThat(name, `is`(expectedEmployee.name))
      assertThat(phone, `is`(expectedEmployee.phoneNumber))
      assertThat(password, `is`(expectedEmployee.password))
      assertThat(branchName, `is`(expectedEmployee.branchName))
      assertThat(permission, `is`(expectedEmployee.permission))
   }
}
   

   

   
