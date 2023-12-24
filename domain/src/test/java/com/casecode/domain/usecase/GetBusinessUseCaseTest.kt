package com.casecode.domain.usecase

import com.casecode.domain.model.users.Business
import com.casecode.domain.model.users.StoreType
import com.casecode.domain.repository.BusinessRepository
import com.casecode.testing.repository.TestBusinessRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetBusinessUseCaseTest
{
   
   // Given business and uid
   private val business = Business(StoreType.Clothes, "mahmoud@gmail.com", "1234")
   private val uid = "test-uid"
   
   // subject under test
   private lateinit var testBusinessRepository: TestBusinessRepository
   private lateinit var getBusinessUseCase: GetBusinessUseCase
   

   

}