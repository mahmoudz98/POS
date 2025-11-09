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
package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.domain.exceptions.NoActiveSessionException
import com.casecode.pos.core.domain.repository.business.EmployeeRepository
import com.casecode.pos.core.model.SessionStateResult
import com.casecode.pos.core.model.business.Employee
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetEmployeesUseCase @Inject constructor(private val employeeRepository: EmployeeRepository) {
    operator fun invoke() = employeeRepository.getEmployees()
}
class CreateEmployeeUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val getCurrentSessionUseCase: GetCurrentSessionUseCase,
) {
    suspend operator fun invoke(
        employee: Employee,
        password: String,
    ): Result<Unit> {
        val businessId = when (val sessionInfo = getCurrentSessionUseCase().first()) {
            is SessionStateResult.OwnerLoggedIn -> sessionInfo.businessId
            is SessionStateResult.EmployeeLoggedIn -> sessionInfo.businessId
            else -> null
        }
        if (businessId == null) return Result.failure(NoActiveSessionException())
        return employeeRepository.createEmployee(employee, password, businessId)
    }
}
class UpdateEmployeeUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val getCurrentSessionUseCase: GetCurrentSessionUseCase,
) {
    suspend operator fun invoke(
        employee: Employee,
        password: String,
    ): Result<Unit> {
        val businessId = when (val sessionInfo = getCurrentSessionUseCase().first()) {
            is SessionStateResult.OwnerLoggedIn -> sessionInfo.businessId
            is SessionStateResult.EmployeeLoggedIn -> sessionInfo.businessId
            else -> null
        }
        if (businessId == null) return Result.failure(NoActiveSessionException())
        return employeeRepository.updateEmployee(employee, password, businessId)
    }
}
class DeleteEmployeeUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val getCurrentSessionUseCase: GetCurrentSessionUseCase,
) {
    suspend operator fun invoke(employeeId: String): Result<Unit> {
        val businessId = when (val sessionInfo = getCurrentSessionUseCase().first()) {
            is SessionStateResult.OwnerLoggedIn -> sessionInfo.businessId
            is SessionStateResult.EmployeeLoggedIn -> sessionInfo.businessId
            else -> null
        }
        if (businessId == null) return Result.failure(NoActiveSessionException())
        return employeeRepository.deleteEmployee(employeeId, businessId = businessId)
    }
}
