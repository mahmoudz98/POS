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

class TestSessionRepository : SessionRepository {

    private val _sessionInfoFlow = MutableStateFlow<SessionStateResult>(SessionStateResult.Loading)
    override val sessionInfo: Flow<SessionStateResult> = _sessionInfoFlow
    override suspend fun startOwnerSession(
        user: User,
        isCompleteSetupBusiness: Boolean,
        branchId: String,
    ) {
    }

    override suspend fun startEmployeeSession(
        businessId: String,
        employee: Employee,
        activeBranchId: String,
    ) {
    }

    override suspend fun clearSession() {
        _sessionInfoFlow.value = SessionStateResult.None
    }

    fun setSessionInfo(newSessionStateResult: SessionStateResult) {
        _sessionInfoFlow.value = newSessionStateResult
    }
    fun setSessionInfoOwnerLoggedIn() {
        _sessionInfoFlow.value = SessionStateResult.OwnerLoggedIn("business1", "branch22", "122", "mahmoud")
    }
}
