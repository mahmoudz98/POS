package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.utils.EmptyType
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.casecode.pos.core.testing.repository.TestSubscriptionsRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test


class GetSubscriptionsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testSubscriptionsRepository = TestSubscriptionsRepository()
    private val getSubscriptionsUseCase = GetSubscriptionsUseCase(testSubscriptionsRepository)


    @Test
    fun `getSubscriptionsUseCase return resource success of subscriptions`() = runTest {
        // Given
        val exceptedResultSuccess = Resource.success(subscriptionsFake())
        testSubscriptionsRepository.sendSubscriptions(subscriptionsFake())

        // When
        val result = getSubscriptionsUseCase()
        val resultValueSuccess = result.first()
        // Then
        assertThat(exceptedResultSuccess, `is`(resultValueSuccess))

    }

    @Test
    fun getSubscriptionsUseCaseWhenResourceEmptyReturnEmpty() = runTest {
        // Given - send empty
        testSubscriptionsRepository.setReturnEmpty(true)
        val exceptedResultEmpty = Resource.empty<Boolean>(EmptyType.DATA, "Empty")

        // when
        val result = getSubscriptionsUseCase()
        val resultEmptyValue = result.first()

        // Then
        assertThat(resultEmptyValue, `is`(exceptedResultEmpty))
    }


    private fun subscriptionsFake(): List<Subscription> =
        mutableListOf(
            Subscription(0, 30, listOf("admin", "non"), "basic"),
            Subscription(20, 30, listOf("admin", "non"), "Pro"),
            Subscription(60, 60, listOf("admin", "non"), "premium"),
        )

}