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
import com.casecode.pos.core.domain.service.GoogleAuthUiClient
import com.casecode.pos.core.domain.usecase.SignInOwnerUseCase
import com.casecode.pos.core.domain.utils.OwnerLoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInOwnerUseCase: SignInOwnerUseCase,
    private val googleAuthUiService: GoogleAuthUiClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.GoogleSignInResult -> executeOwnerSignIn(event.context)
            is LoginEvent.ErrorMessageShown, is LoginEvent.PlayServicesDialogDismissed ->
                _uiState.value =
                    LoginUiState.Idle
        }
    }

    private fun executeOwnerSignIn(context: Context) {
        if (_uiState.value is LoginUiState.Loading) return

        if (googleAuthUiService.isGooglePlayServicesAvailable(context)) {
            viewModelScope.launch {
                _uiState.value = LoginUiState.Loading

                googleAuthUiService.getIdToken(context).fold(
                    onSuccess = { idToken ->
                        handleLoginResult(signInOwnerUseCase(idToken))
                    },
                    onFailure = { exception ->
                        showMessage(R.string.feature_login_error_network_connection)
                    },
                )
            }
        } else {
            _uiState.value = LoginUiState.ShowPlayServicesUnavailableDialog
        }
    }

    private fun handleLoginResult(result: OwnerLoginResult) {
        _uiState.value = LoginUiState.Idle
        when (result) {
            OwnerLoginResult.AuthenticationFailed -> {
                showMessage(R.string.feature_login_error_unknown)
            }

            is OwnerLoginResult.GeneralError -> showMessage(R.string.feature_login_error_unknown)

            OwnerLoginResult.NetworkError -> {
                showMessage(R.string.feature_login_error_network_connection)
            }
            else -> Unit
        }
    }

    private fun showMessage(@StringRes messageResId: Int) {
        _uiState.value = LoginUiState.Error(messageResId)
    }
}