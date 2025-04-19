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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.domain.usecase.AddEmployeeUseCase
import com.casecode.pos.core.domain.usecase.DeleteEmployeeUseCase
import com.casecode.pos.core.domain.usecase.GetBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetEmployeesBusinessUseCase
import com.casecode.pos.core.domain.usecase.UpdateEmployeesUseCase
import com.casecode.pos.core.domain.utils.AddEmployeeResult
import com.casecode.pos.core.domain.utils.BusinessResult
import com.casecode.pos.core.domain.utils.NetworkMonitor
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Employee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.casecode.pos.core.ui.R.string as uiString

@HiltViewModel
class EmployeeViewModel
@Inject
constructor(
    private val networkMonitor: NetworkMonitor,
    private val getEmployeesBusinessUseCase: GetEmployeesBusinessUseCase,
    private val getBusinessUseCase: GetBusinessUseCase,
    private val addEmployeesUseCase: AddEmployeeUseCase,
    private val updateEmployeesUseCase: UpdateEmployeesUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val isOnline: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _uiState = MutableStateFlow(UiEmployeesState())
    val uiState get() = _uiState.asStateFlow()
    private val _employeeSelected: MutableStateFlow<Employee?> = MutableStateFlow(null)
    val employeeSelected = _employeeSelected.asStateFlow()
    private val _branches: MutableStateFlow<List<Branch>> = MutableStateFlow(emptyList())
    val branches get() = _branches.asStateFlow()
    val currentUid = MutableStateFlow<String>("")

    init {
        fetchEmployees()
        fetchBusiness()
        setNetworkMonitor()
    }

    fun getCurrentUid() {
        viewModelScope.launch {
            currentUid.update { authRepository.currentUserId() }
        }
    }

    private fun setNetworkMonitor() = viewModelScope.launch {
        networkMonitor.isOnline.collect {
            setConnected(it)
        }
    }

    private fun setConnected(isConnect: Boolean) {
        isOnline.update { isConnect }
    }

    private fun fetchEmployees() {
        viewModelScope.launch {
            getEmployeesBusinessUseCase().collect { resourceEmployees ->
                when (resourceEmployees) {
                    is Resource.Empty -> {
                        _uiState.update { it.copy(resourceEmployees = Resource.empty()) }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                resourceEmployees = resourceEmployees,
                                userMessage = resourceEmployees.message as Int,
                            )
                        }
                    }
                    Resource.Loading -> _uiState.update {
                        it.copy(
                            resourceEmployees = resourceEmployees,
                        )
                    }

                    is Resource.Success -> {
                        _uiState.update { it.copy(resourceEmployees = resourceEmployees) }
                    }
                }
            }
        }
    }

    private fun fetchBusiness() {
        viewModelScope.launch {
            when (val result = getBusinessUseCase()) {
                is BusinessResult.Error -> {
                    val message = result.message ?: uiString.core_ui_error_unknown
                    showSnackbarMessage(message)
                }

                is BusinessResult.Success -> {
                    _branches.value = result.data.branches
                }
            }
        }
    }

    fun setEmployeeSelected(employeeSelect: Employee) {
        _employeeSelected.value = employeeSelect
    }

    fun addEmployee(employee: Employee) {
        if (isOnline.value == false) {
            return showSnackbarMessage(uiString.core_ui_error_network)
        }
        val employees = (uiState.value.resourceEmployees as? Resource.Success)?.data
        if (employee.isEmployeeNameDuplicate(employees) == true) {
            return showSnackbarMessage(uiString.core_ui_error_employee_name_duplicate)
        }
        viewModelScope.launch {
            val addEmployeeResult = addEmployeesUseCase(employee)
            when (addEmployeeResult) {
                is AddEmployeeResult.Error -> {
                    showSnackbarMessage(addEmployeeResult.message)
                }

                is AddEmployeeResult.Success -> {
                    showSnackbarMessage(uiString.core_ui_success_add_employee_message)
                }
            }
        }
    }

    fun updateEmployee(newEmployee: Employee) {
        if (isOnline.value == false) return showSnackbarMessage(uiString.core_ui_error_network)
        val oldEmployee =
            _employeeSelected.value
                ?: return showSnackbarMessage(uiString.core_ui_error_update_employee_message)

        if (oldEmployee == newEmployee) {
            return showSnackbarMessage(uiString.core_ui_error_update_employee_message)
        }
        val employees = (uiState.value.resourceEmployees as? Resource.Success)?.data
        if (newEmployee.isEmployeeNameDuplicate(employees, oldEmployee) == true) {
            return showSnackbarMessage(uiString.core_ui_error_employee_name_duplicate)
        }
        viewModelScope.launch {
            val updateEmployeeResource = updateEmployeesUseCase(oldEmployee, newEmployee)
            if (updateEmployeeResource is Resource.Error) {
                val message =
                    updateEmployeeResource.message as? Int
                        ?: uiString.core_ui_error_update_employee_message
                showSnackbarMessage(message)
            } else if (updateEmployeeResource is Resource.Success) {
                showSnackbarMessage(uiString.core_ui_success_update_employee_message)
            }
        }
    }

    fun deleteEmployee() {
        if (isOnline.value == false) {
            return showSnackbarMessage(uiString.core_ui_error_network)
        }
        val employee =
            _employeeSelected.value ?: return showSnackbarMessage(uiString.core_ui_error_unknown)
        viewModelScope.launch {
            val deleteEmployeeResource = deleteEmployeeUseCase(employee)
            if (deleteEmployeeResource is Resource.Error) {
                val message =
                    deleteEmployeeResource.message as? Int ?: uiString.core_ui_error_unknown
                showSnackbarMessage(message)
            } else if (deleteEmployeeResource is Resource.Success) {
                showSnackbarMessage(deleteEmployeeResource.data)
                _employeeSelected.value = null
            }
        }
    }

    fun showSnackbarMessage(message: Int) {
        _uiState.update { it.copy(userMessage = message) }
    }

    fun snackbarMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }
}