package com.casecode.pos.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.domain.usecase.AddBranchBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetSubscriptionBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetSubscriptionsUseCase
import com.casecode.pos.core.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Business
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val currentUser: FirebaseUser? = null,
    val business: Business = Business(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
    val subscriptions: List<Subscription> = emptyList(),
    val isSubscriptionsError: Boolean = false,
    val selectedSubscription: Subscription? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authService: AuthService,
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
            authService.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
    }

    private fun fetchBusiness() {
        viewModelScope.launch {
            getBusinessUseCase().collect { result ->
                when (result) {
                    is Resource.Empty -> {}
                    is Resource.Error -> {
                        val message = result.message as? Int ?: com.casecode.pos.core.ui.R.string.core_ui_error_unknown
                        _uiState.update { it.copy(userMessage = message) }
                    }

                    Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }

                    is Resource.Success -> {
                        _uiState.update { it.copy(business = result.data, isLoading = false) }
                    }
                }
            }
        }
    }

    private fun fetchSubscriptions() {
        viewModelScope.launch {
            getSubscriptionsUseCase().collect { result ->
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
    }

    fun addBranch(name: String, phoneBranch: String) {
        viewModelScope.launch {
            val currentBusiness = _uiState.value.business
            val lastBranchCode = currentBusiness.branches.lastOrNull()?.branchCode?.inc() ?: 1
            val branch = Branch(
                lastBranchCode,
                branchName = name,
                phoneNumber = phoneBranch,
            )

            when (val result = addBranchBusinessUseCase(branch)) {
                is Resource.Empty -> {}
                is Resource.Error -> {
                    val message = result.message as? Int ?: com.casecode.pos.core.ui.R.string.core_ui_error_add_branch_message
                    _uiState.update { it.copy(userMessage = message) }
                }

                Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
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