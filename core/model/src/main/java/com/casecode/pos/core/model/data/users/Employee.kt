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
package com.casecode.pos.core.model.data.users

data class Employee(
    val name: String = "",
    val phoneNumber: String = "",
    val password: String? = null,
    val branchName: String? = null,
    val permission: String = "",
) {
    fun isEmployeeNameDuplicate(
        currentEmployees: List<Employee>?,
        oldEmployee: Employee? = null,
    ): Boolean {
        currentEmployees?.forEach {
            if (it.name == this.name && it != oldEmployee) {
                return true
            }
        }
        return false
    }
}