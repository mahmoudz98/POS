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
package com.casecode.pos.core.testing.datasource

import com.casecode.pos.core.firebase.datasource.EmployeeNetworkDataSource
import com.casecode.pos.core.firebase.model.NetworkEmployee
import javax.inject.Inject

class TestEmployeeNetworkDataSource @Inject
constructor() : EmployeeNetworkDataSource {

    // Use a flat list to store all NetworkEmployee objects
    private val employees = mutableListOf<NetworkEmployee>()
    private var networkError: Throwable? = null

    var findEmployeeByIdentifierCallCount = 0
        private set
    var addEmployeeCallCount = 0
        private set

    override suspend fun findEmployeeByIdentifier(
        businessId: String,
        employeeIdentifier: String,
    ): NetworkEmployee? {
        networkError?.let { throw it }
        findEmployeeByIdentifierCallCount++
        return employees.find { it.assignedBranchIds == businessId && (it.id == employeeIdentifier || it.name == employeeIdentifier) }
    }

    override suspend fun addEmployee(
        businessId: String,
        employee: NetworkEmployee,
    ) {
        networkError?.let { throw it }
        addEmployeeCallCount++
        // Ensure the employee has the correct businessId if it's not already set
        val employeeWithBusinessId = employee.copy(assignedBranchIds = businessId)
        employees.add(employeeWithBusinessId)
    }

    override suspend fun updateEmployee(
        businessId: String,
        employee: NetworkEmployee,
    ) {
        networkError?.let { throw it }
        // Remove the old version of the employee
        employees.removeIf { it.id == employee.id && it.assignedBranchIds == businessId }
        // Add the updated version
        employees.add(employee.copy(assignedBranchIds = businessId))
    }

    /**
     * Simulates deleting an employee from the network.
     *
     * @param businessId The ID of the business the employee belongs to.
     * @param employee The `NetworkEmployee` object to delete.
     */
    override suspend fun deleteEmployee(
        businessId: String,
        employee: NetworkEmployee,
    ) {
        networkError?.let { throw it }
        // Remove the employee from the list
        employees.removeIf { it.id == employee.id && it.assignedBranchIds == businessId }
    }

    override suspend fun getEmployees(businessId: String): List<NetworkEmployee> {
        networkError?.let { throw it }
        return employees.filter { it.assignedBranchIds == businessId }
    }

    /**
     * Helper method for tests to pre-populate the network data source.
     */
    fun setEmployee(businessId: String, employee: NetworkEmployee) {
        // Ensure the employee has the correct businessId if it's not already set
        val employeeWithBusinessId = employee.copy(assignedBranchIds = businessId)
        employees.add(employeeWithBusinessId)
    }

    /**
     * Helper method for tests to simulate a network error.
     */
    fun setNetworkError(error: Throwable) {
        networkError = error
    }

    /**
     * Helper method to clear the network error.
     */
    fun clearNetworkError() {
        networkError = null
    }

    fun clear() {
        employees.clear()
        networkError = null
        findEmployeeByIdentifierCallCount = 0
        addEmployeeCallCount = 0
    }
}
