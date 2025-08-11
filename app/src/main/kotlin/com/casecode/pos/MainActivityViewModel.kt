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
import com.casecode.pos.core.domain.repository.business.SessionRepository
import com.casecode.pos.core.model.LoginStateResult
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.ui.stateInWhileSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    sessionRepository: SessionRepository,
) : ViewModel() {
    val initialDestinationState: StateFlow<InitialDestinationState> =
        sessionRepository.loginState.map {
            when (it) {
                LoginStateResult.Loading -> InitialDestinationState.Loading
                LoginStateResult.LoggedOut -> InitialDestinationState.SignOut
                is LoginStateResult.OwnerOnBoarding -> InitialDestinationState.NotCompleteBusiness
                is LoginStateResult.EmployeeLoggedIn -> {
                    when (it.role) {
                        EmployeeRole.OWNER, EmployeeRole.MANAGER -> InitialDestinationState.LoginByAdminEmployee
                        EmployeeRole.CASHIER -> InitialDestinationState.LoginBySaleEmployee
                    }
                }

                is LoginStateResult.OwnerLoggedIn -> InitialDestinationState.LoginByAdmin
            }
        }.stateInWhileSubscribed(InitialDestinationState.Loading)
}

sealed interface InitialDestinationState {
    data object Loading : InitialDestinationState

    data object SignOut : InitialDestinationState
    data object NotCompleteBusiness : InitialDestinationState

    data object LoginByAdmin : InitialDestinationState

    data object LoginByAdminEmployee : InitialDestinationState

    data object LoginBySaleEmployee : InitialDestinationState

    data object LoginByNoneEmployee : InitialDestinationState

    fun shouldKeepSplashScreen() = this is Loading
}
