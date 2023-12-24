package com.casecode.testing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.casecode.domain.usecase.GetBusinessUseCase
import com.casecode.domain.usecase.GetSubscriptionsUseCase
import com.casecode.domain.usecase.SetBusinessUseCase
import com.casecode.domain.usecase.SetEmployeesBusinessUseCase
import com.casecode.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.testing.repository.TestBusinessRepository
import com.casecode.testing.repository.TestEmployeesBusinessRepository
import com.casecode.testing.repository.TestSubscriptionsBusinessRepository
import com.casecode.testing.repository.TestSubscriptionsRepository
import com.casecode.testing.util.TestNetworkMonitor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.Extensions

@ExperimentalCoroutinesApi
abstract class BaseTest
{
   
   
   // Set the main coroutines dispatcher for unit testing.
   @get:Extensions
   var mainCoroutineRule = CoroutinesTestExtension()
   
   @Rule
   @JvmField
   val instantTaskExecutorRule = InstantTaskExecutorRule()
   lateinit var testNetworkMonitor: TestNetworkMonitor
   private lateinit var testBusinessRepository: TestBusinessRepository
    lateinit var testSubscriptionsRepository: TestSubscriptionsRepository
   private lateinit var testSubscriptionsBusinessRepository: TestSubscriptionsBusinessRepository
   private lateinit var testEmployeesBusinessRepository: TestEmployeesBusinessRepository
   
   private lateinit var getBusinessUseCase: GetBusinessUseCase
   lateinit var setBusinessUseCase: SetBusinessUseCase
   lateinit var getSubscriptionsUseCase: GetSubscriptionsUseCase
   lateinit var setSubscriptionBusinessUseCase: SetSubscriptionBusinessUseCase
   lateinit var setEmployeesBusinessUseCase: SetEmployeesBusinessUseCase
   
   @BeforeEach
   fun setup()
   {
      testNetworkMonitor = TestNetworkMonitor()
      testBusinessRepository = TestBusinessRepository()
      testSubscriptionsRepository = TestSubscriptionsRepository()
      testSubscriptionsBusinessRepository = TestSubscriptionsBusinessRepository()
      testEmployeesBusinessRepository = TestEmployeesBusinessRepository()
      
      // use cases
      getBusinessUseCase = GetBusinessUseCase(testBusinessRepository)
      setBusinessUseCase = SetBusinessUseCase(testBusinessRepository)
      getSubscriptionsUseCase = GetSubscriptionsUseCase(testSubscriptionsRepository)
      setSubscriptionBusinessUseCase =
         SetSubscriptionBusinessUseCase(testSubscriptionsBusinessRepository)
      setEmployeesBusinessUseCase = SetEmployeesBusinessUseCase(testEmployeesBusinessRepository)
      
      init()
   }
   
   abstract fun init()
   
   
   companion object
   {
      const val ERROR_MESSAGE_LIVEDATA_NULL = "LiveData has null value"
   }
}