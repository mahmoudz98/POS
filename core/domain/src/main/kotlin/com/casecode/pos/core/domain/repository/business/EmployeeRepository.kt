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
package com.casecode.pos.core.domain.repository.business

import com.casecode.pos.core.domain.utils.Syncable
import com.casecode.pos.core.model.business.Employee
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository : Syncable {
    fun getEmployees(): Flow<List<Employee>>
    suspend fun authenticateEmployee(
        businessId: String,
        employeeIdentifier: String,
        password: String,
    ): Result<Employee>

    suspend fun getNextIdSuggestion(): Result<String>
    suspend fun createEmployee(employee: Employee, plainTextPassword: String, businessId: String): Result<Unit>
    suspend fun updateEmployee(employee: Employee, plainTextPassword: String, businessId: String): Result<Unit>
    suspend fun deleteEmployee(id: String, businessId: String): Result<Unit>
}
