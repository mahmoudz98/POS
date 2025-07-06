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
import androidx.compose.runtime.Immutable

/**
 * Represents every possible state of the Login screen's UI.
 * Marked as Immutable for Compose performance optimization.
 */
@Immutable
sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object ShowPlayServicesUnavailableDialog : LoginUiState

    data class Error(@StringRes val messageResId: Int) : LoginUiState
}

/**
 * Represents every user action that can be initiated from the UI.
 */
@Immutable
sealed interface LoginEvent {
    // Event when Google Sign-In provides a result (success or failure).
    data class GoogleSignInResult(val context: Context) : LoginEvent

    // Event to acknowledge that an error message has been displayed.
    data object ErrorMessageShown : LoginEvent
    data object PlayServicesDialogDismissed : LoginEvent
}