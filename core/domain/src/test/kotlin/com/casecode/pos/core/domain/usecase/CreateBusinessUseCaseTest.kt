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

import com.casecode.pos.core.model.PurchaseResult
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.PlanLimits
import com.casecode.pos.core.model.business.SubscriptionPlan
import com.casecode.pos.core.model.business.TaxRate
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

class CreateBusinessUseCaseTest {
    @get:Rule
    val coroutinesRule = CoroutinesTestRule()
    private lateinit var testBusinessRepository: TestBusinessRepository

    private lateinit var createBusinessUseCase: CreateBusinessUseCase

    // Helper test data
    private val testPlan = SubscriptionPlan(
        id = "plan_1",
        nameEn = "Basic",
        nameAr = "أساسي",
        isFree = false,
        featuresEn = emptyList(),
        featuresAr = emptyList(),
        limits = PlanLimits(1, 5, 100, 1000),
    )
    private val testBranch = Branch(id = "branch_1", name = "Main Branch", phone = "1234567890")
    private val testTaxRate = TaxRate(id = "tax_1", name = "VAT", rate = 15.0f)

    @Before
    fun setup() {
        testBusinessRepository = TestBusinessRepository()
        createBusinessUseCase = CreateBusinessUseCase(
            businessRepository = testBusinessRepository,
            logService = TestLogService(),
        )
    }

    @Test
    fun `createBusiness with empty businessName returns failure`() = runTest {
        val invalidData = OnboardingData(
            ownerUid = "test_uid",
            businessName = "",
            email = "test@test.com",
            phone = "1234567890",
            businessVertical = Vertical.RETAIL,
            currencyCode = "USD",
            selectedPlan = testPlan,
            paymentResult = PurchaseResult(providerTransactionId = "fake_id", wasSuccessful = true),
            initialBranches = listOf(testBranch),
            initialTaxRate = testTaxRate,
        )

        val result = createBusinessUseCase(invalidData)

        assertTrue(result.isFailure)
    }

    @Test
    fun `createBusiness with empty initialBranches returns failure`() = runTest {
        val invalidData = OnboardingData(
            ownerUid = "test_uid",
            businessName = "My Awesome Business",
            email = "test@test.com",
            phone = "1234567890",
            businessVertical = Vertical.RETAIL,
            currencyCode = "USD",
            selectedPlan = testPlan,
            paymentResult = PurchaseResult(providerTransactionId = "fake_id", wasSuccessful = true),
            initialBranches = emptyList(),
            initialTaxRate = testTaxRate,
        )

        val result = createBusinessUseCase(invalidData)

        assertTrue(result.isFailure)
    }

    @Test
    fun `createBusiness with unsuccessful paymentResult returns failure`() = runTest {
        val invalidData = OnboardingData(
            ownerUid = "test_uid",
            businessName = "My Awesome Business",
            email = "test@test.com",
            phone = "1234567890",
            businessVertical = Vertical.RETAIL,
            currencyCode = "USD",
            selectedPlan = testPlan,
            paymentResult = PurchaseResult(providerTransactionId = "fake_id", wasSuccessful = false),
            initialBranches = listOf(testBranch),
            initialTaxRate = testTaxRate,
        )

        val result = createBusinessUseCase(invalidData)

        assertTrue(result.isFailure)
    }

    @Test
    fun `createBusiness with valid data returns success`() = runTest {
        val validData = OnboardingData(
            ownerUid = "test_uid",
            businessName = "My Awesome Business",
            email = "test@test.com",
            phone = "1234567890",
            businessVertical = Vertical.RETAIL,
            currencyCode = "USD",
            selectedPlan = testPlan,
            paymentResult = PurchaseResult(providerTransactionId = "fake_id", wasSuccessful = true),
            initialBranches = listOf(testBranch),
            initialTaxRate = testTaxRate,
        )

        // When: The use case is invoked
        val result = createBusinessUseCase(validData)

        // Then: The result should be a success
        assertTrue(result.isSuccess)
    }
}