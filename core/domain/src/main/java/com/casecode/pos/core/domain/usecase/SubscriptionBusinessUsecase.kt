package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.repository.AddSubscriptionBusiness
import com.casecode.pos.core.domain.repository.SubscriptionsBusinessRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.SubscriptionBusiness
import javax.inject.Inject

class GetSubscriptionBusinessUseCase
    @Inject
    constructor(
        private val subscriptionsRep: SubscriptionsBusinessRepository,
    ) {
        operator fun invoke() = subscriptionsRep.getSubscriptionsBusiness()
    }

class SetSubscriptionBusinessUseCase
    @Inject
    constructor(
        private val subscriptionsRep: SubscriptionsBusinessRepository,
    ) {
        suspend operator fun invoke(subscriptionBusiness: SubscriptionBusiness): AddSubscriptionBusiness {
            if (subscriptionBusiness.type.isNullOrEmpty()) {
                return Resource.empty(R.string.add_subscription_business_empty)
            }

            return subscriptionsRep.setSubscriptionBusiness(subscriptionBusiness)
    }
}