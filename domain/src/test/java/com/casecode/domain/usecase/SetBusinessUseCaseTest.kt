package com.casecode.domain.usecase

import com.casecode.domain.model.users.Business
import com.casecode.domain.model.users.StoreType
import com.casecode.domain.utils.EmptyType
import com.casecode.domain.utils.Resource
import com.casecode.testing.repository.TestBusinessRepository
import com.casecode.testing.util.CoroutinesTestExtension
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class SetBusinessUseCaseTest
{
   
   
   // Given business and uid
   private val business = Business(StoreType.Clothes, "mahmoud@gmail.com", "1234", arrayListOf())
   private val uid = "test-uid"
   
   private  var testBusinessRepository: TestBusinessRepository = TestBusinessRepository()
   private val setBusinessUseCase: SetBusinessUseCase = SetBusinessUseCase(testBusinessRepository)
   
   @Test
   fun setBusinessUseCase_shouldAddNewBusiness_returnResourceOfSuccessTrue() = runTest {
      
      // When add new business in use case
      val addBusiness = setBusinessUseCase(business, uid)
      
      
      // Then check if result in  business repo and business use case is same.
      assertThat(addBusiness, `is`(Resource.success(true)))
   }
   
   // test with empty uid
   @Test
   fun setBusinessUseCase_emptyUid_returnEmptyUid() = runTest {
      
      // When add business and uid is empty
      val isAddBusiness = setBusinessUseCase(business, "")
      
      // Then check if result is empty uid ,
      assertThat(isAddBusiness, `is`(Resource.empty(EmptyType.DATA, "uid is empty")))
   }
   
   @Test
   fun setBusinessUseCase_emptyBusinessBranches_returnEmptyBusinessBranches() = runTest {
      
      // When add business and uid is empty
      val isAddBusiness = setBusinessUseCase(Business(), uid)
      
      // Then check if result is empty uid ,
      assertThat(isAddBusiness, `is`(Resource.empty(EmptyType.DATA, "branches is empty")))
   }
   
}