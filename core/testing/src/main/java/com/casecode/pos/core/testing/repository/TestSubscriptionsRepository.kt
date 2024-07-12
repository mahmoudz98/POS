package com.casecode.pos.core.testing.repository

import com.casecode.pos.core.domain.repository.SubscriptionsRepository
import com.casecode.pos.core.domain.repository.SubscriptionsResource
import com.casecode.pos.core.domain.utils.EmptyType
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.subscriptions.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.junit.Before
import javax.inject.Inject

class TestSubscriptionsRepository @Inject constructor() : SubscriptionsRepository {


    private var subscriptions: List<Subscription> = subscriptionsFake()


    private var shouldReturnError = false
    private var shouldReturnEmpty = false


    @Before
    fun setup() {
        shouldReturnError = false
        shouldReturnEmpty = false
    }

    /**
     * Gets a Flow of plans.
     *
     * @return A Flow of plans.
     */
    override fun getSubscriptions(): Flow<SubscriptionsResource> = flow {

        if (shouldReturnError) {
            emit(Resource.error("Error"))
        } else if (shouldReturnEmpty) {
            emit(Resource.empty(EmptyType.DATA, "Empty"))
        } else {
            emit(Resource.success(subscriptions))
        }

    }

    fun sendSubscriptions(subscriptions: List<Subscription>) {
        this.subscriptions = subscriptions
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    fun setReturnEmpty(value: Boolean) {
        shouldReturnEmpty = value
    }

    private fun subscriptionsFake(): List<Subscription> {
        return listOf(
            Subscription(
                duration = 30,
                cost = 0, type = "basic", permissions = listOf("write", "read", "admin"),
            ),
            Subscription(
                duration = 30,
                cost = 20, type = "pro", permissions = listOf("write", "read", "admin"),
            ),
            Subscription(
                duration = 90,
                cost = 60, type = "premium", permissions = listOf("write", "read", "admin"),
            ),
        )
    }
}