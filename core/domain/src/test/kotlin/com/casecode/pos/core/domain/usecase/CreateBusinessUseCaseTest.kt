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

import com.casecode.pos.core.domain.exceptions.ValidationException
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.PlanLimits
import com.casecode.pos.core.model.business.SubscriptionPlan
import com.casecode.pos.core.model.business.TaxRate
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.model.data.PurchaseResult
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CreateBusinessUseCaseTest {
    @get:Rule
    val coroutinesRule = CoroutinesTestRule()
    private lateinit var testBusinessRepository: TestBusinessRepository

    private lateinit var createBusinessUseCase: CreateBusinessUseCase

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
    fun emptyBusinessName_returnsValidationException() = runTest {
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
        assertIs<ValidationException>(result.exceptionOrNull())
    }

    @Test
    fun emptyInitialBranches_returnsValidationException() = runTest {
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
        assertIs<ValidationException>(result.exceptionOrNull())
    }

    @Test
    fun unsuccessfulPayment_returnsValidationException() = runTest {
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
        assertIs<ValidationException>(result.exceptionOrNull())
    }

    @Test
    fun validData_returnsSuccess() = runTest {
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

        val result = createBusinessUseCase(validData)

        assertTrue(result.isSuccess)

        val storedBusiness = testBusinessRepository.findBusinessByOwner("test_uid").getOrNull()
        assertNotNull(storedBusiness)
        assertEquals(validData.businessName, storedBusiness.name)
        assertEquals(validData.ownerUid, storedBusiness.ownerUid)
    }
}
