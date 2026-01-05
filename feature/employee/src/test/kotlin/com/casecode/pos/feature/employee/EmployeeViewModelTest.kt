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
import com.casecode.pos.core.domain.usecase.GenerateEmployeeIdUseCase
import com.casecode.pos.core.domain.usecase.GetBranchesUseCase
import com.casecode.pos.core.domain.usecase.GetCurrentSessionUseCase
import com.casecode.pos.core.domain.usecase.GetEmployeesUseCase
import com.casecode.pos.core.domain.usecase.UpdateEmployeeUseCase
import com.casecode.pos.core.model.SessionStateResult
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.testing.data.branchesTestData
import com.casecode.pos.core.testing.data.employeeTestData
import com.casecode.pos.core.testing.repository.business.TestBranchRepository
import com.casecode.pos.core.testing.repository.business.TestEmployeeRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
    private lateinit var generateEmployeeIdUseCase: GenerateEmployeeIdUseCase
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
        generateEmployeeIdUseCase = GenerateEmployeeIdUseCase(employeeRepository)
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
            getEmployeesUseCase = getEmployeesUseCase,
            getBranchesUseCase = getBranchesUseCase,
            generateEmployeeIdUseCase = generateEmployeeIdUseCase,
            createEmployeeUseCase = createEmployeeUseCase,
            updateEmployeeUseCase = updateEmployeeUseCase,
            deleteEmployeeUseCase = deleteEmployeeUseCase,
            getCurrentSessionUseCase = getCurrentSessionUseCase,
        )
    }

    @Test
    fun loadInitialData_whenOwnerLoggedIn_loadsEmployeesAndBranches() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val employees = employeeTestData
        val branches = branchesTestData
        // When
        branchRepository.sendBranches(branches)
        employeeRepository.sendEmployees(employees)
        sessionRepository.setSessionInfo(ownerSession)

        advanceUntilIdle()

        // Then
        assertEquals(employees, viewModel.uiState.value.employees)
        assertEquals(branches, viewModel.uiState.value.availableBranches)
        assertEquals(false, viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.userMessage)
    }

    @Test
    fun createEmployee_whenValidDataAndActiveSession_returnMessageSuccess() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        sessionRepository.setSessionInfo(ownerSession)

        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged("employee11"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePhoneChanged("1212321312"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePasswordChanged("123456666"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeRoleChanged(EmployeeRole.CASHIER))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeBranchAssigned("branch1"))

        // When
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        // Then
        assertEquals(
            uiString.core_ui_success_add_employee_message,
            viewModel.uiState.value.userMessage,
        )
    }

    @Test
    fun createEmployee_whenValidDataAndNoActiveSession_returnMessageFailure() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        sessionRepository.setSessionInfo(SessionStateResult.None)

        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged("employee11"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePhoneChanged("1212321312"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePasswordChanged("123456666"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeRoleChanged(EmployeeRole.CASHIER))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeBranchAssigned("branch1"))

        // When
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        // Then
        assertEquals(
            uiString.core_ui_error_no_active_session,
            viewModel.uiState.value.userMessage,
        )
    }

    @Test
    fun createEmployee_whenIdDuplicate_returnMessageFailure() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        employeeRepository.sendEmployees()
        branchRepository.sendBranches()
        sessionRepository.setSessionInfo(ownerSession)
        val duplicateId = viewModel.uiState.value.employees.first().id
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeIdChanged(duplicateId))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged("John Doe"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePhoneChanged("1212321312"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePasswordChanged("123456666"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeRoleChanged(EmployeeRole.CASHIER))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeBranchAssigned("branch1"))

        // When
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        // Then
        assertEquals(
            uiString.core_ui_error_employee_id_duplicate,
            viewModel.uiState.value.userMessage,
        )
    }

    @Test
    fun createEmployee_whenInvalidPassword_returnMessageFailure() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        sessionRepository.setSessionInfo(ownerSession)

        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged("employee11"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePhoneChanged("1212321312"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePasswordChanged("1234"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeRoleChanged(EmployeeRole.CASHIER))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeBranchAssigned("branch1"))

        // When
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        // Then
        assertEquals(
            uiString.core_ui_error_add_employee_password_less_than_six,
            viewModel.employeeFormUiState.value.formErrors.passwordError,
        )
    }

    @Test
    fun createEmployee_whenInvalidData_returnMessageFailure() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        sessionRepository.setSessionInfo(ownerSession)

        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged(""))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePhoneChanged(""))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePasswordChanged("12345"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeRoleChanged(EmployeeRole.CASHIER))

        // When
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        // Then
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
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        sessionRepository.setSessionInfo(ownerSession)
        employeeRepository.setFailure(IOException("network error"))

        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged("employee11"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePhoneChanged("1212321312"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeePasswordChanged("123456666"))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeRoleChanged(EmployeeRole.CASHIER))
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeBranchAssigned("branch1"))

        // Ensure dialog is open
        viewModel.onEvent(EmployeeEvent.CreationEmployeeOpened)

        // When
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        // Then
        assertEquals(
            uiString.core_ui_error_add_employee_message,
            viewModel.uiState.value.userMessage,
        )
    }

    @Test
    fun updateEmployee_whenValidData_updatesEmployeeAndShowsSuccess() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        sessionRepository.setSessionInfo(ownerSession)

        val existingEmployee = Employee(
            id = "emp1",
            name = "Existing Employee",
            phone = "1112223333",
            role = EmployeeRole.CASHIER,
            assignedBranchId = "branch1",
        )
        viewModel.onEvent(EmployeeEvent.UpdatingEmployeeOpened(existingEmployee))

        // Update name
        viewModel.onEventEmployeeForm(EmployeeFormEvent.EmployeeNameChanged("Updated Name"))

        // When
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        // Then
        assertEquals(
            uiString.core_ui_success_update_employee_message,
            viewModel.uiState.value.userMessage,
        )
        assertEquals(DialogState.None, viewModel.uiState.value.dialogState)
    }

    @Test
    fun updateEmployee_whenRepositoryFails_showsError() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        sessionRepository.setSessionInfo(ownerSession)

        val existingEmployee = Employee(
            id = "emp1",
            name = "Existing Employee",
            phone = "1112223333",
            role = EmployeeRole.CASHIER,
            assignedBranchId = "branch1",
        )
        viewModel.onEvent(EmployeeEvent.UpdatingEmployeeOpened(existingEmployee))
        employeeRepository.setFailure(IOException("Update failed"))

        // When
        viewModel.onEventEmployeeForm(EmployeeFormEvent.SaveClicked)

        // Then
        assertEquals(
            uiString.core_ui_error_update_employee_message,
            viewModel.uiState.value.userMessage,
        )
    }

    @Test
    fun deleteEmployee_whenEmployeeSelected_deletesEmployeeAndShowsSuccess() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        sessionRepository.setSessionInfo(ownerSession)

        val employeeToDelete = Employee(
            id = "emp_to_delete",
            name = "To Delete",
            phone = "0000000000",
            role = EmployeeRole.CASHIER,
            assignedBranchId = "branch1",
        )
        // Select employee for deletion
        viewModel.onEvent(EmployeeEvent.DeletingEmployeeOpened(employeeToDelete))

        // When
        viewModel.deleteEmployee()

        // Then
        assertEquals(
            uiString.core_ui_success_delete_employee_message,
            viewModel.uiState.value.userMessage,
        )
    }

    @Test
    fun deleteEmployee_whenNoEmployeeSelected_showsError() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // When (no employee selected)
        viewModel.deleteEmployee()

        // Then
        assertEquals(
            uiString.core_ui_error_unknown,
            viewModel.uiState.value.userMessage,
        )
    }

    @Test
    fun deleteEmployee_whenRepositoryFails_showsError() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        sessionRepository.setSessionInfo(ownerSession)

        val employeeToDelete = Employee(
            id = "emp_to_delete",
            name = "To Delete",
            phone = "0000000000",
            role = EmployeeRole.CASHIER,
            assignedBranchId = "branch1",
        )
        viewModel.onEvent(EmployeeEvent.DeletingEmployeeOpened(employeeToDelete))
        employeeRepository.setFailure(IOException("Delete failed"))

        // When
        viewModel.deleteEmployee()

        // Then
        assertEquals(
            uiString.core_ui_error_delete_employee_message,
            viewModel.uiState.value.userMessage,
        )
    }

    @Test
    fun loadInitialData_whenEmployeeLoggedIn_loadsEmployeesAndBranches() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val employeeSession = SessionStateResult.EmployeeLoggedIn(
            "business1",
            "branch1",
            "emp1",
            "token",
            EmployeeRole.CASHIER,
        )

        val employees = listOf(
            Employee(
                id = "emp1",
                name = "Employee 1",
                role = EmployeeRole.CASHIER,
                phone = "123",
                assignedBranchId = "branch1",
            ),
        )
        val branches = listOf(
            Branch(
                id = "branch1",
                name = "Branch 1",
                phone = "456",
                businessId = "business1",
            ),
        )

        // When
        branchRepository.sendBranches(branches)
        employeeRepository.sendEmployees(employees)
        sessionRepository.setSessionInfo(employeeSession)
        advanceUntilIdle()

        // Then
        assertEquals(employees, viewModel.uiState.value.employees)
        assertEquals(branches, viewModel.uiState.value.availableBranches)
    }

    @Test
    fun onEvent_CreationEmployeeOpened_setsDialogStateCreating() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        // When
        viewModel.onEvent(EmployeeEvent.CreationEmployeeOpened)

        // Then
        assertEquals(DialogState.Creating, viewModel.uiState.value.dialogState)
    }

    @Test
    fun onEvent_UpdatingEmployeeOpened_setsDialogStateUpdatingAndPrefillsForm() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.employeeFormUiState.collect() }

        val employee = Employee(
            id = "emp1",
            name = "Test Employee",
            phone = "123456",
            role = EmployeeRole.MANAGER,
            assignedBranchId = "branch1",
        )

        // When
        viewModel.onEvent(EmployeeEvent.UpdatingEmployeeOpened(employee))

        // Then
        assertEquals(DialogState.Updating, viewModel.uiState.value.dialogState)
        assertEquals(employee, viewModel.uiState.value.employeeSelected)

        val formState = viewModel.employeeFormUiState.value
        assertEquals(employee.name, formState.name)
        assertEquals(employee.phone, formState.phone)
        assertEquals(employee.role, formState.role)
        assertEquals(employee.assignedBranchId, formState.assignedBranchId)
        // Password should be empty for updates usually, unless we want to reset it
        assertEquals("", formState.password)
    }

    @Test
    fun onEvent_DeletingEmployeeOpened_setsDialogStateDeleting() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        val employee = Employee(
            id = "emp1",
            name = "Test Employee",
            phone = "123",
            role = EmployeeRole.CASHIER,
            assignedBranchId = "b1",
        )

        // When
        viewModel.onEvent(EmployeeEvent.DeletingEmployeeOpened(employee))

        // Then
        assertEquals(DialogState.Deleting, viewModel.uiState.value.dialogState)
        assertEquals(employee, viewModel.uiState.value.employeeSelected)
    }

    @Test
    fun onEvent_DialogsClosed_resetsDialogState() = runTest {
        // Given
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
        viewModel.onEvent(EmployeeEvent.CreationEmployeeOpened)
        assertEquals(DialogState.Creating, viewModel.uiState.value.dialogState)

        // When
        viewModel.onEvent(EmployeeEvent.EmployeeFormClosed)

        // Then
        assertEquals(DialogState.None, viewModel.uiState.value.dialogState)

        // Given Deleting
        val employee = Employee(
            "id",
            "name",
            "phone",
            EmployeeRole.CASHIER,
            "bid",
        )
        viewModel.onEvent(EmployeeEvent.DeletingEmployeeOpened(employee))
        assertEquals(DialogState.Deleting, viewModel.uiState.value.dialogState)

        // When
        viewModel.onEvent(EmployeeEvent.DeletingEmployeeClosed)

        // Then
        assertEquals(DialogState.None, viewModel.uiState.value.dialogState)
    }
}
