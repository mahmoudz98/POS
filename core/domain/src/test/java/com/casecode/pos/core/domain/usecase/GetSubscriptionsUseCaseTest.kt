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
package com.casecode.pos.core.domain.usecase

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
    fun `getSubscriptionsUseCase return resource success of subscriptions`() =
        runTest {
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
    fun getSubscriptionsUseCaseWhenResourceEmptyReturnEmpty() =
        runTest {
            // Given - send empty
            testSubscriptionsRepository.setReturnEmpty(true)
            val exceptedResultEmpty = Resource.empty<Boolean>("Empty")

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