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

import com.casecode.pos.core.domain.usecase.AddEmployeesUseCase
import com.casecode.pos.core.domain.usecase.DeleteEmployeeUseCase
import com.casecode.pos.core.domain.usecase.GetBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetEmployeesBusinessUseCase
import com.casecode.pos.core.domain.usecase.UpdateEmployeesUseCase
import com.casecode.pos.core.testing.repository.TestBusinessRepository
import com.casecode.pos.core.testing.repository.TestEmployeesBusinessRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import com.casecode.pos.core.testing.util.TestNetworkMonitor
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import com.casecode.pos.core.data.R.string as dataString
import com.casecode.pos.core.ui.R.string as uiString

class EmployeeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private lateinit var viewModel: EmployeeViewModel
    private val networkMonitor = TestNetworkMonitor()
    private val employeesBusinessRepository = TestEmployeesBusinessRepository()
    private val businessRepository = TestBusinessRepository()
    private val getEmployees = GetEmployeesBusinessUseCase(employeesBusinessRepository)
    private val getBusiness = GetBusinessUseCase(businessRepository)
    private val addEmployee = AddEmployeesUseCase(employeesBusinessRepository)
    private val updateEmployee = UpdateEmployeesUseCase(employeesBusinessRepository)
    private val deleteEmployeeUseCase = DeleteEmployeeUseCase(employeesBusinessRepository)

    @Before
    fun init() {
        viewModel = EmployeeViewModel(
            networkMonitor,
            getEmployees,
            getBusiness,
            addEmployee,
            updateEmployee,
            deleteEmployeeUseCase,
        )
    }

    @Test
    fun addEmployee_hasEmployee_returnAddEmployeeMessage() = runTest {
        // Given
        viewModel.addEmployee("mahmoud22", "131434", "1234", "branch1", "sale")
        assertEquals(
            viewModel.uiState.value.userMessage,
            uiString.core_ui_success_add_employee_message,
        )
    }

    @Test
    fun addEmployee_nameEmployeeDuplicateEmployee_returnMessageDuplicateName() = runTest {
        // Given
        viewModel.addEmployee("Mahmoud", "131434", "1234", "branch1", "sale")
        assertEquals(
            viewModel.uiState.value.userMessage,
            uiString.core_ui_error_employee_name_duplicate,
        )
    }

    @Test
    fun addEmployee_error_returnMessageError() = runTest {
        employeesBusinessRepository setReturnError true
        viewModel.addEmployee("Mahmoud1223", "131434", "1234", "branch1", "sale")
        assertEquals(
            viewModel.uiState.value.userMessage,
            com.casecode.pos.core.data.R.string.core_data_add_employees_business_failure,
        )
    }

    @Test
    fun updateEmployee_hasChangeOldEmployee_returnMessageUpdateSuccess() = runTest {
        // Given
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        // When
        viewModel.updateEmployee("Mahmoud1223", "131434", "1234", "branch1", "sale")
        assertEquals(
            viewModel.uiState.value.userMessage,
            uiString.core_ui_success_update_employee_message,
        )
    }

    @Test
    fun updateEmployee_nameEmployeeDuplicateEmployee_returnMessageDuplicateName() = runTest {
        // Given
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        // When
        val nameDuplicate = employeesBusinessRepository.fakeEmployees[1].name
        viewModel.updateEmployee(nameDuplicate, "131434", "1234", "branch1", "sale")
        assertEquals(
            viewModel.uiState.value.userMessage,
            uiString.core_ui_error_employee_name_duplicate,
        )
    }

    @Test
    fun updateEmployee_hasSameOldEmployee_returnMessageUpdateEmployeeFailed() = runTest {
        // Given
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        val name = employeesBusinessRepository.fakeEmployees[0].name
        val phoneNumber = employeesBusinessRepository.fakeEmployees[0].phoneNumber
        val password = employeesBusinessRepository.fakeEmployees[0].password!!
        val branchName = employeesBusinessRepository.fakeEmployees[0].branchName!!
        val permission = employeesBusinessRepository.fakeEmployees[0].permission
        // When

        viewModel.updateEmployee(name, phoneNumber, password, branchName, permission)

        assertEquals(
            viewModel.uiState.value.userMessage,
            uiString.core_ui_error_update_employee_message,
        )
    }

    @Test
    fun updateEmployee_error_returnMessageError() = runTest {
        employeesBusinessRepository setReturnError true
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        viewModel.updateEmployee("Mahmoud1223", "131434", "1234", "branch1", "sale")

        assertEquals(
            viewModel.uiState.value.userMessage,
            com.casecode.pos.core.data.R.string.core_data_employee_update_business_failure,
        )
    }

    @Test
    fun updateEmployee_network_unavailable_returnMessageError() = runTest {
        // Given
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        // When
        networkMonitor.setConnected(false)
        viewModel.updateEmployee("Mahmoud1223", "131434", "1234", "branch1", "sale")
        // Then

        assertEquals(
            viewModel.uiState.value.userMessage,
            uiString.core_ui_error_network,
        )
    }

    @Test
    fun deleteEmployee_returnMessageSuccess() = runTest {
        // Given
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        // When
        viewModel.deleteEmployee()

        assertEquals(
            viewModel.uiState.value.userMessage,
            dataString.core_data_employee_delete_business_success,
        )
    }

    @Test
    fun deleteEmployee_whenError_returnMessageError() = runTest {
        // Given
        employeesBusinessRepository setReturnError true
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        // When
        viewModel.deleteEmployee()
        assertEquals(
            viewModel.uiState.value.userMessage,
            dataString.core_data_employee_delete_business_failure,
        )
    }

    @Test
    fun deleteEmployee_whenNetworkUnavailable_returnMessageError() = runTest {
        // Given
        viewModel.setEmployeeSelected(employeesBusinessRepository.fakeEmployees[0])
        // When
        networkMonitor.setConnected(false)
        viewModel.deleteEmployee()
        assertEquals(
            viewModel.uiState.value.userMessage,
            uiString.core_ui_error_network,
        )
    }
}