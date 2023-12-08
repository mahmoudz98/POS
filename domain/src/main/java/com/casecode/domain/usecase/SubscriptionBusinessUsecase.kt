package com.casecode.domain.usecase

import com.casecode.domain.model.users.SubscriptionBusiness
import com.casecode.domain.repository.SubscriptionsBusinessRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSubscriptionBusinessUseCase @Inject constructor(private val subscriptionsRep: SubscriptionsBusinessRepository)
{
   operator fun invoke() = subscriptionsRep.getSubscriptionsBusiness()
}
@Singleton
class SetSubscriptionBusinessUseCase @Inject constructor(private val subscriptionsRep: SubscriptionsBusinessRepository)
{
   suspend operator fun invoke(subscriptionBusiness: SubscriptionBusiness, uid: String) = subscriptionsRep.setSubscriptionBusiness(subscriptionBusiness, uid)
}