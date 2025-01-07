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
package com.casecode.pos.feature.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.domain.utils.NetworkMonitor
import com.casecode.pos.core.domain.repository.AccountRepository
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.utils.SignInGoogleState
import com.casecode.pos.core.model.data.LoginStateResult
import com.casecode.pos.core.ui.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
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
 * @property accountRepository Service for interacting with account data.
 * @property authRepository Service for authentication.
 */
@HiltViewModel
class SignInActivityViewModel
@Inject
constructor(
    private val networkMonitor: NetworkMonitor,
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    @Inject
    lateinit var googleIdOption: GetGoogleIdOption
    private val _signInUiState = MutableStateFlow(SignInActivityUiState())
    val signInUiState = _signInUiState.asStateFlow()
    val loginStateResult: StateFlow<LoginStateResult> =
        authRepository.loginData.stateIn(
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

    fun isGooglePlayServicesAvailable() = accountRepository.isGooglePlayServicesAvailable()

    fun signIn(idToken: suspend () -> String) {
        if (!signInUiState.value.isOnline) {
            _signInUiState.update { it.copy(userMessage = R.string.core_ui_error_network) }
        } else {
            viewModelScope.launch {
                _signInUiState.update { it.copy(isLoading = true) }
                when (val result = accountRepository.signIn(idToken)) {
                    is SignInGoogleState.Success -> {
                        _signInUiState.update {
                            it.copy(isLoading = false)
                        }
                        checkIfRegistrationAndBusinessCompleted()
                    }

                    SignInGoogleState.Cancelled -> {
                        _signInUiState.update {
                            it.copy(
                                isLoading = false,
                            )
                        }
                    }

                    is SignInGoogleState.Error -> {
                        _signInUiState.update {
                            it.copy(
                                isLoading = false,
                                userMessage = result.message,
                            )
                        }
                    }
                }
            }
        }
    }

    fun checkIfRegistrationAndBusinessCompleted() {
        viewModelScope.launch {
            accountRepository.checkUserLogin()
        }
    }

    fun snackbarMessageShown() {
        _signInUiState.update { it.copy(userMessage = null) }
    }
}