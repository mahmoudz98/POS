package com.casecode.pos.ui.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.R
import com.casecode.pos.core.data.service.AccountService
import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.LoginStateResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for SignInActivity.
 *
 * @property networkMonitor Network monitor to track network connectivity.
 * @property accountService Service for interacting with account data.
 * @property authService Service for authentication.
 */
@HiltViewModel
class SignInActivityViewModel
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val accountService: AccountService,
    private val authService: AuthService,
) : ViewModel() {

    private val _signInUiState = MutableStateFlow(SignInActivityUiState())
    val signInUiState = _signInUiState.asStateFlow()

    val loginStateResult: StateFlow<LoginStateResult> =
        authService.loginData.stateIn(
            scope = viewModelScope,
            initialValue = LoginStateResult.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    init {
        setNetworkMonitor()
    }

    private fun setNetworkMonitor() = viewModelScope.launch {
        networkMonitor.isOnline.collect {
            setConnected(it)
        }
    }

    private fun setConnected(isOnline: Boolean) {
        _signInUiState.update { it.copy(isOnline = isOnline) }
    }

    fun signIn() {
        if (signInUiState.value.isOnline.not()) {
            _signInUiState.update { it.copy(userMessage = com.casecode.pos.core.ui.R.string.core_ui_error_network) }
            return
        }
        viewModelScope.launch {
            when (val result = accountService.signIn()) {
                is Resource.Loading -> {}
                is Resource.Empty -> {
                    _signInUiState.update {
                        it.copy(
                            userMessage = result.message as Int,
                        )
                    }
                }

                is Resource.Error -> {
                    _signInUiState.update {
                        it.copy(
                            userMessage = result.message as Int,
                        )
                    }
                }

                is Resource.Success -> {
                    _signInUiState.update {
                        it.copy(
                            userMessage = result.data,
                        )
                    }
                    checkIfRegistrationAndBusinessCompleted()
                }
            }

        }
    }
    fun checkIfRegistrationAndBusinessCompleted() {
        viewModelScope.launch {
            accountService.checkUserLogin()
        }

    }

    fun snackbarMessageShown() {
        _signInUiState.update { it.copy(userMessage = null) }
    }


}