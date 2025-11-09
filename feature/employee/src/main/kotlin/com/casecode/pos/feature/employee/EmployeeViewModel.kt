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
import com.casecode.pos.core.domain.exceptions.EmployeeNameCollisionException
import com.casecode.pos.core.domain.exceptions.NoActiveSessionException
import com.casecode.pos.core.domain.usecase.CreateEmployeeUseCase
import com.casecode.pos.core.domain.usecase.DeleteEmployeeUseCase
import com.casecode.pos.core.domain.usecase.GetBranchesUseCase
import com.casecode.pos.core.domain.usecase.GetEmployeesUseCase
import com.casecode.pos.core.domain.usecase.UpdateEmployeeUseCase
import com.casecode.pos.core.model.business.Employee
import com.casecode.pos.core.ui.stateInWhileSubscribed
import com.casecode.pos.core.ui.updateWithViewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.casecode.pos.core.ui.R.string as uiString

@HiltViewModel
internal class EmployeeViewModel @Inject constructor(
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val getBranchesUseCase: GetBranchesUseCase,
    private val createEmployeeUseCase: CreateEmployeeUseCase,
    private val updateEmployeeUseCase: UpdateEmployeeUseCase,
    private val deleteEmployeeUseCase: DeleteEmployeeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeUiState())
    val uiState = _uiState.onStart {
        loadInitialData()
    }.stateInWhileSubscribed(EmployeeUiState())
    private val _employeeFormUiState = MutableStateFlow(EmployeeFormUiState())
    val employeeFormUiState = _employeeFormUiState.asStateFlow()
    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val employeesFlow = getEmployeesUseCase()
            val branchesFlow = getBranchesUseCase()

            combine(employeesFlow, branchesFlow) { employeesResult, branchesResult ->
                _uiState.update {
                    it.copy(
                        employees = employeesResult,
                        isLoading = false,

                        availableBranches = branchesResult,
                    )
                }
            }.collect()
        }
    }

    fun onEvent(event: EmployeeEvent) {
        when (event) {
            is EmployeeEvent.UserMessageShown -> _uiState.update {
                it.copy(
                    userMessage = null,
                )
            }

            EmployeeEvent.EmployeeFormClosed, EmployeeEvent.DeletingEmployeeClosed -> {
                _uiState.update { it.copy(dialogState = DialogState.None) }
                _employeeFormUiState.update { EmployeeFormUiState() }
            }

            EmployeeEvent.CreationEmployeeOpened ->
                _uiState.update { it.copy(dialogState = DialogState.Creating) }

            is EmployeeEvent.DeletingEmployeeOpened ->
                _uiState.update {
                    it.copy(
                        dialogState = DialogState.Deleting,
                        employeeSelected = event.employee,
                    )
                }

            is EmployeeEvent.UpdatingEmployeeOpened -> {
                _uiState.update { state ->
                    state.copy(
                        employeeSelected = event.employee,
                        dialogState = DialogState.Updating,
                    )
                }
                _employeeFormUiState.update {
                    it.copy(
                        name = event.employee.name,
                        phone = event.employee.phone,
                        password = "",
                        role = event.employee.role,
                        assignedBranchId = event.employee.assignedBranchId,
                    )
                }
            }
        }
    }

    fun onEventEmployeeForm(event: EmployeeFormEvent) {
        when (event) {
            is EmployeeFormEvent.EmployeeNameChanged -> updateEmployeeFormState {
                it.copy(
                    name = event.name,
                    formErrors = it.formErrors.copy(nameError = null),
                )
            }

            is EmployeeFormEvent.EmployeePasswordChanged -> updateEmployeeFormState {
                it.copy(
                    password = event.password,
                    formErrors = it.formErrors.copy(passwordError = null),
                )
            }

            is EmployeeFormEvent.EmployeePhoneChanged -> updateEmployeeFormState {
                it.copy(
                    phone = event.phone,
                    formErrors = it.formErrors.copy(phoneError = null),
                )
            }

            is EmployeeFormEvent.EmployeeBranchAssigned -> updateAssignedBranches(
                event.branchId,

                )

            is EmployeeFormEvent.EmployeeRoleChanged -> updateEmployeeFormState {
                it.copy(
                    role = event.role,
                )
            }

            EmployeeFormEvent.SaveClicked -> saveEmployee()
        }
    }

    private fun updateAssignedBranches(branchId: String) {
        updateEmployeeFormState { currentState ->
            currentState.copy(assignedBranchId = branchId)
        }
    }

    private fun saveEmployee() {
        val isUpdate = _uiState.value.dialogState == DialogState.Updating
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            _employeeFormUiState.update { it.copy(formErrors = it.validate(isUpdate)) }
            val formState = _employeeFormUiState.value
            if (!formState.isValid()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }
            val employee = Employee(
                id = if (isUpdate) _uiState.value.employeeSelected!!.id else "",
                name = formState.name,
                phone = formState.phone,
                role = formState.role,
                assignedBranchId = formState.assignedBranchId,

                )
            val result = if (isUpdate) {
                updateEmployeeUseCase(
                    employee,
                    password = formState.password,
                )
            } else {
                createEmployeeUseCase(
                    employee,
                    password = formState.password,
                )
            }
            onEvent(EmployeeEvent.EmployeeFormClosed)
            handleResult(result, isUpdate)
            _uiState.update { it.copy(isLoading = false) }
            updateEmployeeFormState { EmployeeFormUiState() }
        }
    }

    private fun handleResult(result: Result<Unit>, isUpdate: Boolean) {
        result.onSuccess {
            _uiState.update {
                it.copy(
                    userMessage = if (isUpdate) {
                        uiString.core_ui_success_update_employee_message
                    } else {
                        uiString.core_ui_success_add_employee_message
                    },
                )
            }
        }.onFailure {
            when (it) {
                is NoActiveSessionException -> {
                    _uiState.update { state ->
                        state.copy(
                            userMessage = uiString.core_ui_error_no_active_session,
                        )
                    }
                }
                is EmployeeNameCollisionException ->{
                    _uiState.update { state ->
                        state.copy(
                            userMessage = uiString.core_ui_error_employee_name_duplicate,
                        )
                    }
                }
                else -> {
                    _uiState.update { state ->
                        state.copy(
                            userMessage = if (isUpdate) {
                                uiString.core_ui_error_update_employee_message
                            } else {
                                uiString.core_ui_error_add_employee_message
                            },

                            )
                    }
                }
            }
        }
    }
        fun deleteEmployee() {
            viewModelScope.launch {
                val employee = _uiState.value.employeeSelected
                if (employee == null) {
                    _uiState.update { it.copy(userMessage = uiString.core_ui_error_unknown) }
                    return@launch
                }
                deleteEmployeeUseCase(employee.id).onSuccess {
                    _uiState.update { it.copy(userMessage = uiString.core_ui_success_delete_employee_message) }

                }.onFailure {
                    _uiState.update { it.copy(userMessage = uiString.core_ui_error_delete_employee_message) }

                }
                onEvent(EmployeeEvent.EmployeeFormClosed)
                _uiState.update { it.copy(employeeSelected = null) }
            }
        }

        private fun updateEmployeeFormState(updateAction: (EmployeeFormUiState) -> EmployeeFormUiState) {
            _employeeFormUiState.updateWithViewModelScope { currentState ->
                updateAction(currentState)
            }
        }
    }
