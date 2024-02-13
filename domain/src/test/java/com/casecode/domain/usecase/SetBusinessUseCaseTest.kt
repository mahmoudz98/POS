package com.casecode.domain.usecase

import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.Business
import com.casecode.domain.model.users.StoreType
import com.casecode.domain.repository.AddBusiness
import com.casecode.domain.utils.EmptyType
import com.casecode.domain.utils.Resource
import com.casecode.pos.domain.R
import com.casecode.testing.repository.TestBusinessRepository
import com.casecode.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

class SetBusinessUseCaseTest
{
   
   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()
   
   // Given business and uid
   private val business = Business(StoreType.Clothes, "mahmoud@gmail.com", "1234",false,
      listOf(Branch(1, "22", "2323")))
   private val uid = "test-uid"
   
   private var testBusinessRepository: TestBusinessRepository = TestBusinessRepository()
   private val setBusinessUseCase: SetBusinessUseCase = SetBusinessUseCase(testBusinessRepository)
   
   @Test
   fun `invoke with valid data should add new business and return Resource of success true`() = runTest {
      
      // When add new business in use case
      val addBusiness = setBusinessUseCase(business, uid)
      
      
      // Then check if result in  business repo and business use case is same.
      assertThat(addBusiness, `is`(Resource.success(true)))
   }
   
   
   
   @Test
   fun `invoke with empty UID return resource with UID empty`() = runTest {
      
      // When add business and uid is empty
      val isAddBusiness = setBusinessUseCase(Business(), "")
      
      // Then check if result is empty uid ,
      assertThat(isAddBusiness, `is`(Resource.empty(EmptyType.DATA, R.string.uid_empty)))
   }
   
   @Test
   fun `invoke with empty business branches should return Resource with BRANCHES_EMPTY error`() = runTest {
      
      // When add business and business branches are empty
      val emptyBranchBusiness = Business()
      
      // Then check if the result is empty business branches,
      val isAddBusiness: AddBusiness = setBusinessUseCase(emptyBranchBusiness, uid)
      assertThat(isAddBusiness, `is`(Resource.empty(EmptyType.DATA, R.string.branches_empty)))
   }
   
   @Test
   fun `invoke with empty phone should return Resource with PHONE_BUSINESS_EMPTY error`() = runTest {
      
      // When add business and phone is empty
      val businessWithEmptyPhone = Business(StoreType.Clothes, "mahmoud@gmail.com", "",false, listOf(Branch()))
      
      // Then check if the result is empty phone,
      val isAddBusiness: AddBusiness = setBusinessUseCase(businessWithEmptyPhone, uid)
      assertThat(isAddBusiness, `is`(Resource.empty(EmptyType.DATA, R.string.phone_business_empty)))
   }
   
   @Test
   fun `invoke with empty email should return Resource with EMAIL_BUSINESS_EMPTY error`() = runTest {
      
      // When add business and email is empty
      val businessWithEmptyEmail = Business(StoreType.Clothes, "", "1234",false, listOf(Branch()))
      
      // Then check if the result is empty email,
      val isAddBusiness: AddBusiness = setBusinessUseCase(businessWithEmptyEmail, uid)
      assertThat(isAddBusiness, `is`(Resource.empty(EmptyType.DATA, R.string.email_business_empty)))
   }
   
}