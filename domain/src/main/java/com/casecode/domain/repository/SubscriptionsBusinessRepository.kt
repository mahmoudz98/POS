package com.casecode.domain.repository

import com.casecode.domain.model.users.SubscriptionBusiness
import com.casecode.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

typealias AddSubscriptionBusiness = Resource<Boolean>

@Singleton
 interface SubscriptionsBusinessRepository
{
   suspend fun setSubscriptionBusiness(subscriptionBusiness: SubscriptionBusiness, uid: String): AddSubscriptionBusiness
   fun getSubscriptionsBusiness(): Flow<Resource<List<SubscriptionBusiness>>>
   
}