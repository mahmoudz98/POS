package com.casecode.domain.usecase

import com.casecode.domain.repository.SubscriptionsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSubscriptionsUseCase @Inject constructor(private val subscriptionsRep: SubscriptionsRepository) {
    operator fun invoke() = subscriptionsRep.getSubscriptions()
}

