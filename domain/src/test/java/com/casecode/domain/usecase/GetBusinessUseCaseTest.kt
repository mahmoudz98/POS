package com.casecode.domain.usecase

import com.casecode.domain.model.users.Business
import com.casecode.domain.model.users.StoreType
import com.casecode.testing.repository.TestBusinessRepository

class GetBusinessUseCaseTest
{
   
   // Given business and uid
   private val business = Business(StoreType.Clothes, "mahmoud@gmail.com", "1234")
   private val uid = "test-uid"
   
   // subject under test
   private lateinit var testBusinessRepository: TestBusinessRepository
   private lateinit var getBusinessUseCase: GetBusinessUseCase
   
   
}