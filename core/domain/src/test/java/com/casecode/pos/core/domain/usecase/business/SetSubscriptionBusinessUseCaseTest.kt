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

import com.casecode.pos.core.domain.R
import com.casecode.pos.core.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.SubscriptionBusiness
import com.casecode.pos.core.testing.repository.TestSubscriptionsBusinessRepository
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class SetSubscriptionBusinessUseCaseTest {
    @get:Rule
    val coroutineTestRule = CoroutinesTestRule()

    // Given uid and subscription
    private val subscription: SubscriptionBusiness =
        SubscriptionBusiness(type = "Pro", cost = 20L, duration = 60, listOf("admin"))

    // subject under test
    private val testSubscriptionsBusinessRepository: TestSubscriptionsBusinessRepository =
        TestSubscriptionsBusinessRepository()
    private val setSubscriptionBusinessUseCase: SetSubscriptionBusinessUseCase =
        SetSubscriptionBusinessUseCase(testSubscriptionsBusinessRepository)

    @Test
    fun setSubscriptionBusinessUseCase_shouldAddNewSubscriptionBusiness_returnTrue() =
        runTest {
            // When
            val resultIsAddSubscriptionBusiness = setSubscriptionBusinessUseCase(subscription)

            // Then
            val isAddSubscriptionBusiness = Resource.success(true)
            assertEquals(isAddSubscriptionBusiness, (resultIsAddSubscriptionBusiness))
        }

    @Test
    fun setSubscriptionBusinessUseCase_emptyBusiness_returnEmptyTypeOfSubscription() =
        runTest {
            // When subscription business fields is empty
            val resultEmptySubscriptionBusiness =
                setSubscriptionBusinessUseCase(SubscriptionBusiness())

            // Then - return Resource of empty data
            assertEquals(
                resultEmptySubscriptionBusiness,
                (Resource.empty(R.string.core_domain_add_subscription_business_empty)),
            )
        }
}