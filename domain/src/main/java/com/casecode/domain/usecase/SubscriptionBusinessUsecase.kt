package com.casecode.domain.usecase

import com.casecode.domain.model.users.SubscriptionBusiness
import com.casecode.domain.repository.AddSubscriptionBusiness
import com.casecode.domain.repository.SubscriptionsBusinessRepository
import com.casecode.domain.utils.EmptyType
import com.casecode.domain.utils.Resource
import javax.inject.Inject

class GetSubscriptionBusinessUseCase @Inject constructor(private val subscriptionsRep: SubscriptionsBusinessRepository) {
    operator fun invoke() = subscriptionsRep.getSubscriptionsBusiness()
}

class SetSubscriptionBusinessUseCase @Inject constructor(private val subscriptionsRep: SubscriptionsBusinessRepository) {
    suspend operator fun invoke(
        subscriptionBusiness: SubscriptionBusiness,
        uid: String
    ): AddSubscriptionBusiness {
        if (uid.isEmpty()) {
            return Resource.empty(EmptyType.DATA, "uid is empty")
        }
        if (subscriptionBusiness.type.isEmpty()) {
            return Resource.empty(EmptyType.DATA, "Subscription business type is empty")
        }

        return subscriptionsRep.setSubscriptionBusiness(subscriptionBusiness, uid)
    }
}