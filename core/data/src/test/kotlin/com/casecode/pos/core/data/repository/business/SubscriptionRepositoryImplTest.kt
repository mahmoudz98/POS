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
package com.casecode.pos.core.data.repository.business

import com.casecode.pos.core.firebase.model.NetworkPlanLimits
import com.casecode.pos.core.firebase.model.NetworkSubscription
import com.casecode.pos.core.firebase.model.NetworkSubscriptionPlan
import com.casecode.pos.core.model.business.BillingEvent
import com.casecode.pos.core.model.business.BillingEventType
import com.casecode.pos.core.testing.datasource.TestConfigDataSource
import com.casecode.pos.core.testing.datasource.TestSubscriptionNetworkDataSource
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock

class SubscriptionRepositoryImplTest {

    @get:Rule
    val coroutinesRule = CoroutinesTestRule()

    private lateinit var networkDataSource: TestSubscriptionNetworkDataSource
    private lateinit var configDataSource: TestConfigDataSource
    private lateinit var logService: TestLogService
    private lateinit var repository: SubscriptionRepositoryImpl

    @Before
    fun setup() {
        networkDataSource = TestSubscriptionNetworkDataSource()
        configDataSource = TestConfigDataSource()
        logService = TestLogService()
        repository = SubscriptionRepositoryImpl(
            networkDataSource = networkDataSource,
            configDataSource = configDataSource,
            logService = logService,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun getOnboardingConfig_success_returnsConfig() = runTest {
        // Given
        val plan = NetworkSubscriptionPlan(
            id = "id1",
            nameEn = "name1",
            nameAr = "اسم1",
            price = 10.0,
            isFree = false,
            featuresEn = listOf("feat1"),
            featuresAr = listOf("مميزة1"),
            limits = NetworkPlanLimits(1, 5, 100, 10),
        )
        configDataSource.setSubscriptionPlans(listOf(plan))

        // When
        val result = repository.getOnboardingConfig()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.plans?.size)
    }

    @Test
    fun getCurrentSubscription_returnsSubscription() = runTest {
        // Given
        val networkSub = NetworkSubscription("biz1", "plan1", creditBalance = 100)
        networkDataSource.setSubscription(networkSub)

        // When
        val result = repository.getCurrentSubscription("biz1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(100, result.getOrNull()?.creditBalance)
    }

    @Test
    fun purchaseCredits_success_updatesBalance() = runTest {
        // Given
        networkDataSource.setSubscription(
            NetworkSubscription(
                "biz1",
                "plan1",
                creditBalance = 1000,
            ),
        )
        val event = BillingEvent(
            id = "id1",
            eventType = BillingEventType.CHARGE_SUCCESSFUL,
            description = "desc",
            amount = 50.0,
            currencyCode = "USD",
            creditsChange = 500,
            eventDate = Clock.System.now(),
            paymentProviderTransactionId = "tx1",
        )

        // When
        val result = repository.purchaseCredits("biz1", event, 500L)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, networkDataSource.updateCreditBalanceCallCount)
    }
}
