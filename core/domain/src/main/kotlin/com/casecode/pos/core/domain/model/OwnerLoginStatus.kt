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
package com.casecode.pos.core.domain.model

import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.Business

sealed interface OwnerLoginStatus {
    data class Active(val business: Business?) : OwnerLoginStatus
    object NeedsOnboarding : OwnerLoginStatus
    data class OnboardingPending(val business: Business?) : OwnerLoginStatus
}

/**
 * A sealed class representing all possible outcomes of an owner sign-in attempt.
 * This provides a rich, structured result to the ViewModel.
 */
sealed interface OwnerLoginResult {
    /**
     * Success! The user is authenticated and has a fully configured business.
     * The app should navigate to the main dashboard.
     */
    data object Success : OwnerLoginResult

    /**
     * The user's business account requires onboarding (e.g., first-time setup).
     */
    data object AccountNeedsOnboarding : OwnerLoginResult

    /**
     * A recoverable network error occurred during the process.
     * The UI should show a "Check your connection" message.
     */
    data object NetworkError : OwnerLoginResult

    /**
     * An unexpected or unrecoverable error occurred (e.g., failed to talk to Firebase).
     * The UI should show a generic "Something went wrong" message.
     */
    data class GeneralError(val exception: Throwable) : OwnerLoginResult

    /**
     * The Google Sign-In part of the flow failed or was cancelled by the user.
     * The UI should return to an idle state without showing a disruptive error message.
     */
    object AuthenticationFailed : OwnerLoginResult

    /**
     * The user has multiple branches and needs to select one to proceed.
     */
    data class BranchSelectionRequired(val branches: List<Branch>) : OwnerLoginResult
}
