package com.casecode.pos.core.testing.repository

import com.casecode.pos.core.domain.repository.AddSubscriptionBusiness
import com.casecode.pos.core.domain.repository.SubscriptionsBusinessRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.SubscriptionBusiness
import kotlinx.coroutines.flow.Flow
import org.junit.Before
import javax.inject.Inject

class TestSubscriptionsBusinessRepository @Inject constructor() : SubscriptionsBusinessRepository {

    private var shouldReturnError = false
    private var shouldReturnEmpty = false

    @Before
    fun setup() {
        shouldReturnError = false
        shouldReturnEmpty = false
    }

    override suspend fun setSubscriptionBusiness(
        subscriptionBusiness: SubscriptionBusiness,

        ): AddSubscriptionBusiness {
        return if (shouldReturnError) {
            Resource.Error("Exception")
        } else
            Resource.Success(true)
    }

    override fun getSubscriptionsBusiness(): Flow<Resource<List<SubscriptionBusiness>>> {
        TODO("Not yet implemented")
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }


}