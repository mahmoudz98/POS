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

import com.casecode.pos.core.domain.exceptions.EmployeeNotFoundException
import com.casecode.pos.core.domain.exceptions.InvalidPasswordException
import com.casecode.pos.core.domain.exceptions.NoActiveSessionException
import com.casecode.pos.core.domain.model.EmployeeLoginResult
import com.casecode.pos.core.domain.repository.business.BusinessRepository
import com.casecode.pos.core.domain.repository.business.EmployeeRepository
import com.casecode.pos.core.domain.repository.business.SessionRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.model.SessionStateResult
import com.casecode.pos.core.model.business.Employee
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject

/**
 * Pure business logic for employee authentication.
 *
 * Single Responsibility: Authenticate employee and start session.
 * No knowledge of sync, WorkManager, or Android framework.
 * Returns domain result that higher layers can interpret.
 */
class SignInEmployeeUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val sessionRepository: SessionRepository,
    private val logService: LogService,
    private val businessRepository: BusinessRepository,
) {
    suspend operator fun invoke(
        companyCode: String,
        employeeId: String,
        password: String,
    ): EmployeeLoginResult {
        logService.log("SignInEmployeeUseCase: Authenticating employee: $employeeId for company: $companyCode")

        try {
            val businessResult = businessRepository.getBusinessByCompanyCode(companyCode)
            val business = businessResult.getOrElse { e ->
                logService.log("SignInEmployeeUseCase: Failed to fetch business. Error: $e")
                return if (e is IOException) {
                    EmployeeLoginResult.NetworkError
                } else {
                    EmployeeLoginResult.GeneralError(e)
                }
            } ?: return EmployeeLoginResult.InvalidCompanyCode

            val employee = employeeRepository.authenticateEmployee(
                businessId = business.id,
                employeeIdentifier = employeeId,
                password = password,
            ).getOrElse { exception ->
                return when (exception) {
                    is EmployeeNotFoundException -> {
                        logService.log("SignInEmployeeUseCase: Employee not found: $employeeId")
                        EmployeeLoginResult.EmployeeNotFound
                    }

                    is InvalidPasswordException -> {
                        logService.log("SignInEmployeeUseCase: Invalid password for employee: $employeeId")
                        EmployeeLoginResult.InvalidCredentials
                    }

                    is IOException -> {
                        logService.log("SignInEmployeeUseCase: Network error during authentication: $exception")
                        EmployeeLoginResult.NetworkError
                    }

                    else -> {
                        logService.log("SignInEmployeeUseCase: Authentication error: $exception")
                        EmployeeLoginResult.GeneralError(exception)
                    }
                }
            }

            sessionRepository.startEmployeeSession(business.id, employee, employee.assignedBranchId)
            logService.log("SignInEmployeeUseCase: Authentication successful")
            return EmployeeLoginResult.Success
        } catch (e: Exception) {
            logService.log("SignInEmployeeUseCase: Error during authentication: $e")
            return if (e is IOException) {
                EmployeeLoginResult.NetworkError
            } else {
                EmployeeLoginResult.GeneralError(e)
            }
        }
    }
}

class GetEmployeesUseCase @Inject constructor(private val employeeRepository: EmployeeRepository) {
    operator fun invoke() = employeeRepository.getEmployees()
}

class GenerateEmployeeIdUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository,
) {
    suspend operator fun invoke() = employeeRepository.getNextIdSuggestion()
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
