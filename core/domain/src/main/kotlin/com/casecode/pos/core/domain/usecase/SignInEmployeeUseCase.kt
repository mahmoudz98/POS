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

import com.casecode.pos.core.domain.exceptions.InvalidCredentialsException
import com.casecode.pos.core.domain.repository.business.EmployeeRepository
import com.casecode.pos.core.domain.repository.business.SessionRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.model.business.Employee
import javax.inject.Inject

class SignInEmployeeUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val sessionRepository: SessionRepository,
    private val logService: LogService,
) {
    suspend operator fun invoke(
        companyCode: String,
        employeeId: String,
        password: String,
    ): Result<Employee> = runCatching {
        logService.log("SignInEmployeeUseCase: Authenticating employee: $employeeId for company: $companyCode")
        val resultPair = employeeRepository.authenticateEmployee(companyCode, employeeId, password).getOrThrow()
            ?: throw InvalidCredentialsException("Invalid company code, employee ID, or password.")

        val (businessId, employee) = resultPair

        val activeBranchId = employee.assignedBranchId

        logService.log("SignInEmployeeUseCase: Employee authenticated: ${employee.id}. Starting session.")
        sessionRepository.startEmployeeSession(businessId, employee, activeBranchId)

        employee
    }
}
