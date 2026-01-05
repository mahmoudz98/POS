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

import com.casecode.pos.core.model.SessionStateResult
import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.BusinessStatus
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Instant

class GetCompanyCodeUseCaseTest {
    private lateinit var testSessionRepository: TestSessionRepository
    private lateinit var testBusinessRepository: TestBusinessRepository
    private lateinit var getCurrentSessionUseCase: GetCurrentSessionUseCase
    private lateinit var getCompanyCodeUseCase: GetCompanyCodeUseCase

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

    @Before
    fun setup() {
        testSessionRepository = TestSessionRepository()
        testBusinessRepository = TestBusinessRepository()
        getCurrentSessionUseCase = GetCurrentSessionUseCase(testSessionRepository)
        getCompanyCodeUseCase = GetCompanyCodeUseCase(getCurrentSessionUseCase, testBusinessRepository)
    }

    @Test
    fun ownerLoggedIn_ReturnsCompanyCode() = runTest {
        testBusinessRepository.addBusiness(testBusiness)
        testSessionRepository.setSessionInfo(
            SessionStateResult.OwnerLoggedIn(
                businessId = "owner123",
                activeBranchId = "branch1",
                userId = "owner123",
                userName = "Owner",
            ),
        )

        val result = getCompanyCodeUseCase().first()

        assertEquals(testBusiness.companyCode, result)
    }

    @Test
    fun employeeLoggedIn_ReturnsCompanyCode() = runTest {
        testBusinessRepository.addBusiness(testBusiness)
        testSessionRepository.setSessionInfo(
            SessionStateResult.EmployeeLoggedIn(
                businessId = "owner123",
                activeBranchId = "branch1",
                userId = "emp1",
                userName = "Employee",
                role = EmployeeRole.CASHIER,
            ),
        )

        val result = getCompanyCodeUseCase().first()

        assertEquals(testBusiness.companyCode, result)
    }

    @Test
    fun noSession_ReturnsNull() = runTest {
        testSessionRepository.setSessionInfo(SessionStateResult.None)

        val result = getCompanyCodeUseCase().first()

        assertNull(result)
    }
}
