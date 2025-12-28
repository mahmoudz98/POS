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

import com.casecode.pos.core.domain.exceptions.AuthenticationException
import com.casecode.pos.core.domain.exceptions.EmployeeIdCollisionException
import com.casecode.pos.core.domain.exceptions.EmployeeNotFoundException
import com.casecode.pos.core.domain.exceptions.InvalidPasswordException
import com.casecode.pos.core.domain.repository.business.EmployeeRepository
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.testing.base.TestRepository
import com.casecode.pos.core.testing.data.employeeTestData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TestEmployeeRepository : TestRepository(), EmployeeRepository {
    private val employeesSharedFlow:
        MutableSharedFlow<List<Employee>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val employees = employeeTestData.toMutableList()

    // Authentication test state
    private var validCredentials: Map<String, AuthCredentials> = emptyMap()
    private var authenticationError: Throwable? = null
    private var invalidCompanyCodes: Set<String> = emptySet()
    private var invalidEmployeeIds: Set<Pair<String, String>> =
        emptySet()
    private var invalidPasswords: Set<Triple<String, String, String>> =
        emptySet()

    // Simple test helpers for common cases
    var employee: Employee? = null
    var throwEmployeeNotFound = false
    var throwInvalidPassword = false
    var throwIOException = false
    var throwGeneralException: Exception? = null

    data class AuthCredentials(
        val companyCode: String,
        val employeeId: String,
        val password: String,
        val employee: Employee,
    )

    override fun getEmployees(): Flow<List<Employee>> {
        return employeesSharedFlow.asSharedFlow()
    }

    suspend fun sendEmployees(data: List<Employee> = employeeTestData) {
        employeesSharedFlow.emit(data)
    }

    override suspend fun authenticateEmployee(
        businessId: String,
        employeeIdentifier: String,
        password: String,
    ): Result<Employee> {
        // Simple test helpers (check these first)
        if (throwIOException) {
            return Result.failure(java.io.IOException("Network error"))
        }
        throwGeneralException?.let { return Result.failure(it) }
        if (throwEmployeeNotFound) {
            return Result.failure(EmployeeNotFoundException("Employee not found"))
        }
        if (throwInvalidPassword) {
            return Result.failure(InvalidPasswordException("Invalid password"))
        }
        employee?.let { return Result.success(it) }

        // Return error if set
        authenticationError?.let { return Result.failure(it) }

        // Check invalid company code
        if (businessId in invalidCompanyCodes) {
            return Result.failure(AuthenticationException("Invalid company code"))
        }

        // Check invalid employee ID
        if (Pair(businessId, employeeIdentifier) in invalidEmployeeIds) {
            return Result.failure(EmployeeNotFoundException("Employee not found"))
        }

        // Check invalid password
        if (Triple(businessId, employeeIdentifier, password) in invalidPasswords) {
            return Result.failure(InvalidPasswordException("Invalid password"))
        }

        // Check valid credentials
        val key = "$businessId:$employeeIdentifier:$password"
        val credentials = validCredentials[key]
        return if (credentials != null) {
            Result.success(credentials.employee)
        } else {
            Result.failure(AuthenticationException("Authentication failed"))
        }
    }

    override suspend fun getNextIdSuggestion(): Result<String> {
        return Result.success(employees.maxOf { it.id }.ifEmpty { "1001" })
    }

    override suspend fun createEmployee(
        employee: Employee,
        plainTextPassword: String,
        businessId: String,
    ): Result<Unit> {
        if (failureThrowable != null) {
            return Result.failure(failureThrowable!!)
        }
        if (employees.any { it.name == employee.name }) {
            return Result.failure(
                EmployeeIdCollisionException("Employee with name ${employee.name} already exists"),
            )
        }
        employees.add(employee)
        employeesSharedFlow.emit(employees)
        return Result.success(Unit)
    }

    override suspend fun updateEmployee(
        employee: Employee,
        plainTextPassword: String,
        businessId: String,
    ): Result<Unit> {
        if (failureThrowable != null) {
            return Result.failure(failureThrowable!!)
        }
        val old = employees.find { it.id == employee.id }
        employees.remove(old)
        employees.add(employee)
        employeesSharedFlow.emit(employees)
        return Result.success(Unit)
    }

    override suspend fun deleteEmployee(
        id: String,
        businessId: String,
    ): Result<Unit> {
        if (failureThrowable != null) {
            return Result.failure(failureThrowable!!)
        }
        val old = employees.find { it.id == id }
        employees.remove(old)
        employeesSharedFlow.emit(employees)
        return Result.success(Unit)
    }

    override suspend fun syncUp(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun syncDown(): Boolean {
        TODO("Not yet implemented")
    }
}
