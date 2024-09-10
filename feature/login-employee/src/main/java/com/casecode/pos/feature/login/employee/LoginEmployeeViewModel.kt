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
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.repository.AccountRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.ui.R as CoreResource

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginEmployeeViewModel
@Inject
constructor(
    private val networkMonitor: NetworkMonitor,
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val _loginEmployeeUiState = MutableStateFlow(LoginEmployeeUiState())
    val loginEmployeeUiState = _loginEmployeeUiState.asStateFlow()

    init {
        setNetworkMonitor()
    }

    private fun setNetworkMonitor() =
        viewModelScope.launch {
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

    fun loginByEmployee(
        uid: String,
        name: String,
        password: String,
    ) {
        if (_loginEmployeeUiState.value.isOnline.not()) {
            _loginEmployeeUiState.update { it.copy(userMessage = CoreResource.string.core_ui_error_network) }
            return
        }
        _loginEmployeeUiState.update { it.copy(inProgressLoginEmployee = true) }
        viewModelScope.launch {
            when (val loginEmployee = accountRepository.employeeLogin(uid, name, password)) {
                is Resource.Success -> {
                    if (loginEmployee.data) {
                        _loginEmployeeUiState.update { it.copy(inProgressLoginEmployee = false) }
                    } else {
                        _loginEmployeeUiState.update {
                            it.copy(
                                inProgressLoginEmployee = false,
                                userMessage = R.string.feature_login_employee_login_error_employee_incorrect,
                            )
                        }
                    }
                }

                else -> {
                    _loginEmployeeUiState.update {
                        it.copy(
                            inProgressLoginEmployee = false,
                            userMessage = R.string.feature_login_employee_login_error_login,
                        )
                    }
                }
            }
        }
    }
}