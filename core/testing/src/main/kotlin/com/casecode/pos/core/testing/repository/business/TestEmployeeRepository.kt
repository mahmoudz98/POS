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

import com.casecode.pos.core.domain.repository.business.EmployeeRepository
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.testing.base.FakeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestEmployeeRepository @Inject constructor() : FakeRepository(), EmployeeRepository {

    private val employeesByBusiness = mutableMapOf<String, MutableList<Employee>>()

    override suspend fun authenticateEmployee(
        companyCode: String,
        employeeIdentifier: String,
        password: String,
    ): Result<Pair<String, Employee>?> {
        getFailureResult<Pair<String, Employee>?>()?.let { return it }

        // In a real test, we would look up business by company code first.
        // For this fake, we can assume the business exists and just find the employee.
        val foundEmployee = employeesByBusiness.values.flatten()
            .find { it.employeeId == employeeIdentifier }

        return Result.success(
            foundEmployee?.let { Pair(it.businessId, it) },
        )
    }

    fun addEmployee(businessId: String, employee: Employee) {
        val employeeList = employeesByBusiness.getOrPut(businessId) { mutableListOf() }
        employeeList.add(employee)
    }

    override fun clear() {
        employeesByBusiness.clear()
        returnSuccess()
    }
}
