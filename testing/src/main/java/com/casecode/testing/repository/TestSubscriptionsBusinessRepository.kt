package com.casecode.testing.repository

import com.casecode.domain.model.users.SubscriptionBusiness
import com.casecode.domain.repository.AddSubscriptionBusiness
import com.casecode.domain.repository.SubscriptionsBusinessRepository
import com.casecode.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import org.junit.Before
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestSubscriptionsBusinessRepository @Inject constructor() : SubscriptionsBusinessRepository
{
   
   private var shouldReturnError = false
   private var shouldReturnEmpty = false
   
   @Before
   fun setup()
   {
      shouldReturnError = false
      shouldReturnEmpty = false
   }
   
   override suspend fun setSubscriptionBusiness(
        subscriptionBusiness: SubscriptionBusiness,
        uid: String,
                                               ): AddSubscriptionBusiness
   {
      return if (shouldReturnError)
      {
         Resource.Error("Exception")
      } else
         Resource.Success(true)
   }
   
   override fun getSubscriptionsBusiness(): Flow<Resource<List<SubscriptionBusiness>>>
   {
      TODO("Not yet implemented")
   }
   
   fun setReturnError(value: Boolean)
   {
      shouldReturnError = value
   }
   
   
}