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

import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.Currency
import com.casecode.pos.core.model.business.PlanLimits
import com.casecode.pos.core.model.business.SubscriptionPlan
import com.casecode.pos.core.model.business.TaxRate
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.model.data.PurchaseResult
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue
import com.casecode.pos.core.ui.R.string as uiString

class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val businessNameHint by lazy {
        composeTestRule.activity.getString(uiString.core_ui_business_name_hint)
    }
    private val currencyHint by lazy {
        composeTestRule.activity.getString(uiString.core_ui_currency_hint)
    }
    private val addBranchButton by lazy {
        composeTestRule.activity.getString(uiString.core_ui_add_branch_button_text)
    }
    private val branchesTitle by lazy {
        composeTestRule.activity.getString(OnboardingStep.BRANCHES.titleRes)
    }
    private val nextButton by lazy {
        composeTestRule.activity.getString(R.string.feature_onboarding_next_button_text)
    }
    private val backButton by lazy {
        composeTestRule.activity.getString(R.string.feature_onboarding_previous_button_text)
    }

    @Test
    fun whenStateIsLoading_showsLoadingIndicator() {
        val loadingState = OnboardingUiState(isLoading = true)

        composeTestRule.setContent {
            OnBoardingScreen(
                uiState = loadingState,
                onEvent = {},
            )
        }

        // Assuming your loading indicator has this content description
        composeTestRule.onNodeWithContentDescription("onBoardingLoading").assertIsDisplayed()
    }

    @Test
    fun onboardingFlow_whenStateIsIdle_showsBusinessInfoStep() {
        val initialState = OnboardingUiState()

        composeTestRule.setContent {
            OnBoardingScreen(
                uiState = initialState,
                onEvent = {},
            )
        }

        composeTestRule.onNodeWithText(businessNameHint).assertIsDisplayed()
    }

    @Test
    fun givenBusinessInfo_whenNextClicked_progressesToFinancialsStep() {
        val state = MutableStateFlow(OnboardingUiState())

        composeTestRule.setContent {
            OnBoardingScreen(
                uiState = state.collectAsState().value,
                onEvent = { event ->
                    if (event is OnboardingEvent.NextClicked) {
                        state.value = state.value.copy(currentStep = OnboardingStep.FINANCIALS)
                    }
                },
            )
        }

        // Simulate valid input for Business Info step
        composeTestRule.onNodeWithText(businessNameHint).performTextInput("Test Cafe")
        // In a real test, you would select from the dropdown.
        // For this state-driven test, we assume the ViewModel handles the selection.
        state.value = state.value.copy(
            onboardingData = state.value.onboardingData.copy(
                businessName = "Test Cafe",
                businessVertical = Vertical.CAFE,
                email = "a@b.com",
                phone = "123",
            ),
        )

        composeTestRule.onNodeWithText(nextButton).performClick()

        composeTestRule.onNodeWithText(currencyHint).assertIsDisplayed()
    }

    @Test
    fun givenFinancialsInfo_whenNextClicked_progressesToBranchStep() {
        val initialState = OnboardingUiState(currentStep = OnboardingStep.FINANCIALS)
        val state = MutableStateFlow(initialState)

        composeTestRule.setContent {
            OnBoardingScreen(
                uiState = state.collectAsState().value,
                onEvent = { event ->
                    if (event is OnboardingEvent.NextClicked) {
                        state.value = state.value.copy(currentStep = OnboardingStep.BRANCHES)
                    }
                },
            )
        }

        // Simulate valid input for Financials step
        state.value = state.value.copy(
            onboardingData = state.value.onboardingData.copy(
                selectedCurrency = Currency("USD", "US Dollar", "", "", 2),
                taxRate = TaxRate(name = "VAT", rate = 10f),
            ),
        )

        composeTestRule.onNodeWithText(nextButton).performClick()

        composeTestRule.onNodeWithText(addBranchButton).assertIsDisplayed()
    }

    @Test
    fun givenSubscription_whenNextClicked_progressesToBranchesStep() {
        val initialState = OnboardingUiState(currentStep = OnboardingStep.SUBSCRIPTION)
        val state = MutableStateFlow(initialState)
        val subscriptionPlan = SubscriptionPlan(
            id = "1",
            nameEn = "standard",
            nameAr = "",
            prices = emptyList(),
            isFree = true,
            featuresEn = listOf(),
            featuresAr = listOf(),
            limits = PlanLimits(
                maxBranches = 1,
                maxEmployees = 2,
                maxItems = 500,
                initialCredits = 300,
            ),
        )
        composeTestRule.setContent {
            OnBoardingScreen(
                uiState = state.collectAsState().value,
                onEvent = { event ->
                    if (event is OnboardingEvent.NextClicked) {
                        state.value = state.value.copy(currentStep = OnboardingStep.BRANCHES)
                    }
                },
            )
        }

        state.value = state.value.copy(
            onboardingData = state.value.onboardingData.copy(
                selectedPlan = subscriptionPlan,
            ),
            paymentResult = PurchaseResult("fake_id", true),
        )

        composeTestRule.onNodeWithText(nextButton).performClick()

        composeTestRule.onNodeWithText(branchesTitle).assertIsDisplayed()
    }

    @Test
    fun whenSubscriptionPlanAreEmpty_ShownEmptyScreen() {
        val initialState = OnboardingUiState(currentStep = OnboardingStep.SUBSCRIPTION)
        val plansEmptyTitle by lazy {
            composeTestRule.activity.getString(R.string.feature_onboarding_empty_plans_title)
        }
        composeTestRule.setContent {
            OnBoardingScreen(
                uiState = initialState,
                onEvent = {},
            )
        }
        composeTestRule.onNodeWithText(plansEmptyTitle).assertIsDisplayed()
    }

    @Test
    fun whenBranchesIsEmpty_ShowEmptyScreen() {
        val initialState = OnboardingUiState(currentStep = OnboardingStep.BRANCHES)
        val branchEmptyTitle by lazy {
            composeTestRule.activity.getString(R.string.feature_onboarding_branch_setup_empty_title)
        }
        composeTestRule.setContent {
            OnBoardingScreen(
                uiState = initialState,
                onEvent = {},
            )
        }
        composeTestRule.onNodeWithText(branchEmptyTitle).assertIsDisplayed()
    }

    @Test
    fun givenNotInFirstStep_whenBackClicked_returnsToPreviousStep() {
        val initialState = OnboardingUiState(currentStep = OnboardingStep.FINANCIALS)
        val state = MutableStateFlow(initialState)

        composeTestRule.setContent {
            OnBoardingScreen(
                uiState = state.collectAsState().value,
                onEvent = { event ->
                    if (event is OnboardingEvent.BackClicked) {
                        state.value = state.value.copy(currentStep = OnboardingStep.BUSINESS_INFO)
                    }
                },
            )
        }

        composeTestRule.onNodeWithText(backButton).performClick()

        composeTestRule.onNodeWithText(businessNameHint).assertIsDisplayed()
    }

    @Test
    fun onboardingFlow_whenAllStepsAreValidAndNextIsClicked_progressesThroughCorrectOrder() {
        val state = MutableStateFlow(OnboardingUiState())
        var onCompleteCalled = false

        composeTestRule.setContent {
            OnBoardingScreen(
                uiState = state.collectAsState().value,
                onEvent = { event ->
                    when (event) {
                        is OnboardingEvent.NextClicked -> {
                            val nextStep = when (state.value.currentStep) {
                                OnboardingStep.BUSINESS_INFO -> OnboardingStep.FINANCIALS
                                OnboardingStep.FINANCIALS -> OnboardingStep.SUBSCRIPTION
                                OnboardingStep.SUBSCRIPTION -> OnboardingStep.BRANCHES
                                else -> state.value.currentStep
                            }
                            state.value = state.value.copy(currentStep = nextStep)
                        }

                        is OnboardingEvent.CompleteOnboardingClicked -> {
                            onCompleteCalled = true
                        }

                        else -> {}
                    }
                },
            )
        }

        // 1. Business Info Step
        val businessNameHint =
            composeTestRule.activity.getString(uiString.core_ui_business_name_hint)
        composeTestRule.onNodeWithText(businessNameHint).assertIsDisplayed()
        state.value = state.value.copy(
            onboardingData = state.value.onboardingData.copy(
                businessName = "Test Cafe",
                businessVertical = Vertical.CAFE,
                email = "a@b.com",
                phone = "123",
            ),
        )
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.feature_onboarding_next_button_text))
            .performClick()

        // 2. Financials Step
        val currencyHint = composeTestRule.activity.getString(uiString.core_ui_currency_hint)
        composeTestRule.onNodeWithText(currencyHint).assertIsDisplayed()
        state.value = state.value.copy(
            onboardingData = state.value.onboardingData.copy(
                selectedCurrency = Currency("USD", "US Dollar", "", "", 2),
                taxRate = TaxRate(name = "VAT", rate = 10f),
            ),
        )
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.feature_onboarding_next_button_text))
            .performClick()

        // 3. Subscription Step
        val subscriptionTitle =
            composeTestRule.activity.getString(OnboardingStep.SUBSCRIPTION.titleRes)
        composeTestRule.onNodeWithText(subscriptionTitle).assertIsDisplayed()
        state.value = state.value.copy(
            onboardingData = state.value.onboardingData.copy(
                selectedPlan = SubscriptionPlan(
                    id = "1",
                    nameEn = "standard",
                    nameAr = "",
                    prices = emptyList(),
                    isFree = true,
                    featuresEn = listOf(),
                    featuresAr = listOf(),
                    limits = PlanLimits(
                        maxBranches = 1,
                        maxEmployees = 2,
                        maxItems = 500,
                        initialCredits = 300,
                    ),
                ),
            ),
            paymentResult = PurchaseResult("fake_id", true),
        )
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.feature_onboarding_next_button_text))
            .performClick()

        // 4. Branch Setup Step
        val addBranchButton =
            composeTestRule.activity.getString(uiString.core_ui_add_branch_button_text)
        composeTestRule.onNodeWithText(addBranchButton).assertIsDisplayed()
        state.value = state.value.copy(
            onboardingData = state.value.onboardingData.copy(
                branches = listOf(Branch(id = "1", name = "Main", phone = "123")),
            ),
        )
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.feature_onboarding_done_button_text))
            .performClick()

        // 5. Final Assertion
        assertTrue(onCompleteCalled, "onOnboardingComplete should have been called")
    }
}
