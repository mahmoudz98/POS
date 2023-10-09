package com.casecode.domain.repository

import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

typealias Subscriptions = List<Subscription>
typealias PlansResponse = Resource<Subscriptions>
@Singleton
 interface SubscriptionsRepository {
    fun getSubscriptions(): Flow<Resource<List<Subscription>>>
}