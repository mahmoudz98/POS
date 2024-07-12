package com.casecode.pos.core.domain.repository

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.subscriptions.Subscription
import kotlinx.coroutines.flow.Flow

typealias SubscriptionsResource = Resource<List<Subscription>>

interface SubscriptionsRepository {
    fun getSubscriptions(): Flow<SubscriptionsResource>
}