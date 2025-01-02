/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.data.repository

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.casecode.pos.core.testing.repository.TestSubscriptionsRepository
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
    fun getSubscriptions_shouldReturnListOfSubscriptions_whenSuccessful() = runTest {
        // Given
        val actualSubscriptions = subscriptionsFake()

        // when and  send some test Subscriptions and get followed state
        repository.sendSubscriptions(actualSubscriptions)
        val expectedSubscriptions = repository.getSubscriptions()

        // Then
        assertThat(expectedSubscriptions, equalTo(Resource.Success(actualSubscriptions)))
    }

    /**
     * A test that verifies that the getSubscriptions() method returns an error when there are errors.
     */
    @Test
    fun getSubscriptions_shouldReturnError_whenErrors() = runTest {
        // Given
        repository.setReturnError(true)

        // when  send some test error and get followed state
        val actualError = repository.getSubscriptions()

        // Then
        assert(actualError is Resource.Error)
    }

    /**
     * A test that verifies that the getSubscriptions() method returns an empty list when there are no plans.
     */
    @Test
    fun getSubscriptions_shouldReturnEmptyList() = runTest {
        // When send plans is empty
        repository.setReturnEmpty(true)
        val subscriptionsResponse = repository.getSubscriptions()

        // Then
        assertThat(subscriptionsResponse, equalTo(Resource.empty("Empty")))
    }

    private fun subscriptionsFake(): List<Subscription> = listOf(
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