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
package com.casecode.pos.core.domain.usecase.business

import com.casecode.pos.core.domain.usecase.GetSubscriptionsUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.casecode.pos.core.testing.repository.TestSubscriptionsRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetSubscriptionsUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testSubscriptionsRepository = TestSubscriptionsRepository()
    private val getSubscriptionsUseCase = GetSubscriptionsUseCase(testSubscriptionsRepository)

    @Test
    fun `getSubscriptionsUseCase return resource success of subscriptions`() =
        runTest {
            // 1- Given
            val expectedSubscriptions = subscriptionsFake()
            testSubscriptionsRepository.sendSubscriptions(expectedSubscriptions)

            // 2- When
            val actualSubscriptions = getSubscriptionsUseCase()

            // 3- Then
            assertEquals(
                expected = Resource.success(expectedSubscriptions),
                actual = actualSubscriptions,
            )
        }

    @Test
    fun `getSubscriptionsUseCase when no subscriptions available returns Resource Empty`() =
        runTest {
            // 1- Given
            testSubscriptionsRepository.setReturnEmpty(true)

            // 2- When
            val actualSubscriptions = getSubscriptionsUseCase()

            // 3- Then
            assert(actualSubscriptions is Resource.Empty)
        }

    private fun subscriptionsFake(): List<Subscription> =
        mutableListOf(
            Subscription(0, 30, listOf("admin", "non"), "basic"),
            Subscription(20, 30, listOf("admin", "non"), "Pro"),
            Subscription(60, 60, listOf("admin", "non"), "premium"),
        )
}