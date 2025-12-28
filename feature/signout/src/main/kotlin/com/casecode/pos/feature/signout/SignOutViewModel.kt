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
package com.casecode.pos.feature.signout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.domain.usecase.GetCurrentSessionUseCase
import com.casecode.pos.core.domain.usecase.SignOutUseCase
import com.casecode.pos.core.model.SessionStateResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SignOutViewModel
@Inject
constructor(
    private val signOutUseCase: SignOutUseCase,
    getCurrentSessionUseCase: GetCurrentSessionUseCase,
) : ViewModel() {
    val uiState: StateFlow<SessionStateResult> =
        getCurrentSessionUseCase()
            .stateIn(
                scope = viewModelScope,
                initialValue = SessionStateResult.Loading,
                started = SharingStarted.WhileSubscribed(5_000),
            )

    fun signOut(): Deferred<Unit> = viewModelScope.async {
        signOutUseCase()
    }
}
