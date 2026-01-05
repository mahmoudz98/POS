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
package com.casecode.pos.feature.login

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.domain.model.OwnerLoginResult
import com.casecode.pos.core.domain.service.GoogleAuthUiClient
import com.casecode.pos.core.domain.usecase.GetCurrentUserUseCase
import com.casecode.pos.core.domain.usecase.SignInOwnerUseCase
import com.casecode.pos.core.domain.usecase.StartOwnerSessionUseCase
import com.casecode.pos.core.model.business.Branch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInOwnerUseCase: SignInOwnerUseCase,
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val startOwnerSessionUseCase: StartOwnerSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.GoogleSignInResult -> executeOwnerSignIn(event.context)
            is LoginEvent.ErrorMessageShown, is LoginEvent.PlayServicesDialogDismissed ->
                _uiState.update { LoginUiState.Idle }
            is LoginEvent.BranchSelected -> onBranchSelected(event.branch)
        }
    }

    private fun executeOwnerSignIn(context: Context) {
        if (_uiState.value is LoginUiState.Loading) return

        viewModelScope.launch {
            if (googleAuthUiClient.isGooglePlayServicesAvailable(context)) {
                _uiState.update { LoginUiState.Loading }

                googleAuthUiClient.getIdToken(context).fold(
                    onSuccess = { idToken ->
                        handleLoginResult(signInOwnerUseCase(idToken))
                    },
                    onFailure = {
                        Timber.e(it, "Error during Google Sign-In")
                        showMessage(R.string.feature_login_error_network_connection)
                    },
                )
            } else {
                _uiState.update { LoginUiState.ShowPlayServicesUnavailableDialog }
            }
        }
    }

    private fun handleLoginResult(result: OwnerLoginResult) {
        _uiState.update { LoginUiState.Idle }
        when (result) {
            OwnerLoginResult.AuthenticationFailed -> {
                showMessage(R.string.feature_login_error_unknown)
            }
            OwnerLoginResult.Success, OwnerLoginResult.AccountNeedsOnboarding -> Unit

            is OwnerLoginResult.GeneralError -> showMessage(R.string.feature_login_error_unknown)
            OwnerLoginResult.NetworkError -> {
                showMessage(R.string.feature_login_error_network_connection)
            }
            is OwnerLoginResult.BranchSelectionRequired -> {
                _uiState.update { LoginUiState.BranchSelection(result.branches) }
            }
        }
    }

    private fun onBranchSelected(branch: Branch) {
        if (_uiState.value is LoginUiState.Loading) return
        viewModelScope.launch {
            _uiState.update { LoginUiState.Loading }
            val currentUser = getCurrentUserUseCase()
            if (currentUser != null) {
                startOwnerSessionUseCase(currentUser, branch.id).fold(
                    onSuccess = {
                        _uiState.update { LoginUiState.Idle }
                    },
                    onFailure = {
                        showMessage(R.string.feature_login_error_unknown)
                    },
                )
            } else {
                showMessage(R.string.feature_login_error_unknown)
            }
        }
    }

    private fun showMessage(@StringRes messageResId: Int) {
        _uiState.update { LoginUiState.Error(messageResId) }
    }
}
