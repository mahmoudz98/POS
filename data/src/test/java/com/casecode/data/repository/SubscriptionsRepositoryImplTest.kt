package com.casecode.data.repository

import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.utils.Resource
import com.casecode.testing.repository.TestSubscriptionsRepository
import com.casecode.testing.util.CoroutinesTestExtension
import com.casecode.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


/**
 * A JUnit test class for the SubscriptionsRepositoryImpl class.
 *
 * This class uses the  and CoroutinesTestExtension to ensure that
 * all asynchronous tasks are executed immediately.
 */
@ExtendWith(CoroutinesTestExtension::class)
class SubscriptionsRepositoryImplTest
{
   /**
    * A test  SubscriptionsRepository implementation that can be used for testing.
    */
   private val repository = TestSubscriptionsRepository()
   
   /**
    * A JUnit rule that sets the main dispatcher to a TestCoroutineDispatcher.
    */
   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()
   
   
   /**
    * A test that verifies that the getSubscriptions() method returns a list of plans when successful.
    */
   @Test
   fun getSubscriptions_shouldReturnListOfSubscriptions_whenSuccessful() = runTest {
      // Given
      val actualSubscriptions = listOf(Subscription(duration = 30,
         cost = 0, type = "basic", permissions = listOf("write", "read", "admin")),
         Subscription(duration = 30,
            cost = 20, type = "pro", permissions = listOf("write", "read", "admin")),
         Subscription(duration = 90,
            cost = 60, type = "premium", permissions = listOf("write", "read", "admin")))
      
      
      // when and  send some test Subscriptions and get followed state
      repository.sendPlans(actualSubscriptions)
      val expectedSubscriptions = repository.getSubscriptions().last()
      
      // Then
      assertThat(expectedSubscriptions, equalTo(Resource.Success(actualSubscriptions)))
      
   }
   
   /**
    * A test that verifies that the getSubscriptions() method returns an error when there are errors.
    */
   @Test
   fun getSubscriptions_shouldReturnError_whenErrors() = runTest {
      
      // when  send some test error and get followed state
      repository.setReturnError(true)
      val actualError = repository.getSubscriptions().first()
      
      // Then
      assert(actualError is Resource.Error)
      
   }
   
   /**
    * A test that verifies that the getSubscriptions() method returns an empty list when there are no plans.
    */
   @Test
   fun getSubscriptions_shouldReturnEmptyList()
   {
      runTest {
         // When send plans is empty
         repository.setReturnEmpty(true)
         val plansResponse = repository.getSubscriptions().first()
         
         // Then
         assertThat(plansResponse, equalTo(Resource.Empty()))
      }
   }
   
}