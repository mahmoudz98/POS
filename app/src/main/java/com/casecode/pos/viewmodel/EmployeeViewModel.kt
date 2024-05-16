package com.casecode.pos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.casecode.data.mapper.isEmployeeNameDuplicate
import com.casecode.data.utils.NetworkMonitor
import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.Employee
import com.casecode.domain.usecase.AddEmployeesUseCase
import com.casecode.domain.usecase.GetBusinessUseCase
import com.casecode.domain.usecase.GetEmployeesBusinessUseCase
import com.casecode.domain.usecase.UpdateEmployeesUseCase
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.base.BaseViewModel
import com.casecode.pos.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val getEmployeesBusinessUseCase: GetEmployeesBusinessUseCase,
    private val getBusinessUseCase: GetBusinessUseCase,
    private val addEmployeesUseCase: AddEmployeesUseCase,
    private val updateEmployeesUseCase: UpdateEmployeesUseCase,
) : BaseViewModel() {
    private val isOnline: MutableLiveData<Boolean> = MutableLiveData(false)

    private var _employees: MutableLiveData<List<Employee>> = MutableLiveData()
    val employees get() = _employees

    private var _isCompact: MutableLiveData<Boolean> = MutableLiveData(true)
    val isCompact get() = _isCompact
    private val _employeeSelected: MutableLiveData<Employee> = MutableLiveData()
    val employeeSelected get() = _employeeSelected
    private val _branches: MutableLiveData<List<Branch>> = MutableLiveData()
    val branches get() = _branches
    private val _isEmptyEmployees: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEmptyEmployees: LiveData<Boolean> get() = _isEmptyEmployees
    private val _isUpdateEmployee: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isUpdateEmployee get() = _isUpdateEmployee

    init {
        fetchEmployees()
        fetchBusiness()
        setNetworkMonitor()
    }
    private fun setNetworkMonitor() = viewModelScope.launch {
        networkMonitor.isOnline.collect {
            setConnected(it)
        }
    }

    private fun setConnected(isConnect: Boolean) {
        isOnline.value = isConnect
    }
    fun setCompact(isCompact: Boolean) {
        _isCompact.value = (isCompact)
    }

   private fun fetchEmployees() {
        viewModelScope.launch {
            getEmployeesBusinessUseCase().collect {
                when (it) {
                    is Resource.Empty -> {
                        hideProgress()
                        _isEmptyEmployees.value = true
                    }

                    is Resource.Error -> {
                        _isEmptyEmployees.value = _employees.value.isNullOrEmpty()

                        hideProgress()
                        showSnackbarMessage(it.message as Int)
                    }

                    Resource.Loading -> showProgress()
                    is Resource.Success -> {
                        _isEmptyEmployees.value = false
                        hideProgress()
                        _employees.value = it.data
                    }
                }
            }
        }
    }

  private  fun fetchBusiness() {
        viewModelScope.launch {
            getBusinessUseCase().collect {
                when (it) {
                    is Resource.Empty -> {
                    }

                    is Resource.Error -> {
                        val message = it.message as? Int ?: R.string.all_error_unknown
                        showSnackbarMessage(message)
                    }

                    Resource.Loading -> showProgress()
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
        if (employees.value?.isEmployeeNameDuplicate(employee) == true) {
            return showSnackbarMessage(R.string.employee_name_duplicate)
        }
        viewModelScope.launch {
            val addEmployeeResource = addEmployeesUseCase(employee)
            if (addEmployeeResource is Resource.Error) {
                val message = addEmployeeResource.message as? Int ?: R.string.add_employee_fail
                showSnackbarMessage(message)
            }else if(addEmployeeResource is Resource.Success) {
                showSnackbarMessage(R.string.add_employee_success)
            }

        }
    }

    fun updateEmployee(
        name: String, phone: String, password: String, branchName: String,
        permission: String,
    ) {
        if(isOnline.value == false) return showSnackbarMessage(R.string.network_error)
        val newEmployee = Employee(name, phone, password, branchName, permission)
        val oldEmployee =
            _employeeSelected.value ?: return showSnackbarMessage(R.string.update_employee_fail)
        if (employees.value?.isEmployeeNameDuplicate(newEmployee, oldEmployee) == true) {
            return showSnackbarMessage(R.string.employee_name_duplicate)
        }
        if (oldEmployee == newEmployee) return showSnackbarMessage(R.string.update_employee_fail)
        viewModelScope.launch {
            val updateEmployeeResource = updateEmployeesUseCase(newEmployee, oldEmployee)
            if(updateEmployeeResource is Resource.Error){
                val message =
                    updateEmployeeResource.message as? Int ?: R.string.update_employee_fail
                showSnackbarMessage(message)
                Timber.e("error: ${updateEmployeeResource.message}")
                _isUpdateEmployee.value = Event(false)
            }else if(updateEmployeeResource is Resource.Success){
                showSnackbarMessage(R.string.update_employee_success)
                _isUpdateEmployee.value = Event(true)
            }
        }
    }


}