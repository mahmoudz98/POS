package com.casecode.data.repository

import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.casecode.pos.core.domain.utils.EmptyType
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.testing.repository.TestSubscriptionsRepository
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

/**
 * A JUnit test class for the [SubscriptionsRepositoryImpl] class.
 *
 * This class uses the  and CoroutinesTestExtension to ensure that
 * all asynchronous tasks are executed immediately.
 */
class SubscriptionsRepositoryImplTest {
    private var firestore: FirebaseFirestore = mockk<FirebaseFirestore>()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope: TestScope = TestScope(testDispatcher)

    /**
     * A test  SubscriptionsRepository implementation that can be used for testing.
     */
    private val repository = TestSubscriptionsRepository()

    /**
     * A test that verifies that the getSubscriptions() method returns a list of plans when successful.
     */
    @Test
    fun getSubscriptions_shouldReturnListOfSubscriptions_whenSuccessful() =
        runTest {
            // Given
            val actualSubscriptions = subscriptionsFake()

            // when and  send some test Subscriptions and get followed state
            repository.sendSubscriptions(actualSubscriptions)
            val expectedSubscriptions = repository.getSubscriptions().last()

            // Then
            assertThat(expectedSubscriptions, equalTo(Resource.Success(actualSubscriptions)))
        }

    /**
     * A test that verifies that the getSubscriptions() method returns an error when there are errors.
     */
    @Test
    fun getSubscriptions_shouldReturnError_whenErrors() =
        runTest {
            // Given
            repository.setReturnError(true)

            // when  send some test error and get followed state
            val actualError = repository.getSubscriptions().first()

            // Then
            assert(actualError is Resource.Error)
        }

    /**
     * A test that verifies that the getSubscriptions() method returns an empty list when there are no plans.
     */
    @Test
    fun getSubscriptions_shouldReturnEmptyList() =
        runTest {
            // When send plans is empty
            repository.setReturnEmpty(true)
            val subscriptionsResponse = repository.getSubscriptions().first()

            // Then
            assertThat(subscriptionsResponse, equalTo(Resource.empty(EmptyType.DATA, "Empty")))
        }

    private fun subscriptionsFake(): List<Subscription> {
        return listOf(
            Subscription(
                duration = 30,
                cost = 0,
                type = "basic",
                permissions = listOf("write", "read", "admin"),
            ),
            Subscription(
                duration = 30,
                cost = 20,
                type = "pro",
                permissions = listOf("write", "read", "admin"),
            ),
            Subscription(
                duration = 90,
                cost = 60,
                type = "premium",
                permissions = listOf("write", "read", "admin"),
            ),
        )
    }
}