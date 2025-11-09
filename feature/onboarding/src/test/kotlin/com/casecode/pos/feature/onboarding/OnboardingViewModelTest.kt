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
package com.casecode.pos.feature.onboarding

import android.app.Activity
import com.casecode.pos.core.domain.usecase.CreateBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetCurrentUserUseCase
import com.casecode.pos.core.domain.usecase.GetOnboardingConfigUseCase
import com.casecode.pos.core.domain.usecase.ProcessPlanPurchaseUseCase
import com.casecode.pos.core.domain.usecase.SignOutUseCase
import com.casecode.pos.core.domain.usecase.StartOwnerSessionUseCase
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.Currency
import com.casecode.pos.core.model.business.PlanLimits
import com.casecode.pos.core.model.business.SubscriptionPlan
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.model.data.Country
import com.casecode.pos.core.model.data.PurchaseResult
import com.casecode.pos.core.model.users.User
import com.casecode.pos.core.testing.repository.business.TestAuthRepository
import com.casecode.pos.core.testing.repository.business.TestBranchRepository
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.repository.business.TestSubscriptionRepository
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.services.TestSubscriptionService
import com.casecode.pos.core.testing.util.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class OnboardingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var subject: OnboardingViewModel

    // Test Doubles
    private lateinit var authRepository: TestAuthRepository
    private lateinit var businessRepository: TestBusinessRepository
    private lateinit var subscriptionRepository: TestSubscriptionRepository
    private lateinit var subscriptionService: TestSubscriptionService
    private lateinit var sessionRepository: TestSessionRepository
    private lateinit var branchesRepository: TestBranchRepository
    private lateinit var logService: TestLogService

    // Real Use Cases with Test Doubles
    private lateinit var getOnboardingConfigUseCase: GetOnboardingConfigUseCase
    private lateinit var createBusinessUseCase: CreateBusinessUseCase
    private lateinit var processPlanPurchaseUseCase: ProcessPlanPurchaseUseCase
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var signOutUseCase: SignOutUseCase
    private lateinit var startOwnerSessionUseCase: StartOwnerSessionUseCase

    private val testUser = User(
        "test_uid",
        "test@example.com",
        "Test User",
        null,
    )
    private val testCurrency = Currency(
        "USD",
        "US Dollar",
        "دولار أمريكي",
        "$",
        2,
    )
    private val testPlan = SubscriptionPlan(
        id = "plan_1",
        nameEn = "Basic",
        nameAr = "أساسي",
        prices = emptyList(),
        isFree = true,
        featuresEn = emptyList(),
        featuresAr = emptyList(),
        limits = PlanLimits(
            maxBranches = 1,
            maxEmployees = 2,
            maxItems = 100,
            initialCredits = 1000,
        ),
    )

    @Before
    fun setup() {
        authRepository = TestAuthRepository()
        businessRepository = TestBusinessRepository()
        subscriptionRepository = TestSubscriptionRepository()
        subscriptionService = TestSubscriptionService()
        sessionRepository = TestSessionRepository()
        branchesRepository = TestBranchRepository()
        logService = TestLogService()

        getOnboardingConfigUseCase = GetOnboardingConfigUseCase(subscriptionRepository)
        createBusinessUseCase = CreateBusinessUseCase(businessRepository, logService)
        processPlanPurchaseUseCase = ProcessPlanPurchaseUseCase(subscriptionService, logService)
        getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
        signOutUseCase = SignOutUseCase(authRepository, sessionRepository, logService)
        startOwnerSessionUseCase = StartOwnerSessionUseCase(sessionRepository, branchesRepository)

        subject = OnboardingViewModel(
            getOnboardingConfigUseCase = getOnboardingConfigUseCase,
            createBusinessUseCase = createBusinessUseCase,
            processPlanPurchaseUseCase = processPlanPurchaseUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase,
            signOutUseCase = signOutUseCase,
            startOwnerSessionUseCase = startOwnerSessionUseCase,
        )
    }

    @Test
    fun onEvent_businessNameChanged_updatesState() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }

        val newName = "My Awesome Cafe"
        subject.onEvent(OnboardingEvent.BusinessNameChanged(newName))
        assertEquals(newName, subject.uiState.value.onboardingData.businessName)
    }

    @Test
    fun onEvent_nextClicked_withInvalidBusinessInfo_showsErrorsAndStaysOnStep() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }

        subject.onEvent(OnboardingEvent.BusinessNameChanged("")) // Invalid name
        subject.onEvent(OnboardingEvent.NextClicked)

        val uiState = subject.uiState.value
        assertEquals(OnboardingStep.BUSINESS_INFO, uiState.currentStep)
        assertNotNull(uiState.onboardingData.businessNameError)
    }

    @Test
    fun onEvent_nextClicked_withValidBusinessInfo_movesToFinancialsStep() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }

        fillValidBusinessInfo()
        subject.onEvent(OnboardingEvent.NextClicked)
        assertEquals(OnboardingStep.FINANCIALS, subject.uiState.value.currentStep)
    }

    @Test
    fun onEvent_addBranch_updatesStateWithNewBranch() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }

        subject.onEvent(OnboardingEvent.AddBranch("Main Branch", "12345"))
        val uiState = subject.uiState.value
        assertEquals(1, uiState.onboardingData.branches.size)
        assertEquals("Main Branch", uiState.onboardingData.branches.first().name)
    }

    @Test
    fun onEvent_completeOnboardingClicked_withValidData_createsBusinessAndCompletes() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }

        authRepository.setLoggedInUser(testUser)
        fillAllStepsWithValidData()
        branchesRepository.addBranch("bus1ID", Branch(name = "Main Branch", phone = "1234567890"))

        subject.onEvent(OnboardingEvent.CompleteOnboardingClicked)
        val business = businessRepository.findBusinessByOwner(testUser.uid).getOrNull()
        assertNotNull(business)
    }

    @Test
    fun onEvent_addBranch_whenBranchLimitReached_showsErrorAndDoesNotAddBranch() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }

        // Given: A plan with a limit of 1 branch is selected
        val planWithLimit = testPlan.copy(limits = testPlan.limits.copy(maxBranches = 1))
        subject.onEvent(OnboardingEvent.PlanSelected(planWithLimit, mockk(relaxed = true)))

        // And: One branch has already been added
        subject.onEvent(OnboardingEvent.AddBranch("First Branch", "111"))
        assertEquals(1, subject.uiState.value.onboardingData.branches.size)

        // When: The user tries to add a second branch
        subject.onEvent(OnboardingEvent.AddBranch("Second Branch", "222"))

        // Then: The branch list size should still be 1, and a user message should be shown
        val uiState = subject.uiState.value
        assertEquals(1, uiState.onboardingData.branches.size)
        assertNotNull(uiState.userMessage)
    }

    @Test
    fun onEvent_whenOpenVisibleDialog__showsIsAddDialogTrue() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { subject.uiState.collect() }

        // Given: A plan with a limit of 1 branch is selected
        subject.onEvent(OnboardingEvent.SetAddBranchDialogVisibility(true))

        assertTrue {
            subject.uiState.value.isAddBranchDialogOpen
        }
    }
    private fun fillValidBusinessInfo() {
        subject.onEvent(OnboardingEvent.BusinessNameChanged("Test Business"))
        subject.onEvent(OnboardingEvent.BusinessVerticalSelected(Vertical.RETAIL))
        subject.onEvent(OnboardingEvent.EmailChanged("test@test.com"))
        subject.onEvent(
            OnboardingEvent.CountrySelected(
                Country(
                    countryCode = "EG",
                    nameEn = "Egypt",
                    nameAr = "مصر",
                    phoneCode = "+20",
                ),
            ),
        )
        subject.onEvent(OnboardingEvent.PhoneChanged("1234567890"))
    }

    private fun fillAllStepsWithValidData(isPaymentSuccessful: Boolean = true) {
        fillValidBusinessInfo()
        subject.onEvent(OnboardingEvent.CurrencySelected(testCurrency))
        subject.onEvent(OnboardingEvent.TaxRateSet(mockk(relaxed = true)))
        subject.onEvent(OnboardingEvent.AddBranch("Main Branch", "1234567890"))

        val paymentResult =
            PurchaseResult(wasSuccessful = isPaymentSuccessful, providerTransactionId = "tx_123")
        subscriptionService.setPurchaseResult(Result.success(paymentResult))
        subject.onEvent(OnboardingEvent.PlanSelected(testPlan, mockk<Activity>(relaxed = true)))
    }
}
