package com.casecode.testing.repository

import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.repository.SubscriptionsRepository
import com.casecode.domain.utils.Resource
import com.casecode.testing.util.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach

class TestSubscriptionsRepository : SubscriptionsRepository
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
   override fun getSubscriptions(): Flow<Resource<List<Subscription>>> = callbackFlow {
      // Return a Flow of fake plans, depending on the edge case
      if (shouldReturnError)
      {
         trySendBlocking(Resource.Error(Exception()))
      } else if (shouldReturnEmpty)
      {
         trySendBlocking(Resource.Empty(null))
      } else
      {
         trySendBlocking(Resource.Success(subscriptions))
      }
      close()
   }.flowOn(Dispatchers.IO)
   
   fun sendPlans(subscriptions: List<Subscription>)
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
   
   
}