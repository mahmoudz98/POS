package com.casecode.pos.feature.employee

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.model.isEmployeeNameDuplicate
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.repository.ResourceEmployees
import com.casecode.pos.core.domain.usecase.AddEmployeesUseCase
import com.casecode.pos.core.domain.usecase.GetBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetEmployeesBusinessUseCase
import com.casecode.pos.core.domain.usecase.UpdateEmployeesUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Employee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.casecode.pos.core.ui.R.string as uiString

data class UiEmployeesState(
    val resourceEmployees: ResourceEmployees = Resource.loading(),
    @StringRes val userMessage: Int? = null,
)

@HiltViewModel
class EmployeeViewModel
    @Inject
    constructor(
        private val networkMonitor: NetworkMonitor,
        private val getEmployeesBusinessUseCase: GetEmployeesBusinessUseCase,
        private val getBusinessUseCase: GetBusinessUseCase,
        private val addEmployeesUseCase: AddEmployeesUseCase,
        private val updateEmployeesUseCase: UpdateEmployeesUseCase,
    ) : ViewModel() {
        private val isOnline: MutableLiveData<Boolean> = MutableLiveData(false)

        private val _uiState = MutableStateFlow(UiEmployeesState())
        val uiState get() = _uiState.asStateFlow()

        private val _employeeSelected: MutableStateFlow<Employee?> = MutableStateFlow(null)
        val employeeSelected = _employeeSelected.asStateFlow()

    private val _branches: MutableStateFlow<List<Branch>> = MutableStateFlow(emptyList())
    val branches get() = _branches.asStateFlow()

    init {
        fetchEmployees()
        fetchBusiness()
        setNetworkMonitor()
    }

    private fun setNetworkMonitor() =
        viewModelScope.launch {
            networkMonitor.isOnline.collect {
                setConnected(it)
            }
        }

    private fun setConnected(isConnect: Boolean) {
        isOnline.value = isConnect
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

                    Resource.Loading -> _uiState.update { it.copy(resourceEmployees = resourceEmployees) }
                    is Resource.Success -> {
                        _uiState.update { it.copy(resourceEmployees = resourceEmployees) }
                    }
                }
            }
        }
    }

    private fun fetchBusiness() {
        viewModelScope.launch {
            getBusinessUseCase().collect {
                when (it) {
                    is Resource.Empty -> {
                    }

                    is Resource.Error -> {
                        val message = it.message as? Int ?: uiString.core_ui_error_unknown
                        showSnackbarMessage(message)
                    }

                    Resource.Loading -> {}
                    is Resource.Success -> {
                        _branches.value = it.data.branches
                    }
                }
            }
        }
    }

    fun setEmployeeSelected(employeeSelect: Employee) {
        _employeeSelected.value = employeeSelect
    }

    fun addEmployee(
        name: String,
        phone: String,
        password: String,
        branchName: String,
        permission: String,
    ) {
        val employee = Employee(name, phone, password, branchName, permission)
        if (isOnline.value == false) return showSnackbarMessage(uiString.core_ui_error_network)
        val employees = (uiState.value.resourceEmployees as? Resource.Success)?.data
        if (employees?.isEmployeeNameDuplicate(employee) == true) {
            return showSnackbarMessage(uiString.core_ui_error_employee_name_duplicate)
        }
        viewModelScope.launch {
            val addEmployeeResource = addEmployeesUseCase(employee)
            if (addEmployeeResource is Resource.Error) {
                val message = addEmployeeResource.message as? Int
                    ?: uiString.core_ui_error_add_employee_message
                showSnackbarMessage(message)
            } else if (addEmployeeResource is Resource.Success) {
                showSnackbarMessage(uiString.core_ui_success_add_employee_message)
            }
        }
    }

    fun updateEmployee(
        name: String,
        phone: String,
        password: String,
        branchName: String,
        permission: String,
    ) {
        if (isOnline.value == false) return showSnackbarMessage(uiString.core_ui_error_network)
        val newEmployee = Employee(name, phone, password, branchName, permission)
        val oldEmployee =
            _employeeSelected.value
                ?: return showSnackbarMessage(uiString.core_ui_error_update_employee_message)

        val employees = (uiState.value.resourceEmployees as? Resource.Success)?.data

        if (employees?.isEmployeeNameDuplicate(newEmployee, oldEmployee) == true) {
            return showSnackbarMessage(uiString.core_ui_error_employee_name_duplicate)
        }
        if (oldEmployee == newEmployee) return showSnackbarMessage(uiString.core_ui_error_update_employee_message)
        viewModelScope.launch {
            val updateEmployeeResource = updateEmployeesUseCase(newEmployee, oldEmployee)
            if (updateEmployeeResource is Resource.Error) {
                val message =
                    updateEmployeeResource.message as? Int
                        ?: uiString.core_ui_error_update_employee_message
                showSnackbarMessage(message)
                Timber.e("error: ${updateEmployeeResource.message}")
            } else if (updateEmployeeResource is Resource.Success) {
                showSnackbarMessage(uiString.core_ui_success_update_employee_message)
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