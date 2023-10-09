package com.casecode.pos.viewmodel

import com.casecode.domain.entity.Branch
import com.casecode.domain.entity.StoreType
import com.casecode.domain.utils.Resource
import com.casecode.pos.InstantTaskExecutorExtension
import com.casecode.pos.R
import com.casecode.testing.BaseTest
import com.casecode.testing.CoroutinesTestExtension
import com.casecode.testing.util.getOrAwaitValue
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.ArrayList

@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class)
class BusinessViewModelTest : BaseTest()
{
   
   
   // subject under test
   private lateinit var businessViewModel: BusinessViewModel
   
   override fun init()
   {
      businessViewModel = BusinessViewModel(setBusinessUseCase, getPlanUseCase)
   }
   
   @Test
   
   fun addBusiness_shouldReturnSuccessBusiness() = runTest {
      // Given
      val storeType = StoreType.Clothes.toString()
      val email = "test@email.com"
      val phone = "1234567890"
      val branches =
         arrayListOf(Branch(1, "Branch 1", "1234567890"), Branch(2, "Branch 2", "9876543210"))
      
      // When - add new business
      businessViewModel.setStoreType(storeType)
      businessViewModel.setEmail(email)
      businessViewModel.setPhone(phone)
      businessViewModel.branches.value = branches
      businessViewModel.setCurrentUid("test")
      businessViewModel.setBusiness()
      
      var isSuccess: Resource.Success<Boolean>? = Resource.Success(false)
      val job = launch {
         isSuccess = businessViewModel.addBusiness.value as Resource.Success<Boolean>
      }
      
      // Execute pending coroutines actions
      advanceUntilIdle()
      
      // Then -
      assertThat(isSuccess, equalTo(Resource.Success(true)))
      job.cancel()
   }
   
   @Test
   fun setBusiness_whenStoreTypeArabic_returnStoreTypeEnglish()
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
      assertThat(R.string.add_branch_success, `is`(businessViewModel.userMessage.getOrAwaitValue()))
      assertThat(businessViewModel.isAddBranch.value?.peekContent(), `is`(true))
   }
   
   @Test
   fun updateBranch_whenChangeName_returnUpdateBranchIsTrueAndSuccess()
   {
      // Given
      val branch = Branch(1, "branch1", "1234")
     businessViewModel.branches.value = arrayListOf(branch)
      businessViewModel.setBranchSelected(branch )
      businessViewModel.setBranchName("branch2")
      
      // When
      businessViewModel.setUpdateBranch()
      
      // Then
      assertThat(R.string.update_branch_success, `is`(businessViewModel.userMessage.getOrAwaitValue()))
      assertThat(businessViewModel.isUpdateBranch.value?.peekContent(), `is`(true))
      
   }
   
   @Test
   fun updateBranch_whenNotChange_returnUpdateBranchIsFalseAndFailed()
   {
      // Given
      val branch = Branch(1, "branch1", "1234")
      businessViewModel.branches.value = arrayListOf(branch)
      businessViewModel.setBranchSelected(branch)
      businessViewModel.setBranchName(branch.branchName!!)
      businessViewModel.setBranchPhone(branch.phoneNumber!!)
      
      // When
      businessViewModel.setUpdateBranch()
      
      // Then
      assertThat(R.string.update_branch_fail, `is`(businessViewModel.userMessage.getOrAwaitValue()))
      assertThat(businessViewModel.isUpdateBranch.value?.peekContent(), `is`(false))
      
   }
   
   @Test
   fun `updateBranch when branches is out of index return failed and false`(){
      // Given
      val branch = Branch(1, "branch1", "1234")
      businessViewModel.setBranchSelected(branch)
      
      // When
      businessViewModel.setUpdateBranch()
      
      // Then
      assertThat(R.string.update_branch_fail, `is`(businessViewModel.userMessage.getOrAwaitValue()))
      assertThat(businessViewModel.isUpdateBranch.value?.peekContent(), `is`(false))
      
      
   }
}
   

   

   
