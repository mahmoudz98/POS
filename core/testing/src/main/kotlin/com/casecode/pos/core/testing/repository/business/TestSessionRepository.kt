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
package com.casecode.pos.core.testing.repository.business

import com.casecode.pos.core.domain.repository.business.SessionRepository
import com.casecode.pos.core.model.LoginStateResult
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.users.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class TestSessionRepository @Inject constructor() : SessionRepository {

    private val _loginStateFlow = MutableStateFlow<LoginStateResult>(LoginStateResult.LoggedOut)
    override val loginState: Flow<LoginStateResult> = _loginStateFlow.asStateFlow()

    var lastOwnerSession: User? = null
        private set
    var lastEmployeeSession: Employee? = null
        private set
    var lastOwnerBusinessId: String? = null
        private set
    var lastActiveBranchId: String? = null
        private set

    override suspend fun startOwnerSession(
        user: User,
        isCompleteSetupBusiness: Boolean,
        branchId: String,
    ) {
        lastOwnerSession = user
        lastOwnerBusinessId = user.uid
        lastActiveBranchId = branchId
        _loginStateFlow.value = LoginStateResult.OwnerLoggedIn(user.uid, branchId)
    }

    override suspend fun startEmployeeSession(businessId: String, employee: Employee, activeBranchId: String) {
        lastEmployeeSession = employee
        lastOwnerBusinessId = businessId
        lastActiveBranchId = activeBranchId
        _loginStateFlow.value = LoginStateResult.EmployeeLoggedIn(businessId, activeBranchId, employee.role)
    }

    override suspend fun clearSession() {
        _loginStateFlow.value = LoginStateResult.LoggedOut
        lastOwnerSession = null
        lastEmployeeSession = null
        lastOwnerBusinessId = null
        lastActiveBranchId = null
    }
}