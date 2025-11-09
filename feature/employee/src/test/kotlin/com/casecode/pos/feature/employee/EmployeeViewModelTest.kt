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
package com.casecode.pos.feature.employee

import com.casecode.pos.core.domain.usecase.CreateEmployeeUseCase
import com.casecode.pos.core.domain.usecase.DeleteEmployeeUseCase
import com.casecode.pos.core.domain.usecase.GetBranchesUseCase
import com.casecode.pos.core.domain.usecase.GetCurrentSessionUseCase
import com.casecode.pos.core.domain.usecase.GetEmployeesUseCase
import com.casecode.pos.core.domain.usecase.UpdateEmployeeUseCase
import com.casecode.pos.core.model.SessionStateResult
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.testing.repository.business.TestBranchRepository
import com.casecode.pos.core.testing.repository.business.TestEmployeeRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals
import com.casecode.pos.core.ui.R.string as uiString

class EmployeeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // SUT
    private lateinit var viewModel: EmployeeViewModel

    // Test Doubles
    private lateinit var employeeRepository: TestEmployeeRepository
    private lateinit var branchRepository: TestBranchRepository
    private lateinit var sessionRepository: TestSessionRepository

    // Real Use Cases with Test Doubles
    private lateinit var createEmployeeUseCase: CreateEmployeeUseCase
    private lateinit var updateEmployeeUseCase: UpdateEmployeeUseCase
    private lateinit var getEmployeesUseCase: GetEmployeesUseCase
    private lateinit var deleteEmployeeUseCase: DeleteEmployeeUseCase
    private lateinit var getBranchesUseCase: GetBranchesUseCase
    private lateinit var getCurrentSessionUseCase: GetCurrentSessionUseCase
    private val ownerSession =
        SessionStateResult.OwnerLoggedIn("business1", "branch22", "122", "mahmoud")

    @Before
    fun setup() {
        employeeRepository = TestEmployeeRepository()
        branchRepository = TestBranchRepository()
        sessionRepository = TestSessionRepository()

        getEmployeesUseCase = GetEmployeesUseCase(employeeRepository)
        getBranchesUseCase = GetBranchesUseCase(branchRepository)
        getCurrentSessionUseCase = GetCurrentSessionUseCase(sessionRepository)
        createEmployeeUseCase = CreateEmployeeUseCase(
            employeeRepository,
            getCurrentSessionUseCase = getCurrentSessionUseCase,
        )
        updateEmployeeUseCase = UpdateEmployeeUseCase(
            employeeRepository,
            getCurrentSessionUseCase = getCurrentSessionUseCase,
        )
        deleteEmployeeUseCase = DeleteEmployeeUseCase(
            employeeRepository,
            getCurrentSessionUseCase = getCurrentSessionUseCase,
        )
        viewModel = EmployeeViewModel(
            createEmployeeUseCase = createEmployeeUseCase,
            getEmployeesUseCase = getEmployeesUseCase,
            getBranchesUseCase = getBranchesUseCase,
            updateEmployeeUseCase = updateEmployeeUseCase,
            deleteEmployeeUseCase = deleteEmployeeUseCase,
        )
    }

    @Test
    fun createEmployee_whenValidDataAndActiveSession_returnMessageSuccess() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged("employee11"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePhoneChanged("1212321312"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePasswordChanged("123456666"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeRoleChanged(EmployeeRole.CASHIER))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeBranchAssigned("branch1"))
        sessionRepository.setSessionInfo(ownerSession)

        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        assertEquals(
            uiString.core_ui_success_add_employee_message,
            viewModel.uiState.value.userMessage,
        )
    }

    @Test
    fun createEmployee_whenValidDataAndNoActiveSession_returnMessageFailure() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged("employee11"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePhoneChanged("1212321312"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePasswordChanged("123456666"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeRoleChanged(EmployeeRole.CASHIER))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeBranchAssigned("branch1"))

        sessionRepository.setSessionInfo(SessionStateResult.None)
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        assertEquals(
            uiString.core_ui_error_no_active_session,

            viewModel.uiState.value.userMessage,
        )
    }

    @Test
    fun createEmployee_whenNameDuplicate_returnMessageFailure() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged("John Doe"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePhoneChanged("1212321312"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePasswordChanged("123456666"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeRoleChanged(EmployeeRole.CASHIER))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeBranchAssigned("branch1"))

        sessionRepository.setSessionInfo(ownerSession)
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        assertEquals(
            uiString.core_ui_error_employee_name_duplicate,

            viewModel.uiState.value.userMessage,
        )
    }

    @Test
    fun createEmployee_whenInvalidPassword_returnMessageFailure() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged("employee11"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePhoneChanged("1212321312"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePasswordChanged("1234"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeRoleChanged(EmployeeRole.CASHIER))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeBranchAssigned("branch1"))

        sessionRepository.setSessionInfo(ownerSession)
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        assertEquals(
            uiString.core_ui_error_add_employee_password_less_than_six,
            viewModel.employeeFormUiState.value.formErrors.passwordError,
        )
    }

    @Test
    fun createEmployee_whenInvalidData_returnMessageFailure() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged(""))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePhoneChanged(""))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePasswordChanged("12345"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeRoleChanged(EmployeeRole.CASHIER))

        sessionRepository.setSessionInfo(ownerSession)
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)
        assertEquals(
            uiString.core_ui_error_employee_name_empty,
            viewModel.employeeFormUiState.value.formErrors.nameError,
        )
        assertEquals(
            uiString.core_ui_error_phone_empty,
            viewModel.employeeFormUiState.value.formErrors.phoneError,
        )
        assertEquals(
            uiString.core_ui_error_add_employee_password_less_than_six,
            viewModel.employeeFormUiState.value.formErrors.passwordError,
        )
        assertEquals(
            uiString.core_ui_error_branch_name_empty,
            viewModel.employeeFormUiState.value.formErrors.assignedBranchesError,
        )
    }

    @Test
    fun createEmployee_whenHasError_returnErrorMessage() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged("employee11"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePhoneChanged("1212321312"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePasswordChanged("123456666"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeRoleChanged(EmployeeRole.CASHIER))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeBranchAssigned("branch1"))
        sessionRepository.setSessionInfo(ownerSession)

        employeeRepository.setFailure(IOException("network error"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        assertEquals(
            uiString.core_ui_error_add_employee_message,
            viewModel.uiState.value.userMessage,
        )
    }
}
