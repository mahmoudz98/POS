package com.casecode.domain.usecase

import com.casecode.domain.repository.SubscriptionsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSubscriptionsUseCase @Inject constructor(private val plansRepo: SubscriptionsRepository) {
    operator fun  invoke() = plansRepo.getSubscriptions()
}