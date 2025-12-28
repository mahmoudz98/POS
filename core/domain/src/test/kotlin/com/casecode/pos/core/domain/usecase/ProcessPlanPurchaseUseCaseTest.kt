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

import com.casecode.pos.core.model.business.PlanLimits
import com.casecode.pos.core.model.business.SubscriptionPlan
import com.casecode.pos.core.model.data.PurchaseResult
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.services.TestSubscriptionService
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProcessPlanPurchaseUseCaseTest {
    private lateinit var testSubscriptionService: TestSubscriptionService
    private lateinit var processPlanPurchaseUseCase: ProcessPlanPurchaseUseCase

    @Before
    fun setup() {
        testSubscriptionService = TestSubscriptionService()
        processPlanPurchaseUseCase = ProcessPlanPurchaseUseCase(
            subscriptionService = testSubscriptionService,
            logService = TestLogService(),
        )
    }

    @Test
    fun freePlan_ReturnsSuccessImmediately() = runTest {
        val freePlan = SubscriptionPlan(
            id = "free_plan",
            nameEn = "Free",
            nameAr = "مجاني",
            isFree = true,
            featuresEn = emptyList(),
            featuresAr = emptyList(),
            limits = PlanLimits(1, 1, 100, 1000),
        )

        val result = processPlanPurchaseUseCase(mockk(), freePlan)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.wasSuccessful == true)
        assertTrue(result.getOrNull()?.providerTransactionId?.startsWith("free_plan_activation_") == true)
    }

    @Test
    fun paidPlan_CallsSubscriptionService() = runTest {
        val paidPlan = SubscriptionPlan(
            id = "paid_plan",
            nameEn = "Paid",
            nameAr = "مدفوع",
            isFree = false,
            featuresEn = emptyList(),
            featuresAr = emptyList(),
            limits = PlanLimits(1, 1, 100, 1000),
        )
        val purchaseResult = PurchaseResult(wasSuccessful = true, providerTransactionId = "trans_123")
        testSubscriptionService.setPurchaseResult(Result.success(purchaseResult))

        val result = processPlanPurchaseUseCase(mockk(), paidPlan)

        assertTrue(result.isSuccess)
        assertEquals(purchaseResult, result.getOrNull())
    }

    @Test
    fun serviceFailure_ReturnsFailure() = runTest {
        val paidPlan = SubscriptionPlan(
            id = "paid_plan",
            nameEn = "Paid",
            nameAr = "مدفوع",
            isFree = false,
            featuresEn = emptyList(),
            featuresAr = emptyList(),
            limits = PlanLimits(1, 1, 100, 1000),
        )
        testSubscriptionService.setPurchaseResult(Result.failure(Exception("Purchase failed")))

        val result = processPlanPurchaseUseCase(mockk(), paidPlan)

        assertTrue(result.isFailure)
    }
}
