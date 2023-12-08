package com.casecode.domain.usecase

import com.casecode.domain.model.users.Business
import com.casecode.domain.model.users.StoreType
import com.casecode.testing.repository.TestBusinessRepository
import com.casecode.testing.util.CoroutinesTestExtension
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutinesTestExtension::class)
class SetBusinessUseCaseTest
{
   
   
   // Given business and uid
   private val business = Business(StoreType.Clothes, "mahmoud@gmail.com", "1234", arrayListOf())
   private val uid = "test-uid"
   
   
   private lateinit var businessRepository: TestBusinessRepository
   
   private lateinit var setBusinessUseCase: SetBusinessUseCase
   
   @BeforeEach
   fun setup()
   {
      businessRepository = TestBusinessRepository()
      setBusinessUseCase = SetBusinessUseCase(businessRepository)
   }
   
   @Test
   fun setBusinessUseCase_shouldAddNewBusiness_returnTrue() = runTest {
      
      // When add new business in use case
      val addBusiness = setBusinessUseCase(business, uid)
      
      // send some test business and get result from repo
      // businessRepository.sendAddBusiness(true)
      val result = businessRepository.setBusiness(business, uid)
      
      // Then check if result in  business repo and business use case is same.
      assertThat(addBusiness, `is`(result))
   }
   
   
}