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

import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.testing.repository.business.TestEmployeeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class GetEmployeesUseCaseTest {
    private lateinit var testEmployeeRepository: TestEmployeeRepository
    private lateinit var getEmployeesUseCase: GetEmployeesUseCase

    @Before
    fun setup() {
        testEmployeeRepository = TestEmployeeRepository()
        getEmployeesUseCase = GetEmployeesUseCase(testEmployeeRepository)
    }

    @Test
    fun employeesAvailable_ReturnsEmployees() = runTest {
        val employees = listOf(
            Employee(
                id = "1",
                name = "Emp 1",
                phone = "111",
                assignedBranchId = "b1",
                role = EmployeeRole.CASHIER,
            ),
            Employee(
                id = "2",
                name = "Emp 2",
                phone = "222",
                assignedBranchId = "b1",
                role = EmployeeRole.CASHIER,
            ),
        )
        testEmployeeRepository.sendEmployees(employees)

        val result = getEmployeesUseCase().first()

        assertEquals(employees, result)
    }
}
