package com.casecode.domain.repository

import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

typealias SubscriptionsResource = Resource<List<Subscription>>
@Singleton
 interface SubscriptionsRepository {
    fun getSubscriptions(): Flow<SubscriptionsResource>
}