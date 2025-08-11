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

import com.casecode.pos.core.domain.utils.OwnerLoginResult
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.BranchStatus
import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.BusinessStatus
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.model.users.User
import com.casecode.pos.core.testing.repository.business.TestAuthRepository
import com.casecode.pos.core.testing.repository.business.TestBranchRepository
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Clock

class SignInOwnerUseCaseTest {

    @get:Rule
    val coroutinesRule = CoroutinesTestRule()

    private lateinit var testAuthRepository: TestAuthRepository
    private lateinit var testBusinessRepository: TestBusinessRepository
    private lateinit var testBranchRepository: TestBranchRepository
    private lateinit var testSessionRepository: TestSessionRepository

    // The System Under Test (SUT)
    private lateinit var signInOwnerUseCase: SignInOwnerUseCase

    // Test data
    private val testToken = "valid_google_id_token"
    private val testUser = User(
        uid = "owner123",
        email = "owner@test.com",
        name = "Test Owner",
        photoUrl = "",
    )
    private val testBusiness = Business(
        id = "biz456", name = "Test Cafe", ownerUid = "owner123", status = BusinessStatus.ACTIVE,
        vertical = Vertical.RETAIL,
        companyCode = "12321",
        currencyCode = "egp",
        email = "test@pos.com",
        phone = "12345666",
        updatedAt = Clock.System.now(),
        createdAt = Clock.System.now(),
    )
    private val testBranches = listOf(
        Branch(
            id = "branch789",
            name = "Main Street",
            phone = "12",
            status = BranchStatus.OPEN,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now(),
        ),
    )

    @Before
    fun setup() {
        testAuthRepository = TestAuthRepository()
        testBusinessRepository = TestBusinessRepository()
        testBranchRepository = TestBranchRepository()
        testSessionRepository = TestSessionRepository()

        signInOwnerUseCase = SignInOwnerUseCase(
            authRepository = testAuthRepository,
            businessRepository = testBusinessRepository,
            branchRepository = testBranchRepository,
            sessionRepository = testSessionRepository,
            logService = TestLogService(),
        )
    }

    @Test
    fun `when user has business and branches should return Success`() = runTest {
        // Arrange
        testAuthRepository.sendSignInSuccess(testUser)
        testBusinessRepository.addBusiness(testBusiness)
        testBranchRepository.setBranchesForBusiness(testBusiness.id, testBranches)

        // Act
        val result = signInOwnerUseCase(testToken)

        // Assert
        assertEquals(OwnerLoginResult.Success, result, "Result should be the Success object")

        // Assert side-effects
        assertEquals(
            testUser,
            testSessionRepository.lastOwnerSession,
            "A session should have been started for the correct user",
        )
    }

    @Test
    fun `when google sign-in fails should return AuthenticationFailed`() = runTest {
        // Arrange
        val authError = Exception("Invalid Token")
        testAuthRepository.sendSignInFailure(authError)

        // Act
        val result = signInOwnerUseCase(testToken)

        // Assert
        assertEquals(OwnerLoginResult.AuthenticationFailed, result)
        assertNull(
            testSessionRepository.lastOwnerSession,
            "No session should be started on auth failure",
        )
    }

    @Test
    fun `when user authenticates but has no business document should return AccountNeedsOnboarding`() =
        runTest {
            // Arrange
            testAuthRepository.sendSignInSuccess(testUser)
            // Note: No business is added to the business repository.

            // Act
            val result = signInOwnerUseCase(testToken)

            // Assert
            assertEquals(OwnerLoginResult.AccountNeedsOnboarding, result)
        }

    @Test
    fun `when business status is PENDING_ONBOARDING should return AccountNeedsOnboarding`() =
        runTest {
            // Arrange
            testAuthRepository.sendSignInSuccess(testUser)
            val pendingBusiness = testBusiness.copy(status = BusinessStatus.PENDING_ONBOARDING)
            testBusinessRepository.addBusiness(pendingBusiness)

            // Act
            val result = signInOwnerUseCase(testToken)

            // Assert
            assertEquals(OwnerLoginResult.AccountNeedsOnboarding, result)
        }

    @Test
    fun `when getting business fails with IOException should return NetworkError`() =
        runTest {
            // Arrange
            testAuthRepository.sendSignInSuccess(testUser)
            testBusinessRepository.setFailure(IOException("Network unavailable"))

            // Act
            val result = signInOwnerUseCase(testToken)

            // Assert
            assertEquals(OwnerLoginResult.NetworkError, result)
        }

    @Test
    fun `when getting business fails with other Exception should return GeneralError`() =
        runTest {
            // Arrange
            val genericError = IllegalStateException("Firestore corrupted")
            testAuthRepository.sendSignInSuccess(testUser)
            testBusinessRepository.setFailure(genericError)

            // Act
            val result = signInOwnerUseCase(testToken)

            // Assert
            assertIs<OwnerLoginResult.GeneralError>(result)
            assertEquals(genericError, result.exception)
        }

    @Test
    fun `when business is active but has no branches should return AccountNeedsOnboarding`() =
        runTest {
            // Arrange
            testAuthRepository.sendSignInSuccess(testUser)
            testBusinessRepository.addBusiness(testBusiness)
            testBranchRepository.setBranchesForBusiness(testBusiness.id, emptyList())

            // Act
            val result = signInOwnerUseCase(testToken)

            // Assert
            assertEquals(OwnerLoginResult.AccountNeedsOnboarding, result)
        }
}