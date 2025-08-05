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
import com.casecode.pos.core.domain.usecase.SignInOwnerUseCase
import com.casecode.pos.core.model.users.User
import com.casecode.pos.core.testing.repository.business.TestAuthRepository
import com.casecode.pos.core.testing.repository.business.TestBranchRepository
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.services.TestGoogleAuthUiClient
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.util.MainDispatcherRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: LoginViewModel

    // Mocks and Test Doubles
    private val mockContext: Context = mockk<Context>()
    private lateinit var testAuthRepository: TestAuthRepository
    private lateinit var testBusinessRepository: TestBusinessRepository
    private lateinit var testBranchRepository: TestBranchRepository
    private lateinit var testSessionRepository: TestSessionRepository
    private lateinit var testGoogleAuthUiClient: TestGoogleAuthUiClient

    private val testUser = User("uid1", "test@user.com", "Test User", null)

    @Before
    fun setup() {
        // Initialize all test doubles
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

        viewModel = LoginViewModel(
            signInOwnerUseCase = signInOwnerUseCase,
            googleAuthUiService = testGoogleAuthUiClient,
        )
    }

    @Test
    fun `initial state is Idle`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `given Play Services unavailable, when sign in event, then state is ShowPlayServicesUnavailableDialog`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        testGoogleAuthUiClient.setGooglePlayServicesAvailable(false)

        viewModel.onEvent(LoginEvent.GoogleSignInResult(mockContext))

        assertIs<LoginUiState.ShowPlayServicesUnavailableDialog>(viewModel.uiState.value)
    }

    @Test
    fun `given getIdToken fails, when sign in event, then state is Error`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        testGoogleAuthUiClient.setGetIdTokenResult(Result.failure(Exception()))

        viewModel.onEvent(LoginEvent.GoogleSignInResult(mockContext))

        val uiState = viewModel.uiState.value
        assertIs<LoginUiState.Error>(uiState)
        assertEquals(R.string.feature_login_error_network_connection, uiState.messageResId)
    }

    @Test
    fun `given sign in success and business exists, when sign in event, then state is Idle`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Arrange: Full success path
        testGoogleAuthUiClient.setGetIdTokenResult(Result.success("test_token"))
        testAuthRepository.sendSignInSuccess(testUser)

        // Act
        viewModel.onEvent(LoginEvent.GoogleSignInResult(mockContext))

        // Assert: ViewModel resets to Idle after successful login flow
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `given auth fails, when sign in event, then state is Error`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Arrange: Auth repository is set to fail
        testGoogleAuthUiClient.setGetIdTokenResult(Result.success("test_token"))
        testAuthRepository.sendSignInFailure(Exception("Auth failed"))

        // Act
        viewModel.onEvent(LoginEvent.GoogleSignInResult(mockContext))

        // Assert
        val uiState = viewModel.uiState.value
        assertIs<LoginUiState.Error>(uiState)
        assertEquals(R.string.feature_login_error_unknown, uiState.messageResId)
    }

    @Test
    fun `given business does not exist, when sign in event, then state is Idle`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Arrange: Auth succeeds, but no business is found (onboarding case)
        testGoogleAuthUiClient.setGetIdTokenResult(Result.success("test_token"))
        testAuthRepository.sendSignInSuccess(testUser)

        // Act
        viewModel.onEvent(LoginEvent.GoogleSignInResult(mockContext))

        // Assert: ViewModel resets to Idle after onboarding flow
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `given branches do not exist, when sign in event, then state is Idle`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // Arrange: Auth and business fetch succeed, but no branches found (onboarding case)
        testGoogleAuthUiClient.setGetIdTokenResult(Result.success("test_token"))
        testAuthRepository.sendSignInSuccess(testUser)

        // Act
        viewModel.onEvent(LoginEvent.GoogleSignInResult(mockContext))

        // Assert: ViewModel resets to Idle after onboarding flow
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }
}