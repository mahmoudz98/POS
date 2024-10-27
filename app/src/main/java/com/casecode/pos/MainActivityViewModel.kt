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
package com.casecode.pos

import androidx.lifecycle.ViewModel
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.model.data.LoginStateResult
import com.casecode.pos.core.model.data.permissions.Permission
import com.casecode.pos.core.ui.stateInWhileSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    authRepository: AuthRepository,
) : ViewModel() {
    val mainAuthUiState: StateFlow<MainAuthUiState> = authRepository.loginData.map {
        when (it) {
            LoginStateResult.Loading -> MainAuthUiState.Loading
            is LoginStateResult.EmployeeLogin -> {
                when (it.employee.permission) {
                    Permission.ADMIN -> MainAuthUiState.LoginByAdminEmployee
                    Permission.SALE -> MainAuthUiState.LoginBySaleEmployee
                    Permission.NONE -> MainAuthUiState.ErrorLogin
                }
            }

            LoginStateResult.Error, LoginStateResult.NotSignIn -> MainAuthUiState.ErrorLogin
            is LoginStateResult.NotCompleteBusiness -> MainAuthUiState.ErrorLogin
            is LoginStateResult.SuccessLoginAdmin -> MainAuthUiState.LoginByAdmin
        }
    }.stateInWhileSubscribed(MainAuthUiState.Loading)
}

sealed interface MainAuthUiState {
    data object Loading : MainAuthUiState

    data object LoginByAdmin : MainAuthUiState

    data object LoginByAdminEmployee : MainAuthUiState

    data object LoginBySaleEmployee : MainAuthUiState

    data object LoginByNoneEmployee : MainAuthUiState

    data object ErrorLogin : MainAuthUiState
}