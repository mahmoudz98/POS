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

import com.casecode.pos.core.domain.model.EmployeeLoginResult
import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.BusinessStatus
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import com.casecode.pos.core.testing.repository.business.TestEmployeeRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.services.TestLogService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class SignInEmployeeUseCaseTest {
    private lateinit var testEmployeeRepository: TestEmployeeRepository
    private lateinit var testSessionRepository: TestSessionRepository
    private lateinit var testBusinessRepository: TestBusinessRepository
    private lateinit var signInEmployeeUseCase: SignInEmployeeUseCase

    private val testBusiness = Business(
        id = "business123",
        name = "Test Business",
        ownerUid = "owner123",
        vertical = Vertical.RETAIL,
        companyCode = "bus-BUBU",
        currencyCode = "USD",
        status = BusinessStatus.ACTIVE,
        email = "test@business.com",
        phone = "1234567890",
        updatedAt = Instant.parse("2024-01-01T00:00:00Z"),
        createdAt = Instant.parse("2024-01-01T00:00:00Z"),
    )
    private val testEmployee = Employee(
        id = "emp1",
        name = "John Doe",
        phone = "123456789",
        role = EmployeeRole.CASHIER,
        assignedBranchId = "branch1",
    )

    @Before
    fun setup() {
        testEmployeeRepository = TestEmployeeRepository()
        testSessionRepository = TestSessionRepository()
        testBusinessRepository = TestBusinessRepository()
        signInEmployeeUseCase = SignInEmployeeUseCase(
            employeeRepository = testEmployeeRepository,
            sessionRepository = testSessionRepository,
            logService = TestLogService(),
            businessRepository = testBusinessRepository,
        )
    }

    @Test
    fun whenInvalidBusiness_returnsInvalidCompanyCode() = runTest {
        val result = signInEmployeeUseCase("invalid_code", "emp1", "pass")
        assertEquals(EmployeeLoginResult.InvalidCompanyCode, result)
    }

    @Test
    fun whenEmployeeNotFound_returnsEmployeeNotFound() = runTest {
        testBusinessRepository.addBusiness(testBusiness)
        testEmployeeRepository.throwEmployeeNotFound = true

        val result = signInEmployeeUseCase(testBusiness.companyCode, "emp1", "pass")

        assertEquals(EmployeeLoginResult.EmployeeNotFound, result)
    }

    @Test
    fun whenInvalidPassword_returnsInvalidCredentials() = runTest {
        testBusinessRepository.addBusiness(testBusiness)
        testEmployeeRepository.throwInvalidPassword = true

        val result = signInEmployeeUseCase(testBusiness.companyCode, "emp1", "wrong_pass")

        assertEquals(EmployeeLoginResult.InvalidCredentials, result)
    }

    @Test
    fun whenValidCredentials_returnsSuccess() = runTest {
        testBusinessRepository.addBusiness(testBusiness)
        testEmployeeRepository.employee = testEmployee

        val result = signInEmployeeUseCase(testBusiness.companyCode, "emp1", "correct_pass")

        assertEquals(EmployeeLoginResult.Success, result)
    }

    @Test
    fun whenNetworkError_returnsNetworkError() = runTest {
        testBusinessRepository.addBusiness(testBusiness)
        testEmployeeRepository.throwIOException = true

        val result = signInEmployeeUseCase(testBusiness.companyCode, "emp1", "pass")

        assertEquals(EmployeeLoginResult.NetworkError, result)
    }
}
