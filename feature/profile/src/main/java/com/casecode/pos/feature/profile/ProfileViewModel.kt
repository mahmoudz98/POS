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
package com.casecode.pos.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.usecase.AddBranchBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetSubscriptionBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetSubscriptionsUseCase
import com.casecode.pos.core.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.pos.core.domain.utils.AddBranchBusinessResult
import com.casecode.pos.core.domain.utils.BusinessResult
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.casecode.pos.core.model.data.users.Branch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
@Inject
constructor(
    private val authRepository: AuthRepository,
    private val getBusinessUseCase: GetBusinessUseCase,
    private val addBranchBusinessUseCase: AddBranchBusinessUseCase,
    private val getSubscriptionBusinessUseCase: GetSubscriptionBusinessUseCase,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
    private val setSubscriptionsBusinessUseCase: SetSubscriptionBusinessUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchCurrentUser()
        fetchBusiness()
        fetchSubscriptions()
    }

    private fun fetchCurrentUser() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
    }

    private fun fetchBusiness() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getBusinessUseCase()) {
                is BusinessResult.Error -> {
                    val message =
                        result.message
                            ?: com.casecode.pos.core.ui.R.string.core_ui_error_unknown
                    _uiState.update { it.copy(userMessage = message) }
                }

                is BusinessResult.Success -> {
                    _uiState.update { it.copy(business = result.data, isLoading = false) }
                }
            }
        }
    }

    private fun fetchSubscriptions() {
        viewModelScope.launch {
            val result = getSubscriptionsUseCase()
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(subscriptions = result.data) }
                }

                is Resource.Error -> {
                    _uiState.update { it.copy(isSubscriptionsError = true) }
                }

                else -> {}
            }
        }
    }

    fun addBranch(name: String, phoneBranch: String) {
        viewModelScope.launch {
            val currentBusiness = _uiState.value.business
            val lastBranchCode =
                currentBusiness.branches
                    .lastOrNull()
                    ?.branchCode
                    ?.inc() ?: -1
            val branch =
                Branch(
                    lastBranchCode,
                    branchName = name,
                    phoneNumber = phoneBranch,
                )
            _uiState.update { it.copy(isLoading = true) }
            when (val result = addBranchBusinessUseCase(branch)) {
                is AddBranchBusinessResult.Error -> {
                    _uiState.update { it.copy(userMessage = result.message) }
                }

                is AddBranchBusinessResult.Success -> {
                    _uiState.update {
                        it.copy(
                            userMessage = com.casecode.pos.core.ui.R.string.core_ui_success_add_branch_message,
                            isLoading = false,
                        )
                    }
                    fetchBusiness()
                }
            }
        }
    }

    fun onSnackbarMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun selectSubscription(subscription: Subscription) {
        _uiState.update { it.copy(selectedSubscription = subscription) }
    }
}