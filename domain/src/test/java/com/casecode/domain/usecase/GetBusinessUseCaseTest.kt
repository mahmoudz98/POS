package com.casecode.domain.usecase

import com.casecode.domain.entity.Business
import com.casecode.domain.entity.StoreType
import com.casecode.domain.repository.BusinessRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetBusinessUseCaseTest{
   
   // Given business and uid
   private val business = Business(StoreType.Clothes, "mahmoud@gmail.com", "1234")
   private val uid = "test-uid"
   
   
   private val businessRepository = mockk<BusinessRepository>()
   
   private val businessUseCase = GetBusinessUseCase(businessRepository)
   
   @BeforeEach
   fun init(){
      clearAllMocks()
   }
   
   @Test
   fun getBusiness_should_ReturnBusiness_whenBusinessExists() = runTest {
      
      coEvery{businessRepository.getBusiness(uid)}.returns(business)
      // when load business
      val retrieveBusiness = businessUseCase(uid)
      
      // Then the result of business is same.
      MatcherAssert.assertThat(retrieveBusiness, IsEqual(business))
      
   }
}