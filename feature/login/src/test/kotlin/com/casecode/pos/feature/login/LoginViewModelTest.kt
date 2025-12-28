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
package com.casecode.pos.feature.login

import android.content.Context
import com.casecode.pos.core.domain.model.OwnerLoginResult
import com.casecode.pos.core.domain.service.GoogleAuthUiClient
import com.casecode.pos.core.domain.usecase.GetCurrentUserUseCase
import com.casecode.pos.core.domain.usecase.SignInOwnerUseCase
import com.casecode.pos.core.domain.usecase.StartOwnerSessionUseCase
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.users.User
import com.casecode.pos.core.testing.repository.business.TestAuthRepository
import com.casecode.pos.core.testing.repository.business.TestBranchRepository
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.services.TestGoogleAuthUiClient
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginViewModel

    private val mockContext: Context = mockk()
    private lateinit var testAuthRepository: TestAuthRepository
    private lateinit var testBusinessRepository: TestBusinessRepository
    private lateinit var testBranchRepository: TestBranchRepository
    private lateinit var testSessionRepository: TestSessionRepository
    private lateinit var testGoogleAuthUiClient: TestGoogleAuthUiClient

    private val testUser = User("uid1", "test@user.com", "Test User", null)
    private val testBranch = Branch("branch1", "Main Branch", "123456789", "business1")

    @Before
    fun setup() {
        testAuthRepository = TestAuthRepository()
        testBusinessRepository = TestBusinessRepository()
        testBranchRepository = TestBranchRepository()
        testSessionRepository = TestSessionRepository()
        testGoogleAuthUiClient = TestGoogleAuthUiClient()

        val signInOwnerUseCase = SignInOwnerUseCase(
            authRepository = testAuthRepository,
            businessRepository = testBusinessRepository,
            branchRepository = testBranchRepository,
            sessionRepository = testSessionRepository,
            logService = TestLogService(),
        )
        val getCurrentUserUseCase = GetCurrentUserUseCase(authRepository = testAuthRepository)
        val startOwnerSessionUseCase =
            StartOwnerSessionUseCase(testSessionRepository, testBranchRepository)

        viewModel = LoginViewModel(
            signInOwnerUseCase = signInOwnerUseCase,
            googleAuthUiClient = testGoogleAuthUiClient,
            getCurrentUserUseCase = getCurrentUserUseCase,
            startOwnerSessionUseCase = startOwnerSessionUseCase,
        )
    }

    @Test
    fun `initial state is Idle`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `given Play Services unavailable, when sign in event, then state is ShowPlayServicesUnavailableDialog`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testGoogleAuthUiClient.setGooglePlayServicesAvailable(false)
            viewModel.onEvent(LoginEvent.GoogleSignInResult(mockContext))

            assertIs<LoginUiState.ShowPlayServicesUnavailableDialog>(viewModel.uiState.value)
        }

    @Test
    fun `given getIdToken fails, when sign in event, then state is Error with network message`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testGoogleAuthUiClient.setGetIdTokenResult(Result.failure(Exception()))
            viewModel.onEvent(LoginEvent.GoogleSignInResult(mockContext))

            val uiState = viewModel.uiState.value
            assertIs<LoginUiState.Error>(uiState)
            assertEquals(R.string.feature_login_error_network_connection, uiState.messageResId)
        }

    @Test
    fun `given sign in success and business exists, when sign in event, then state is Idle`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testGoogleAuthUiClient.setGetIdTokenResult(Result.success("test_token"))
            testAuthRepository.sendSignInSuccess(testUser)
            // Add business to repository
            testBusinessRepository.addBusiness(testBusinessRepository.testBusiness.copy(ownerUid = testUser.uid))
            // Add branches to repository
            testBranchRepository.sendBranches(listOf(testBranch))

            viewModel.onEvent(LoginEvent.GoogleSignInResult(mockContext))
            advanceUntilIdle()

            assertEquals(LoginUiState.Idle, viewModel.uiState.value)
        }

    @Test
    fun `given auth fails, when sign in event, then state is Error with unknown message`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        testGoogleAuthUiClient.setGetIdTokenResult(Result.success("test_token"))
        testAuthRepository.sendSignInFailure(Exception("Auth failed"))

        viewModel.onEvent(LoginEvent.GoogleSignInResult(mockContext))

        val uiState = viewModel.uiState.value
        assertIs<LoginUiState.Error>(uiState)
        assertEquals(R.string.feature_login_error_unknown, uiState.messageResId)
    }

    @Test
    fun `given branch selection required, when sign in event, then state is BranchSelection`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testGoogleAuthUiClient.setGetIdTokenResult(Result.success("test_token"))

            // Use a mock UseCase to force a specific result that is hard to setup via integration
            val mockSignInOwnerUseCase = mockk<SignInOwnerUseCase>()
            val branches = listOf(testBranch, testBranch.copy(id = "branch2"))
            coEvery { mockSignInOwnerUseCase(any()) } returns
                OwnerLoginResult.BranchSelectionRequired(branches)

            val viewModelWithMock = LoginViewModel(
                signInOwnerUseCase = mockSignInOwnerUseCase,
                googleAuthUiClient = testGoogleAuthUiClient,
                getCurrentUserUseCase = GetCurrentUserUseCase(testAuthRepository),
                startOwnerSessionUseCase =
                StartOwnerSessionUseCase(testSessionRepository, testBranchRepository),
            )

            backgroundScope.launch(UnconfinedTestDispatcher()) {
                viewModelWithMock.uiState.collect()
            }
            viewModelWithMock.onEvent(LoginEvent.GoogleSignInResult(mockContext))

            val uiState = viewModelWithMock.uiState.value
            assertIs<LoginUiState.BranchSelection>(uiState)
            assertEquals(branches, uiState.branches)
        }

    @Test
    fun `given network error during sign in, then state is Error with network message`() = runTest {
        val mockSignInOwnerUseCase = mockk<SignInOwnerUseCase>()
        coEvery { mockSignInOwnerUseCase(any()) } returns OwnerLoginResult.NetworkError

        val viewModelWithMock = LoginViewModel(
            signInOwnerUseCase = mockSignInOwnerUseCase,
            googleAuthUiClient = testGoogleAuthUiClient,
            getCurrentUserUseCase = GetCurrentUserUseCase(testAuthRepository),
            startOwnerSessionUseCase =
            StartOwnerSessionUseCase(testSessionRepository, testBranchRepository),
        )
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModelWithMock.uiState.collect() }
        viewModelWithMock.onEvent(LoginEvent.GoogleSignInResult(mockContext))

        val uiState = viewModelWithMock.uiState.value
        assertIs<LoginUiState.Error>(uiState)
        assertEquals(R.string.feature_login_error_network_connection, uiState.messageResId)
    }

    @Test
    fun `given branch selected and user exists, when BranchSelected event, then start session and go to Idle`() =
        runTest {
            testAuthRepository.sendSignInSuccess(testUser)

            val mockStartOwnerSessionUseCase = mockk<StartOwnerSessionUseCase>()
            coEvery { mockStartOwnerSessionUseCase(any(), any()) } returns Result.success(Unit)

            val viewModelWithMock = LoginViewModel(
                signInOwnerUseCase = mockk(relaxed = true),
                googleAuthUiClient = testGoogleAuthUiClient,
                getCurrentUserUseCase = GetCurrentUserUseCase(testAuthRepository),
                startOwnerSessionUseCase = mockStartOwnerSessionUseCase,
            )

            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModelWithMock.uiState.collect() }
            viewModelWithMock.onEvent(LoginEvent.BranchSelected(testBranch))

            advanceUntilIdle()
            assertEquals(LoginUiState.Idle, viewModelWithMock.uiState.value)
        }

    @Test
    fun `given branch selected but user not found, when BranchSelected event, then state is Error`() =
        runTest {
            backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testAuthRepository.signOut()
            viewModel.onEvent(LoginEvent.BranchSelected(testBranch))

            advanceUntilIdle() // Ensure coroutine completes
            val uiState = viewModel.uiState.value
            assertIs<LoginUiState.Error>(uiState)
            assertEquals(R.string.feature_login_error_unknown, uiState.messageResId)
        }

    @Test
    fun `given branch selected but session start fails, when BranchSelected event, then state is Error`() =
        runTest {
            val mockStartOwnerSessionUseCase = mockk<StartOwnerSessionUseCase>()
            coEvery { mockStartOwnerSessionUseCase(any(), any()) } returns
                Result.failure(Exception("Session failed"))

            val viewModelWithMock = LoginViewModel(
                signInOwnerUseCase = mockk(relaxed = true),
                googleAuthUiClient = testGoogleAuthUiClient,
                getCurrentUserUseCase = GetCurrentUserUseCase(testAuthRepository),
                startOwnerSessionUseCase = mockStartOwnerSessionUseCase,
            )
            testAuthRepository.sendSignInSuccess(testUser)
            backgroundScope.launch(UnconfinedTestDispatcher()) {
                viewModelWithMock.uiState.collect()
            }

            viewModelWithMock.onEvent(LoginEvent.BranchSelected(testBranch))

            advanceUntilIdle() // Ensure coroutine completes
            val uiState = viewModelWithMock.uiState.value
            assertIs<LoginUiState.Error>(uiState)
            assertEquals(R.string.feature_login_error_unknown, uiState.messageResId)
        }

    @Test
    fun `given error state, when ErrorMessageShown event, then state is Idle`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        testGoogleAuthUiClient.setGetIdTokenResult(Result.failure(Exception()))
        viewModel.onEvent(LoginEvent.GoogleSignInResult(mockContext))
        assertIs<LoginUiState.Error>(viewModel.uiState.value)

        viewModel.onEvent(LoginEvent.ErrorMessageShown)
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `given PlayServicesDialog state, when DialogDismissed event, then state is Idle`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        testGoogleAuthUiClient.setGooglePlayServicesAvailable(false)
        viewModel.onEvent(LoginEvent.GoogleSignInResult(mockContext))
        assertIs<LoginUiState.ShowPlayServicesUnavailableDialog>(viewModel.uiState.value)

        viewModel.onEvent(LoginEvent.PlayServicesDialogDismissed)
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `given login is in progress, when second login event occurs, then it is ignored`() =
        runTest {
            // Battery Optimization: This test confirms that we don't spawn multiple expensive operations
            // concurrently, which saves battery and resources.

            val mockGoogleAuthUiClient = mockk<GoogleAuthUiClient>()
            every { mockGoogleAuthUiClient.isGooglePlayServicesAvailable(any()) } returns true

            // Simulate a long-running login process
            coEvery { mockGoogleAuthUiClient.getIdToken(any()) } coAnswers {
                delay(5000)
                Result.success("token")
            }

            // Explicitly mock UseCase to avoid NoWhenBranchMatchedException when the first call eventually completes
            val mockSignInOwnerUseCase = mockk<SignInOwnerUseCase>(relaxed = true)
            coEvery { mockSignInOwnerUseCase(any()) } returns OwnerLoginResult.Success

            val viewModelWithMock = LoginViewModel(
                signInOwnerUseCase = mockSignInOwnerUseCase,
                googleAuthUiClient = mockGoogleAuthUiClient,
                getCurrentUserUseCase = GetCurrentUserUseCase(testAuthRepository),
                startOwnerSessionUseCase =
                StartOwnerSessionUseCase(testSessionRepository, testBranchRepository),
            )

            backgroundScope.launch(UnconfinedTestDispatcher()) {
                viewModelWithMock.uiState.collect()
            }

            // Trigger first login
            viewModelWithMock.onEvent(LoginEvent.GoogleSignInResult(mockContext))

            assertIs<LoginUiState.Loading>(viewModelWithMock.uiState.value)

            // Trigger second login immediately
            viewModelWithMock.onEvent(LoginEvent.GoogleSignInResult(mockContext))

            // Verify getIdToken was called only once, meaning the second attempt was blocked
            coVerify(exactly = 1) { mockGoogleAuthUiClient.getIdToken(any()) }
        }
}
