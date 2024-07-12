package com.casecode.pos.core.domain.repository

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.SubscriptionBusiness
import kotlinx.coroutines.flow.Flow

typealias AddSubscriptionBusiness = Resource<Boolean>

interface SubscriptionsBusinessRepository {
    suspend fun setSubscriptionBusiness(
        subscriptionBusiness: SubscriptionBusiness,
    ): AddSubscriptionBusiness

    fun getSubscriptionsBusiness(): Flow<Resource<List<SubscriptionBusiness>>>

}