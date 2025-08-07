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
package com.casecode.pos.core.data.repository.business

import com.casecode.pos.core.datastore.PosPreferencesDataSource
import com.casecode.pos.core.domain.repository.business.SessionRepository
import com.casecode.pos.core.firebase.datasource.AuthRemoteDataSource
import com.casecode.pos.core.model.LoginStateResult
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.users.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val auth: AuthRemoteDataSource,
    private val prefs: PosPreferencesDataSource,
) : SessionRepository {
    override val loginState: Flow<LoginStateResult> = prefs.sessionData

    override suspend fun startOwnerSession(
        user: User,
        isCompleteSetupBusiness: Boolean,
        branchId: String,
    ) {
        prefs.saveOwnerSession(
            isCompleteBusiness = isCompleteSetupBusiness,
            userId = user.uid,
            userName = user.name ?: "",
            businessId = user.uid,
            activeBranchId = branchId,
        )
    }

    override suspend fun startEmployeeSession(businessId: String, employee: Employee, activeBranchId: String) {
        prefs.saveEmployeeSession(
            userId = employee.employeeId,
            userName = employee.name,
            businessId = businessId,
            activeBranchId = activeBranchId,
            role = employee.role,
        )
    }

    override suspend fun clearSession() {
        if (loginState.first() is LoginStateResult.OwnerLoggedIn) {
            auth.signOut()
        }
        prefs.clearLoginSession()
    }
}
