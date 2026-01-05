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
package com.casecode.pos.core.testing.dao

import android.database.sqlite.SQLiteConstraintException
import com.casecode.pos.core.database.dao.EmployeeDao
import com.casecode.pos.core.database.model.EmployeeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Test double for [EmployeeDao].
 * This class provides a in-memory implementation of [EmployeeDao] for testing purposes,
 * simulating database operations and constraints without actual database interaction.
 */
class TestEmployeeDao : EmployeeDao {

    private val employeesList = mutableListOf<EmployeeEntity>()
    private val _employeesFlow = MutableStateFlow<List<EmployeeEntity>>(emptyList())

    override suspend fun insertOrReplaceEmployee(employee: EmployeeEntity) {
        if (employeesList.any {
                it.isActive == 0 && it.id == employee.id
            }
        ) {
            throw SQLiteConstraintException("Employee name must be unique within a business.")
        }

        val existingIndex = employeesList.indexOfFirst { it.id == employee.id }
        if (existingIndex != -1) {
            employeesList[existingIndex] = employee
        } else {
            employeesList.add(employee)
        }
        _employeesFlow.value = employeesList.toList()
    }

    override suspend fun updateEmployees(vararg employees: EmployeeEntity) {
        employees.forEach { updatedEmployee ->
            val existingIndex = employeesList.indexOfFirst { it.id == updatedEmployee.id }
            if (existingIndex != -1) {
                employeesList[existingIndex] = updatedEmployee
            }
        }
        _employeesFlow.value = employeesList.toList()
    }

    override suspend fun deleteEmployee(id: String, businessId: String) {
        val existingIndex = employeesList.indexOfFirst { it.id == id }
        if (existingIndex != -1) {
            employeesList[existingIndex] = employeesList[existingIndex].copy(isActive = 1)
            _employeesFlow.value = employeesList.toList()
        }
    }

    override fun getEmployees(): Flow<List<EmployeeEntity>> {
        return _employeesFlow.asStateFlow().map { it.filter { entity -> entity.isActive == 0 } }
    }

    override suspend fun getEmployeeById(
        identifier: String,
        businessId: String,
    ): EmployeeEntity? {
        return employeesList.firstOrNull {
            it.businessId == businessId && (it.id == identifier && it.isActive != 1)
        }
    }

    override suspend fun getHighestEmployeeId(): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun employeeExists(id: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Clears all employees from the DAO, resetting its state for a new test.
     */
    fun clear() {
        employeesList.clear()
        _employeesFlow.value = emptyList()
    }
}
