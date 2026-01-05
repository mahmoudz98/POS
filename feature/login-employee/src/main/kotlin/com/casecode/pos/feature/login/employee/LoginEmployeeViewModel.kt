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
package com.casecode.pos.feature.login.employee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.domain.model.EmployeeLoginResult
import com.casecode.pos.core.domain.usecase.SignInEmployeeUseCase
import com.casecode.pos.core.domain.utils.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.casecode.pos.core.ui.R as uiResource

@HiltViewModel
class LoginEmployeeViewModel
@Inject
constructor(
    private val networkMonitor: NetworkMonitor,
    private val signInEmployeeUseCase: SignInEmployeeUseCase,
) : ViewModel() {
    private val _loginEmployeeUiState = MutableStateFlow(LoginEmployeeUiState())
    val loginEmployeeUiState = _loginEmployeeUiState.asStateFlow()

    private val _formState = MutableStateFlow(LoginEmployeeFormState())
    val formState = _formState.asStateFlow()

    init {
        setNetworkMonitor()
    }

    private fun setNetworkMonitor() = viewModelScope.launch {
        networkMonitor.isOnline.collect {
            setConnected(it)
        }
    }

    private fun setConnected(isOnline: Boolean) {
        _loginEmployeeUiState.update { it.copy(isOnline = isOnline) }
    }

    fun snackbarMessageShownLoginEmployee() {
        _loginEmployeeUiState.update { it.copy(userMessage = null) }
    }

    fun showMessageLoginEmployee(message: Int) {
        _loginEmployeeUiState.update { it.copy(userMessage = message) }
    }

    fun onFormEvent(event: LoginEmployeeFormEvent) {
        when (event) {
            is LoginEmployeeFormEvent.CompanyCodeChanged -> {
                _formState.update { it.copy(companyCode = event.value, companyCodeError = null) }
            }
            is LoginEmployeeFormEvent.EmployeeIdChanged -> {
                _formState.update { it.copy(employeeId = event.value, employeeIdError = null) }
            }
            is LoginEmployeeFormEvent.PasswordChanged -> {
                _formState.update { it.copy(password = event.value, passwordError = null) }
            }
            LoginEmployeeFormEvent.SubmitClicked -> {
                val validatedState = _formState.value.validate()
                _formState.update { validatedState }
                if (validatedState.isValid) {
                    loginByEmployee()
                }
            }
        }
    }

    private fun loginByEmployee() {
        if (_loginEmployeeUiState.value.isOnline.not()) {
            _loginEmployeeUiState.update {
                it.copy(userMessage = uiResource.string.core_ui_error_network)
            }
            return
        }

        if (_loginEmployeeUiState.value.inProgressLoginEmployee) return

        val currentState = _formState.value
        val companyCode = currentState.companyCode
        val employeeId = currentState.employeeId
        val password = currentState.password

        _loginEmployeeUiState.update { it.copy(inProgressLoginEmployee = true) }

        viewModelScope.launch {
            val result = signInEmployeeUseCase(companyCode, employeeId, password)

            _loginEmployeeUiState.update { state ->
                when (result) {
                    is EmployeeLoginResult.Success -> {
                        state.copy(
                            inProgressLoginEmployee = false,
                            isLoginSuccess = true,
                            userMessage = null,
                        )
                    }

                    is EmployeeLoginResult.InvalidCredentials,
                    is EmployeeLoginResult.InvalidCompanyCode,
                    is EmployeeLoginResult.EmployeeNotFound,
                    -> {
                        state.copy(
                            inProgressLoginEmployee = false,
                            userMessage = R.string.feature_login_employee_error_employee_incorrect,
                        )
                    }

                    is EmployeeLoginResult.NetworkError -> {
                        state.copy(
                            inProgressLoginEmployee = false,
                            userMessage = uiResource.string.core_ui_error_network,
                        )
                    }

                    is EmployeeLoginResult.GeneralError -> {
                        state.copy(
                            inProgressLoginEmployee = false,
                            userMessage = R.string.feature_login_employee_error_login,
                        )
                    }
                }
            }
        }
    }
}
