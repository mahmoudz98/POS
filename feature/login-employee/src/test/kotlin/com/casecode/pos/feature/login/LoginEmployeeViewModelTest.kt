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

import com.casecode.pos.core.domain.usecase.SignInEmployeeUseCase
import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.BusinessStatus
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import com.casecode.pos.core.testing.repository.business.TestEmployeeRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.util.MainDispatcherRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor
import com.casecode.pos.feature.login.employee.LoginEmployeeFormEvent
import com.casecode.pos.feature.login.employee.LoginEmployeeViewModel
import com.casecode.pos.feature.login.employee.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import com.casecode.pos.core.ui.R as CoreResource

class LoginEmployeeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: LoginEmployeeViewModel

    private lateinit var networkMonitor: TestNetworkMonitor
    private lateinit var employeeRepository: TestEmployeeRepository
    private lateinit var sessionRepository: TestSessionRepository
    private lateinit var businessRepository: TestBusinessRepository

    private lateinit var signInEmployeeUseCase: SignInEmployeeUseCase

    private val testCompanyCode = "TEST123"
    private val testEmployeeId = "emp001"
    private val testPassword = "password123"
    private val testBusinessId = "biz001"

    private val testBusiness = Business(
        id = testBusinessId,
        name = "Test Business",
        ownerUid = "owner123",
        vertical = Vertical.RETAIL,
        companyCode = testCompanyCode,
        currencyCode = "USD",
        status = BusinessStatus.ACTIVE,
        email = "test@business.com",
        phone = "1234567890",
        updatedAt = Clock.System.now(),
        createdAt = Clock.System.now(),
    )

    private val testEmployee = Employee(
        id = testEmployeeId,
        name = "Test Employee",
        phone = "123",
        role = EmployeeRole.CASHIER,
        assignedBranchId = "branch1",
    )

    @Before
    fun setup() {
        networkMonitor = TestNetworkMonitor()
        employeeRepository = TestEmployeeRepository()
        sessionRepository = TestSessionRepository()
        businessRepository = TestBusinessRepository()

        businessRepository.addBusiness(testBusiness)
        networkMonitor.setConnected(true)

        signInEmployeeUseCase = SignInEmployeeUseCase(
            employeeRepository = employeeRepository,
            sessionRepository = sessionRepository,
            logService = TestLogService(),
            businessRepository = businessRepository,
        )

        viewModel = LoginEmployeeViewModel(
            networkMonitor = networkMonitor,
            signInEmployeeUseCase = signInEmployeeUseCase,
        )
    }

    @Test
    fun `when company code changed updates form state and clears error`() {
        viewModel.onFormEvent(LoginEmployeeFormEvent.CompanyCodeChanged("TEST123"))

        assertEquals("TEST123", viewModel.formState.value.companyCode)
        assertNull(viewModel.formState.value.companyCodeError)
    }

    @Test
    fun `when employee id changed updates form state and clears error`() {
        viewModel.onFormEvent(LoginEmployeeFormEvent.EmployeeIdChanged("emp001"))

        assertEquals("emp001", viewModel.formState.value.employeeId)
        assertNull(viewModel.formState.value.employeeIdError)
    }

    @Test
    fun `when password changed updates form state and clears error`() {
        viewModel.onFormEvent(LoginEmployeeFormEvent.PasswordChanged("secret"))

        assertEquals("secret", viewModel.formState.value.password)
        assertNull(viewModel.formState.value.passwordError)
    }

    @Test
    fun `when submit clicked with empty fields shows validation errors`() {
        viewModel.onFormEvent(LoginEmployeeFormEvent.SubmitClicked)

        val state = viewModel.formState.value
        assertTrue(state.companyCodeError != null)
        assertTrue(state.employeeIdError != null)
        assertTrue(state.passwordError != null)
        assertFalse(state.isValid)
    }

    @Test
    fun `when login succeeds sets isLoginSuccess to true`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.loginEmployeeUiState.collect()
        }

        employeeRepository.employee = testEmployee

        viewModel.onFormEvent(LoginEmployeeFormEvent.CompanyCodeChanged(testCompanyCode))
        viewModel.onFormEvent(LoginEmployeeFormEvent.EmployeeIdChanged(testEmployeeId))
        viewModel.onFormEvent(LoginEmployeeFormEvent.PasswordChanged(testPassword))

        viewModel.onFormEvent(LoginEmployeeFormEvent.SubmitClicked)
        advanceUntilIdle()

        val state = viewModel.loginEmployeeUiState.value
        assertTrue(state.isLoginSuccess)
        assertFalse(state.inProgressLoginEmployee)
        assertNull(state.userMessage)
    }

    @Test
    fun `when invalid credentials shows error`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.loginEmployeeUiState.collect()
        }

        employeeRepository.throwInvalidPassword = true

        viewModel.onFormEvent(LoginEmployeeFormEvent.CompanyCodeChanged(testCompanyCode))
        viewModel.onFormEvent(LoginEmployeeFormEvent.EmployeeIdChanged(testEmployeeId))
        viewModel.onFormEvent(LoginEmployeeFormEvent.PasswordChanged("wrong"))

        viewModel.onFormEvent(LoginEmployeeFormEvent.SubmitClicked)
        advanceUntilIdle()

        val state = viewModel.loginEmployeeUiState.value
        assertFalse(state.isLoginSuccess)
        assertFalse(state.inProgressLoginEmployee)
        assertEquals(R.string.feature_login_employee_error_employee_incorrect, state.userMessage)
    }

    @Test
    fun `when invalid company code shows error`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.loginEmployeeUiState.collect()
        }

        viewModel.onFormEvent(LoginEmployeeFormEvent.CompanyCodeChanged("WRONG"))
        viewModel.onFormEvent(LoginEmployeeFormEvent.EmployeeIdChanged(testEmployeeId))
        viewModel.onFormEvent(LoginEmployeeFormEvent.PasswordChanged(testPassword))

        viewModel.onFormEvent(LoginEmployeeFormEvent.SubmitClicked)
        advanceUntilIdle()

        val state = viewModel.loginEmployeeUiState.value
        assertEquals(R.string.feature_login_employee_error_employee_incorrect, state.userMessage)
    }

    @Test
    fun `when employee not found shows error`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.loginEmployeeUiState.collect()
        }

        employeeRepository.throwEmployeeNotFound = true

        viewModel.onFormEvent(LoginEmployeeFormEvent.CompanyCodeChanged(testCompanyCode))
        viewModel.onFormEvent(LoginEmployeeFormEvent.EmployeeIdChanged("ghost"))
        viewModel.onFormEvent(LoginEmployeeFormEvent.PasswordChanged(testPassword))

        viewModel.onFormEvent(LoginEmployeeFormEvent.SubmitClicked)
        advanceUntilIdle()

        val state = viewModel.loginEmployeeUiState.value
        assertEquals(R.string.feature_login_employee_error_employee_incorrect, state.userMessage)
    }

    @Test
    fun `when network error shows network error message`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.loginEmployeeUiState.collect()
        }

        employeeRepository.throwIOException = true

        viewModel.onFormEvent(LoginEmployeeFormEvent.CompanyCodeChanged(testCompanyCode))
        viewModel.onFormEvent(LoginEmployeeFormEvent.EmployeeIdChanged(testEmployeeId))
        viewModel.onFormEvent(LoginEmployeeFormEvent.PasswordChanged(testPassword))

        viewModel.onFormEvent(LoginEmployeeFormEvent.SubmitClicked)
        advanceUntilIdle()

        val state = viewModel.loginEmployeeUiState.value
        assertEquals(CoreResource.string.core_ui_error_network, state.userMessage)
    }

    @Test
    fun `when general error shows generic error message`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.loginEmployeeUiState.collect()
        }

        employeeRepository.throwGeneralException = IllegalStateException("Unexpected error")

        viewModel.onFormEvent(LoginEmployeeFormEvent.CompanyCodeChanged(testCompanyCode))
        viewModel.onFormEvent(LoginEmployeeFormEvent.EmployeeIdChanged(testEmployeeId))
        viewModel.onFormEvent(LoginEmployeeFormEvent.PasswordChanged(testPassword))

        viewModel.onFormEvent(LoginEmployeeFormEvent.SubmitClicked)
        advanceUntilIdle()

        val state = viewModel.loginEmployeeUiState.value
        assertEquals(R.string.feature_login_employee_error_login, state.userMessage)
    }

    @Test
    fun `when offline and submit shows network error`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.loginEmployeeUiState.collect()
        }

        networkMonitor.setConnected(false)
        advanceUntilIdle()

        viewModel.onFormEvent(LoginEmployeeFormEvent.CompanyCodeChanged(testCompanyCode))
        viewModel.onFormEvent(LoginEmployeeFormEvent.EmployeeIdChanged(testEmployeeId))
        viewModel.onFormEvent(LoginEmployeeFormEvent.PasswordChanged(testPassword))

        viewModel.onFormEvent(LoginEmployeeFormEvent.SubmitClicked)

        val state = viewModel.loginEmployeeUiState.value
        assertEquals(CoreResource.string.core_ui_error_network, state.userMessage)
    }

    @Test
    fun `when snackbar message shown clears user message`() {
        viewModel.showMessageLoginEmployee(123)

        viewModel.snackbarMessageShownLoginEmployee()

        assertNull(viewModel.loginEmployeeUiState.value.userMessage)
    }
}
