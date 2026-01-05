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
import com.casecode.pos.core.model.SessionStateResult
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.users.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class TestSessionRepository @Inject
constructor() : SessionRepository {

    private val _sessionInfoFlow = MutableStateFlow<SessionStateResult>(SessionStateResult.Loading)
    override val sessionInfo: Flow<SessionStateResult> = _sessionInfoFlow

    var lastOwnerSession: User? = null
        private set

    var lastBusinessId: String? = null
        private set

    var lastEmployee: Employee? = null
        private set

    var lastBranchId: String? = null
        private set

    override suspend fun startOwnerSession(
        user: User,
        isCompleteSetupBusiness: Boolean,
        branchId: String,
    ) {
        lastOwnerSession = user
        lastBranchId = branchId
        // In a real implementation, this would update sessionInfoFlow
        // For testing, we might want to manually set the flow state if needed,
        // or just verify that this method was called with the correct args.
        // If the test relies on sessionInfoFlow updating, we should update it here.
        if (isCompleteSetupBusiness) {
            _sessionInfoFlow.value = SessionStateResult.OwnerLoggedIn(
                businessId = "business_id_placeholder", // Since User doesn't have businessId directly usually
                activeBranchId = branchId,
                userId = user.uid,
                userName = user.name ?: "",
            )
        }
    }

    override suspend fun startEmployeeSession(
        businessId: String,
        employee: Employee,
        activeBranchId: String,
    ) {
        lastBusinessId = businessId
        lastEmployee = employee
        lastBranchId = activeBranchId

        _sessionInfoFlow.value = SessionStateResult.EmployeeLoggedIn(
            businessId = businessId,
            activeBranchId = activeBranchId,
            userId = employee.id,
            userName = employee.name,
            role = employee.role,
        )
    }

    override suspend fun clearSession() {
        _sessionInfoFlow.value = SessionStateResult.None
        lastOwnerSession = null
        lastBusinessId = null
        lastEmployee = null
        lastBranchId = null
    }

    fun setSessionInfo(newSessionStateResult: SessionStateResult) {
        _sessionInfoFlow.value = newSessionStateResult
    }

    fun setSessionInfoOwnerLoggedIn() {
        _sessionInfoFlow.value = SessionStateResult.OwnerLoggedIn("business1", "branch22", "122", "mahmoud")
    }
}
