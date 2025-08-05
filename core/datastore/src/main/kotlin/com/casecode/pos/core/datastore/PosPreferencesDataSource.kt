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
package com.casecode.pos.core.datastore

import androidx.datastore.core.DataStore
import com.casecode.pos.core.model.LoginStateResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Data source for POS preferences, using DataStore to store and retrieve data.
 *
 * @param sessionDataStore The DataStore instance used to store and retrieve session preferences.
 */
class PosPreferencesDataSource
@Inject
constructor(
    private val sessionDataStore: DataStore<SessionPreferences>,

) {
    /**
     * A Flow that represents the high-level login state of the app.
     * This maps the raw proto data into our domain-specific LoginStateResult sealed interface.
     */
    val loginData: Flow<LoginStateResult> = sessionDataStore.data
        .map { prefs ->
            when (prefs.loginStatus) {
                LoginStatus.OWNER_LOGGED_IN -> LoginStateResult.OwnerLoggedIn(
                    businessId = prefs.businessId,
                    activeBranchId = prefs.activeBranchId,
                )

                LoginStatus.OWNER_ONBOARDING -> LoginStateResult.OwnerOnBoarding(
                    businessId = prefs.businessId,
                )

                LoginStatus.EMPLOYEE_LOGGED_IN -> LoginStateResult.EmployeeLoggedIn(
                    businessId = prefs.businessId,
                    activeBranchId = prefs.activeBranchId,
                    role = prefs.userRole,
                )

                else -> LoginStateResult.LoggedOut
            }
        }

    /**
     * Updates the Datastore to reflect a new user session. This is the implementation
     * that startOwnerSession and startEmployeeSession will call via SessionRepository.
     *
     * This single method replaces your previous `saveOwnerLogin` and `saveEmployeeLogin`.
     */
    suspend fun saveNewLoginSession(
        isOwner: Boolean = false,
        isCompleteSetupBusiness: Boolean = false,
        userId: String,
        userName: String,
        businessId: String,
        activeBranchId: String,
        role: String,
    ) {
        sessionDataStore.updateData { prefs ->
            prefs.toBuilder()
                .setLoginStatus(
                    if (isOwner) {
                        if (isCompleteSetupBusiness) {
                            LoginStatus.OWNER_LOGGED_IN
                        } else {
                            LoginStatus.OWNER_ONBOARDING
                        }
                    } else {
                        LoginStatus.EMPLOYEE_LOGGED_IN
                    },
                )
                .setUserId(userId)
                .setUserName(userName)
                .setBusinessId(businessId)
                .setActiveBranchId(activeBranchId)
                .setUserRole(role)
                .build()
        }
    }

    /**
     * Clears all session data and sets the state to LoggedOut.
     * This replaces your old `restLogin`.
     */
    suspend fun clearLoginSession() {
        sessionDataStore.updateData {
            // It's safer to clear fields explicitly than to just use the default instance,
            // in case the default ever changes.
            it.toBuilder()
                .clearLoginStatus()
                .clearUserId()
                .clearUserName()
                .clearBusinessId()
                .clearActiveBranchId()
                .clearUserRole()
                .build()
        }
    }
}