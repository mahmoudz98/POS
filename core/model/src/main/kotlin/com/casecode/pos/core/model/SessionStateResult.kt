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
package com.casecode.pos.core.model

import com.casecode.pos.core.model.business.EmployeeRole
sealed interface LoginStateResultOld {
    data object Loading : LoginStateResultOld

    data class SuccessLoginAdminOld(
        val uid: String,
    ) : LoginStateResultOld

    data class NotCompleteBusiness(
        val uid: String,
    ) : LoginStateResultOld

    data class EmployeeLoginOld(
        val employee: EmployeeLoginData,
    ) : LoginStateResultOld

    data object NotSignIn : LoginStateResultOld

    data object Error : LoginStateResultOld
}

sealed interface SessionStateResult {
    object Loading : SessionStateResult
    data class OwnerOnBoarding(val businessId: String) : SessionStateResult
    data class OwnerLoggedIn(
        val businessId: String,
        val activeBranchId: String,
        val userId: String,
        val userName: String,
    ) : SessionStateResult

    data class EmployeeLoggedIn(
        val businessId: String,
        val activeBranchId: String,
        val userId: String,
        val userName: String,
        val role: EmployeeRole,
    ) : SessionStateResult
    object None : SessionStateResult
}
