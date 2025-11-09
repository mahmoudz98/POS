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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.domain.usecase.CreateBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetCurrentUserUseCase
import com.casecode.pos.core.domain.usecase.GetOnboardingConfigUseCase
import com.casecode.pos.core.domain.usecase.ProcessPlanPurchaseUseCase
import com.casecode.pos.core.domain.usecase.SignOutUseCase
import com.casecode.pos.core.domain.usecase.StartOwnerSessionUseCase
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.SubscriptionPlan
import com.casecode.pos.core.ui.stateInWhileSubscribed
import com.casecode.pos.core.ui.updateWithViewModelScope
import com.casecode.pos.core.ui.utils.validateEmail
import com.casecode.pos.core.ui.utils.validatePhoneNumber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.casecode.pos.core.domain.usecase.OnboardingData as CreateBusinessOnboardingData
import com.casecode.pos.core.ui.R.string as uiString

@HiltViewModel
class OnboardingViewModel
@Inject
constructor(
    private val getOnboardingConfigUseCase: GetOnboardingConfigUseCase,
    private val createBusinessUseCase: CreateBusinessUseCase,
    private val processPlanPurchaseUseCase: ProcessPlanPurchaseUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val startOwnerSessionUseCase: StartOwnerSessionUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> =
        _uiState.onStart {
            loadOnboardingConfig()
        }.stateInWhileSubscribed(OnboardingUiState())

    private fun loadOnboardingConfig() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val configResult = getOnboardingConfigUseCase()
            _uiState.update {
                if (configResult.isSuccess) {
                    val config = configResult.getOrThrow()
                    it.copy(
                        isLoading = false,
                        availableCurrencies = config.currencies,
                        availablePlans = config.plans,
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        userMessage = uiString.core_ui_error_unknown,
                    )
                }
            }
        }
    }

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.BusinessNameChanged ->
                updateData {
                    it.copy(
                        businessName = event.name,
                        businessNameError = null,
                    )
                }

            is OnboardingEvent.BusinessVerticalSelected -> updateData { it.copy(businessVertical = event.vertical) }
            is OnboardingEvent.EmailChanged ->
                updateData {
                    it.copy(
                        email = event.email,
                        emailError = null,
                    )
                }

            is OnboardingEvent.CountrySelected -> _uiState.update { it.copy(countrySelected = event.country) }

            is OnboardingEvent.PhoneChanged ->
                updateData {
                    it.copy(
                        phone = event.phone,
                        phoneError = null,
                    )
                }

            is OnboardingEvent.CurrencySelected -> updateData { it.copy(selectedCurrency = event.currency) }
            is OnboardingEvent.TaxRateSet -> updateData { it.copy(taxRate = event.taxRate) }
            is OnboardingEvent.PlanSelected -> {
                handlePlanSelected(event.plan, event.activity)
            }

            is OnboardingEvent.AddBranch -> {
                val currentBranches = _uiState.value.onboardingData.branches
                val planLimit = _uiState.value.onboardingData.selectedPlan?.limits?.maxBranches

                if (planLimit != null && currentBranches.size >= planLimit) {
                    _uiState.updateWithViewModelScope {
                        it.copy(
                            userMessage = R.string.feature_onboarding_error_branch_limit_reached_message,
                        )
                    }
                } else {
                    val newBranch = Branch(name = event.branchName, phone = event.phone)
                    updateData { it.copy(branches = it.branches + newBranch) }
                }
            }

            is OnboardingEvent.RemoveBranch -> updateData { it.copy(branches = it.branches - event.branch) }

            is OnboardingEvent.SetAddBranchDialogVisibility -> {
                _uiState.updateWithViewModelScope {
                    it.copy(isAddBranchDialogOpen = event.visible)
                }
            }

            OnboardingEvent.NextClicked -> handleNextClicked()
            OnboardingEvent.BackClicked -> {
                val currentStepOrdinal = _uiState.value.currentStep.ordinal
                if (currentStepOrdinal > 0) {
                    val previousStep = OnboardingStep.fromOrdinal(currentStepOrdinal - 1)
                    _uiState.updateWithViewModelScope { it.copy(currentStep = previousStep) }
                }
            }

            OnboardingEvent.CompleteOnboardingClicked -> completeOnboarding()
            OnboardingEvent.CloseOnboardingClicked -> {
                viewModelScope.launch {
                    signOutUseCase()
                }
            }

            OnboardingEvent.UserMessageShown -> _uiState.update { it.copy(userMessage = null) }
        }
    }

    private fun handleNextClicked() {
        val currentStep = _uiState.value.currentStep
        when (currentStep) {
            OnboardingStep.BUSINESS_INFO -> {
                if (!validateBusinessInfo()) return
            }

            OnboardingStep.FINANCIALS -> {
                if (!validateFinancials()) return
            }

            OnboardingStep.BRANCHES -> {
                if (!validateBranchSetup()) return
            }

            OnboardingStep.SUBSCRIPTION -> {
            }
        }

        val currentStepOrdinal = _uiState.value.currentStep.ordinal
        if (currentStepOrdinal < OnboardingStep.entries.size - 1) {
            val nextStep = OnboardingStep.fromOrdinal(currentStepOrdinal + 1)
            _uiState.update { it.copy(currentStep = nextStep) }
        }
    }

    private fun validateBusinessInfo(): Boolean {
        val data = uiState.value.onboardingData

        val businessNameError =
            if (data.businessName.isBlank()) uiString.core_ui_error_unknown else null
        val emailError = validateEmail(data.email)
        val phoneError =
            validatePhoneNumber(data.phone, uiState.value.countrySelected?.countryCode ?: "")

        _uiState.update {
            val newData =
                it.onboardingData.copy(
                    businessNameError = businessNameError,
                    emailError = emailError,
                    phoneError = phoneError,
                )
            it.copy(onboardingData = newData)
        }

        return businessNameError == null && emailError == null && phoneError == null
    }

    private fun validateFinancials(): Boolean {
        val data = _uiState.value.onboardingData
        val currencyError =
            if (data.selectedCurrency == null) uiString.core_ui_error_unknown else null
        val taxRateError =
            if (data.taxRate == null || data.taxRate.rate < 0.0f) uiString.core_ui_error_unknown else null

        _uiState.updateWithViewModelScope {
            val newData =
                it.onboardingData.copy(
                    currencyError = currencyError,
                    taxRateError = taxRateError,
                )
            it.copy(onboardingData = newData)
        }

        return currencyError == null && taxRateError == null
    }

    private fun handlePlanSelected(
        plan: SubscriptionPlan,
        activity: Activity,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            processPlanPurchaseUseCase(activity, plan).onSuccess { purchaseResult ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        paymentResult = purchaseResult,
                        onboardingData = it.onboardingData.copy(selectedPlan = plan),
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        paymentResult = null,
                        onboardingData = it.onboardingData.copy(selectedPlan = null),
                        isLoading = false,
                        userMessage = uiString.core_ui_error_unknown,
                    )
                }
            }
        }
    }

    private fun validateBranchSetup(): Boolean {
        val data = _uiState.value.onboardingData
        val branchesError =
            if (data.branches.isEmpty()) uiString.core_ui_error_add_branch_empty else null

        _uiState.update {
            val newData =
                it.onboardingData.copy(
                    branchesError = branchesError,
                )
            it.copy(onboardingData = newData)
        }

        return branchesError == null
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            if (!validateBusinessInfo()) return@launch
            _uiState.update { it.copy(isLoading = true) }
            val owner = getCurrentUserUseCase()
            if (owner == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userMessage = uiString.core_ui_error_unknown,
                    )
                }
                return@launch
            }
            val currentState = _uiState.value
            val data = currentState.onboardingData
            val paymentResult = currentState.paymentResult

            if (data.selectedPlan == null || paymentResult == null || !paymentResult.wasSuccessful) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userMessage = uiString.core_ui_error_unknown,
                    )
                }
                return@launch
            }
            val useCaseData =
                CreateBusinessOnboardingData(
                    ownerUid = owner.uid,
                    businessName = data.businessName,
                    email = data.email,
                    phone = data.phone,
                    businessVertical = data.businessVertical!!,
                    currencyCode = data.selectedCurrency!!.code,
                    selectedPlan = data.selectedPlan,
                    paymentResult = paymentResult,
                    initialBranches = data.branches,
                    initialTaxRate = data.taxRate!!,
                )
            createBusinessUseCase(useCaseData).onSuccess { businessId ->
                startOwnerSessionUseCase(owner, businessId).onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userMessage = R.string.feature_onboarding_error_starting_session_message,
                        )
                    }
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userMessage = R.string.feature_onboarding_error_creating_business_message,
                    )
                }
            }
        }
    }

    private fun updateData(updateAction: (OnboardingData) -> OnboardingData) {
        _uiState.updateWithViewModelScope { currentState ->
            val newData = updateAction(currentState.onboardingData)
            currentState.copy(onboardingData = newData)
        }
    }
}
