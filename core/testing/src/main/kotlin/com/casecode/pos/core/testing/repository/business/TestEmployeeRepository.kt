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

import com.casecode.pos.core.domain.exceptions.EmployeeNameCollisionException
import com.casecode.pos.core.domain.repository.business.EmployeeRepository
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.testing.base.TestRepository
import com.casecode.pos.core.testing.data.employeeTestData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class TestEmployeeRepository : TestRepository(), EmployeeRepository {
    private val employeesSharedFlow:
        MutableSharedFlow<List<Employee>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val employees = employeeTestData.toMutableList()

    override fun getEmployees(): Flow<List<Employee>> {
        return employeesSharedFlow
    }

    override suspend fun authenticateEmployee(
        companyCode: String,
        employeeIdentifier: String,
        password: String,
    ): Result<Pair<String, Employee>?> {
        TODO("Not yet implemented")
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
                EmployeeNameCollisionException("Employee with name ${employee.name} already exists"),
            )
        }
        employees.add(employee)
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
