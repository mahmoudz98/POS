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

import com.casecode.pos.core.domain.model.OwnerLoginResult
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
import kotlin.time.Instant

class SignInOwnerUseCaseTest {

    @get:Rule
    val coroutinesRule = CoroutinesTestRule()

    private lateinit var testAuthRepository: TestAuthRepository
    private lateinit var testBusinessRepository: TestBusinessRepository
    private lateinit var testBranchRepository: TestBranchRepository
    private lateinit var testSessionRepository: TestSessionRepository

    private lateinit var signInOwnerUseCase: SignInOwnerUseCase

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
        updatedAt = Instant.parse("2024-01-01T00:00:00Z"),
        createdAt = Instant.parse("2024-01-01T00:00:00Z"),
    )
    private val testBranches = listOf(
        Branch(
            id = "branch789",
            name = "Main Street",
            phone = "12",
            status = BranchStatus.OPEN,
            createdAt = Instant.parse("2024-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2024-01-01T00:00:00Z"),
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
    fun whenHasBusiness_ReturnsSuccess() = runTest {
        testAuthRepository.sendSignInSuccess(testUser)
        testBusinessRepository.addBusiness(testBusiness)
        testBranchRepository.setBranchesForBusiness(testBusiness.id, testBranches)

        val result = signInOwnerUseCase(testToken)

        assertEquals(OwnerLoginResult.Success, result)

        assertEquals(
            testUser,
            testSessionRepository.lastOwnerSession,
        )
    }

    @Test
    fun whenAuthFailure_ReturnsAuthenticationFailed() = runTest {
        val authError = Exception("Invalid Token")
        testAuthRepository.sendSignInFailure(authError)

        val result = signInOwnerUseCase(testToken)

        assertEquals(OwnerLoginResult.AuthenticationFailed, result)
        assertNull(
            testSessionRepository.lastOwnerSession,
        )
    }

    @Test
    fun whenNoBusiness_ReturnsAccountNeedsOnboarding() =
        runTest {
            testAuthRepository.sendSignInSuccess(testUser)

            val result = signInOwnerUseCase(testToken)

            assertEquals(OwnerLoginResult.AccountNeedsOnboarding, result)
        }

    @Test
    fun whenBusinessPendingOnboarding_ReturnsAccountNeedsOnboarding() =
        runTest {
            testAuthRepository.sendSignInSuccess(testUser)
            val pendingBusiness = testBusiness.copy(status = BusinessStatus.PENDING_ONBOARDING)
            testBusinessRepository.addBusiness(pendingBusiness)

            val result = signInOwnerUseCase(testToken)

            assertEquals(OwnerLoginResult.AccountNeedsOnboarding, result)
        }

    @Test
    fun whenNetworkError_ReturnsNetworkError() =
        runTest {
            testAuthRepository.sendSignInSuccess(testUser)
            testBusinessRepository.setFailure(IOException("Network unavailable"))

            val result = signInOwnerUseCase(testToken)

            assertEquals(OwnerLoginResult.NetworkError, result)
        }

    @Test
    fun whenGenericError_ReturnsGeneralError() =
        runTest {
            val genericError = IllegalStateException("Firestore corrupted")
            testAuthRepository.sendSignInSuccess(testUser)
            testBusinessRepository.setFailure(genericError)

            val result = signInOwnerUseCase(testToken)

            assertIs<OwnerLoginResult.GeneralError>(result)
            assertEquals(genericError, result.exception)
        }

    @Test
    fun whenActiveBusinessNoBranches_ReturnsSuccess() =
        runTest {
            testAuthRepository.sendSignInSuccess(testUser)
            testBusinessRepository.addBusiness(testBusiness)
            testBranchRepository.setBranchesForBusiness(testBusiness.id, emptyList())

            val result = signInOwnerUseCase(testToken)

            assertEquals(OwnerLoginResult.Success, result)
        }

    @Test
    fun whenMultipleBranches_ReturnsBranchSelectionRequired() = runTest {
        val branch1 = testBranches.first()
        val branch2 = branch1.copy(id = "branch2", name = "Second Branch")
        val multipleBranches = listOf(branch1, branch2)

        testAuthRepository.sendSignInSuccess(testUser)
        testBusinessRepository.addBusiness(testBusiness)
        testBranchRepository.setBranchesForBusiness(testBusiness.id, multipleBranches)

        val result = signInOwnerUseCase(testToken)

        assertEquals(OwnerLoginResult.BranchSelectionRequired(multipleBranches), result)
    }
}
