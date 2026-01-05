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
import com.casecode.pos.core.model.SessionStateResult
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.testing.repository.business.TestEmployeeRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class UpdateEmployeeUseCaseTest {
    private lateinit var testEmployeeRepository: TestEmployeeRepository
    private lateinit var testSessionRepository: TestSessionRepository
    private lateinit var getCurrentSessionUseCase: GetCurrentSessionUseCase
    private lateinit var updateEmployeeUseCase: UpdateEmployeeUseCase

    @Before
    fun setup() {
        testEmployeeRepository = TestEmployeeRepository()
        testSessionRepository = TestSessionRepository()
        getCurrentSessionUseCase = GetCurrentSessionUseCase(testSessionRepository)
        updateEmployeeUseCase = UpdateEmployeeUseCase(testEmployeeRepository, getCurrentSessionUseCase)
    }

    @Test
    fun whenActiveSession_UpdatesEmployeeSuccessfully() = runTest {
        val businessId = "business_1"
        testSessionRepository.setSessionInfo(
            SessionStateResult.OwnerLoggedIn(
                businessId = businessId,
                activeBranchId = "branch_1",
                userId = "owner_1",
                userName = "Owner",
            ),
        )
        val employee = Employee(
            id = "emp_1",
            name = "Employee 1",
            phone = "123",
            role = EmployeeRole.CASHIER,
            assignedBranchId = "branch_1",
        )
        testEmployeeRepository.sendEmployees(listOf(employee))
        val updatedEmployee = employee.copy(name = "Updated Employee")

        val result = updateEmployeeUseCase(updatedEmployee, "password")

        assertTrue(result.isSuccess)
    }

    @Test
    fun whenNoActiveSession_ReturnsNoActiveSessionException() = runTest {
        testSessionRepository.setSessionInfo(SessionStateResult.None)
        val employee = Employee(
            id = "emp_1",
            name = "Employee 1",
            phone = "123",
            role = EmployeeRole.CASHIER,
            assignedBranchId = "branch_1",
        )

        val result = updateEmployeeUseCase(employee, "password")

        assertTrue(result.isFailure)
        assertIs<NoActiveSessionException>(result.exceptionOrNull())
    }
}
