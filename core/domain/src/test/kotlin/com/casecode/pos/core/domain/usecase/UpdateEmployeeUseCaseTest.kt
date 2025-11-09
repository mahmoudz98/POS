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
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertTrue

class UpdateEmployeeUseCaseTest {

    private lateinit var employeeRepository: TestEmployeeRepository
    private lateinit var sessionRepository: TestSessionRepository
    private lateinit var updateEmployeeUseCase: UpdateEmployeeUseCase
    private lateinit var getCurrentSessionUseCase: GetCurrentSessionUseCase

    @Before
    fun setup() {
        employeeRepository = TestEmployeeRepository()
        sessionRepository = TestSessionRepository()
        getCurrentSessionUseCase = GetCurrentSessionUseCase(sessionRepository)
        updateEmployeeUseCase = UpdateEmployeeUseCase(employeeRepository, getCurrentSessionUseCase)
    }

    @Test
    fun `when`() = runTest {
        val employee = Employee(
            name = "John Doe",
            phone = "1234567890",
            role = EmployeeRole.CASHIER,
            assignedBranchId = "branch1",
        )
        sessionRepository.setSessionInfoOwnerLoggedIn()
        val result = updateEmployeeUseCase(
            employee,
            "",
        )
        assertTrue { result.isSuccess }
    }
}
