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

import com.casecode.pos.core.domain.model.OwnerLoginStatus
import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.BusinessStatus
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.model.users.User
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import com.casecode.pos.core.testing.services.TestLogService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class CheckOwnerLoginStatusUseCaseTest {
    private lateinit var testBusinessRepository: TestBusinessRepository
    private lateinit var checkOwnerLoginStatusUseCase: CheckOwnerLoginStatusUseCase

    private val testUser = User(
        uid = "user123",
        email = "test@example.com",
        name = "test",
        photoUrl = null,
    )
    private val testBusiness = Business(
        id = "business123",
        name = "Test Business",
        ownerUid = "user123",
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
        testBusinessRepository = TestBusinessRepository()
        checkOwnerLoginStatusUseCase = CheckOwnerLoginStatusUseCase(
            businessRepository = testBusinessRepository,
            logService = TestLogService(),
        )
    }

    @Test
    fun activeBusiness_returnsActiveStatus() = runTest {
        // Given
        testBusinessRepository.addBusiness(testBusiness)

        // When
        val result = checkOwnerLoginStatusUseCase(testUser)

        // Then
        assertTrue(result.isSuccess)
        val status = result.getOrNull()
        assertTrue(status is OwnerLoginStatus.Active)
        assertEquals(testBusiness, status.business)
    }

    @Test
    fun pendingOnboardingBusiness_returnsOnboardingPendingStatus() = runTest {
        // Given
        val pendingBusiness = testBusiness.copy(status = BusinessStatus.PENDING_ONBOARDING)
        testBusinessRepository.addBusiness(pendingBusiness)

        // When
        val result = checkOwnerLoginStatusUseCase(testUser)

        // Then
        assertTrue(result.isSuccess)
        val status = result.getOrNull()
        assertTrue(status is OwnerLoginStatus.OnboardingPending)
        assertEquals(pendingBusiness, status.business)
    }

    @Test
    fun inactiveBusiness_returnsOnboardingPendingStatus() = runTest {
        // Given
        val inactiveBusiness = testBusiness.copy(status = BusinessStatus.INACTIVE)
        testBusinessRepository.addBusiness(inactiveBusiness)

        // When
        val result = checkOwnerLoginStatusUseCase(testUser)

        // Then
        assertTrue(result.isSuccess)
        val status = result.getOrNull()
        assertTrue(status is OwnerLoginStatus.OnboardingPending)
    }

    @Test
    fun noBusinessFound_returnsNeedsOnboardingStatus() = runTest {
        // Given
        testBusinessRepository.clear()

        // When
        val result = checkOwnerLoginStatusUseCase(testUser)

        // Then
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull() is OwnerLoginStatus.NeedsOnboarding)
    }

    @Test
    fun repositoryFailure_returnsFailure() = runTest {
        // Given
        testBusinessRepository.setFailure(Exception("Database error"))

        // When
        val result = checkOwnerLoginStatusUseCase(testUser)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Database error", result.exceptionOrNull()?.message)
    }
}
