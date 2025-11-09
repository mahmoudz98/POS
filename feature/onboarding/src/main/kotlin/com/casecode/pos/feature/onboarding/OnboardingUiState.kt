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
import androidx.annotation.StringRes
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.Currency
import com.casecode.pos.core.model.business.SubscriptionPlan
import com.casecode.pos.core.model.business.TaxRate
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.model.data.Country
import com.casecode.pos.core.model.data.PurchaseResult
import com.casecode.pos.core.model.data.getSupportedCountries

/**
 * A data class holding all temporary data collected from the user during the onboarding wizard.
 * This structure is designed to be directly compatible with the CreateBusinessUseCase.
 */
data class OnboardingData(
    val businessName: String = "",
    val businessVertical: Vertical? = null,
    val businessNameError: Int? = null,
    val email: String = "",
    val emailError: Int? = null,
    val phone: String = "",
    val phoneError: Int? = null,
    val selectedCurrency: Currency? = null,
    val currencyError: Int? = null,
    val taxRate: TaxRate? = null,
    val taxRateError: Int? = null,
    val selectedPlan: SubscriptionPlan? = null,
    val subscriptionPlanError: Int? = null,
    val selectedPlanError: Int? = null,
    val branches: List<Branch> = emptyList(),
    val branchesError: Int? = null,
)

/**
 * Represents the entire state of the onboarding screen.
 * This single state object is the source of truth for the UI.
 */

data class OnboardingUiState(
    val currentStep: OnboardingStep = OnboardingStep.BUSINESS_INFO,
    val onboardingData: OnboardingData = OnboardingData(),
    val availableVerticals: List<Vertical> = Vertical.entries,
    val availableCurrencies: List<Currency> = emptyList(),
    val availablePlans: List<SubscriptionPlan> = emptyList(),
    val availableCountries: List<Country> = getSupportedCountries(),
    val countrySelected: Country? = null,
    val paymentResult: PurchaseResult? = null,
    val isLoading: Boolean = false,
    @StringRes val userMessage: Int? = null,
    val isAddBranchDialogOpen: Boolean = false,
) {
    /**
     * Calculated property to determine if the "Next" button should be enabled
     * for the current step, based on the collected data.
     */
    val isNextEnabled: Boolean
        get() = when (currentStep) {
            OnboardingStep.BUSINESS_INFO -> onboardingData.businessName.isNotBlank() &&
                onboardingData.businessVertical != null &&
                onboardingData.email.isNotBlank() &&
                onboardingData.phone.isNotBlank()

            OnboardingStep.FINANCIALS -> onboardingData.selectedCurrency != null && onboardingData.taxRate != null
            OnboardingStep.SUBSCRIPTION -> onboardingData.selectedPlan != null && paymentResult?.wasSuccessful == true
            OnboardingStep.BRANCHES -> onboardingData.branches.isNotEmpty()
        }
}

/**
 * A sealed interface representing every possible user action or event
 * that can occur on the onboarding screen.
 */
sealed interface OnboardingEvent {
    data class BusinessNameChanged(val name: String) : OnboardingEvent
    data class BusinessVerticalSelected(val vertical: Vertical) : OnboardingEvent
    data class EmailChanged(val email: String) : OnboardingEvent
    data class CountrySelected(val country: Country) : OnboardingEvent
    data class PhoneChanged(val phone: String) : OnboardingEvent
    data class CurrencySelected(val currency: Currency) : OnboardingEvent
    data class TaxRateSet(val taxRate: TaxRate) : OnboardingEvent
    data class AddBranch(val branchName: String, val phone: String) : OnboardingEvent
    data class RemoveBranch(val branch: Branch) : OnboardingEvent
    data class PlanSelected(val plan: SubscriptionPlan, val activity: Activity) : OnboardingEvent
    object NextClicked : OnboardingEvent
    object BackClicked : OnboardingEvent
    object CompleteOnboardingClicked : OnboardingEvent
    object CloseOnboardingClicked : OnboardingEvent
    object UserMessageShown : OnboardingEvent
    data class SetAddBranchDialogVisibility(val visible: Boolean) : OnboardingEvent
}
