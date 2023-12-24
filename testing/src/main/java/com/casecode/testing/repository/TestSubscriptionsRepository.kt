package com.casecode.testing.repository

import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.repository.SubscriptionsRepository
import com.casecode.domain.repository.SubscriptionsResource
import com.casecode.domain.utils.EmptyType
import com.casecode.domain.utils.Resource
import com.casecode.testing.util.EspressoIdlingResource
import com.casecode.testing.util.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestSubscriptionsRepository @Inject constructor() : SubscriptionsRepository
{
   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()
   
   
   private var subscriptions: List<Subscription> = mutableListOf()
   
   
   private var shouldReturnError = false
   private var shouldReturnEmpty = false
   
   
   @BeforeEach
   fun setup()
   {
      shouldReturnError = false
      shouldReturnEmpty = false
   }
   
   /**
    * Gets a Flow of plans.
    *
    * @return A Flow of plans.
    */
   override fun getSubscriptions(): Flow<SubscriptionsResource> = callbackFlow {
      EspressoIdlingResource.wrapEspressoIdlingResource {
         // Return a Flow of fake plans, depending on the edge case
         if (shouldReturnError)
         {
            trySend(Resource.error("Error"))
         } else if (shouldReturnEmpty)
         {
            trySend(Resource.empty(EmptyType.DATA, "Empty"))
         } else
         {
            trySend(Resource.success(subscriptions))
         }
         close()
      }
   }.flowOn(Dispatchers.IO)
   
   fun sendSubscriptions(subscriptions: List<Subscription>)
   {
      this.subscriptions = subscriptions
   }
   
   fun setReturnError(value: Boolean)
   {
      shouldReturnError = value
   }
   
   fun setReturnEmpty(value: Boolean)
   {
      shouldReturnEmpty = value
   }
   fun subscriptionsFake(): List<Subscription>
   {
      return listOf(Subscription(duration = 30,
         cost = 0, type = "basic", permissions = listOf("write", "read", "admin")),
         Subscription(duration = 30,
            cost = 20, type = "pro", permissions = listOf("write", "read", "admin")),
         Subscription(duration = 90,
            cost = 60, type = "premium", permissions = listOf("write", "read", "admin")))
   }
}
