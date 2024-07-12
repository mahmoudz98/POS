package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.repository.SubscriptionsRepository
import com.casecode.pos.core.domain.repository.SubscriptionsResource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubscriptionsUseCase @Inject constructor(private val subscriptionsRep: SubscriptionsRepository) {
    operator fun invoke(): Flow<SubscriptionsResource> {
        return subscriptionsRep.getSubscriptions()
    }
}