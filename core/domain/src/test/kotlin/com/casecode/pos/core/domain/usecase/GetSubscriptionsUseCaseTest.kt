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
import com.casecode.pos.core.model.subscriptions.Subscription
import com.casecode.pos.core.testing.repository.TestSubscriptionsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetSubscriptionsUseCaseTest {
    private lateinit var testSubscriptionsRepository: TestSubscriptionsRepository
    private lateinit var getSubscriptionsUseCase: GetSubscriptionsUseCase

    @Before
    fun setup() {
        testSubscriptionsRepository = TestSubscriptionsRepository()
        getSubscriptionsUseCase = GetSubscriptionsUseCase(testSubscriptionsRepository)
    }

    @Test
    fun subscriptionsAvailable_ReturnsSubscriptions() = runTest {
        val subscriptions = listOf(
            Subscription(duration = 30, cost = 10, type = "Basic", permissions = emptyList()),
        )
        testSubscriptionsRepository.sendSubscriptions(subscriptions)

        val result = getSubscriptionsUseCase()

        assertTrue(result is Resource.Success)
        assertEquals(subscriptions, (result as Resource.Success).data)
    }

    @Test
    fun repositoryError_ReturnsError() = runTest {
        testSubscriptionsRepository.setReturnError(true)

        val result = getSubscriptionsUseCase()

        assertTrue(result is Resource.Error)
    }

    @Test
    fun emptyRepository_ReturnsEmpty() = runTest {
        testSubscriptionsRepository.setReturnEmpty(true)

        val result = getSubscriptionsUseCase()

        assertTrue(result is Resource.Empty)
    }
}
