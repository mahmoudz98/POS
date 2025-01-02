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
package com.casecode.pos.core.model.data

sealed interface LoginStateResult {
    data object Loading : LoginStateResult

    data class SuccessLoginAdmin(
        val uid: String,
    ) : LoginStateResult

    data class NotCompleteBusiness(
        val uid: String,
    ) : LoginStateResult

    data class EmployeeLogin(
        val employee: EmployeeLoginData,
    ) : LoginStateResult

    data object NotSignIn : LoginStateResult

    data object Error : LoginStateResult
}