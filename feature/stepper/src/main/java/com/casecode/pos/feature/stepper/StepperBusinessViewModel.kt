package com.casecode.pos.feature.stepper

import androidx.annotation.OpenForTesting
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.core.data.model.asSubscriptionBusiness
import com.casecode.pos.core.data.model.toStoreType
import com.casecode.pos.core.data.service.AccountService
import com.casecode.pos.core.data.utils.NetworkMonitor
import com.casecode.pos.core.domain.usecase.CompleteBusinessUseCase
import com.casecode.pos.core.domain.usecase.GetSubscriptionsUseCase
import com.casecode.pos.core.domain.usecase.SetBusinessUseCase
import com.casecode.pos.core.domain.usecase.SetEmployeesBusinessUseCase
import com.casecode.pos.core.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.subscriptions.Subscription
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.model.data.users.Business
import com.casecode.pos.core.model.data.users.Employee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.casecode.pos.core.ui.R.string as uiString

/**
 * ViewModel for business stepper screen.
 *
 * This ViewModel handles the business setup process, including setting up business information,
 * branches, subscriptions, and employees. It also handles UI state and user messages.
 */
@HiltViewModel
class StepperBusinessViewModel
@Inject
constructor(
    private val networkMonitor: NetworkMonitor,
    private val accountService: AccountService,
    private val setBusinessUseCase: SetBusinessUseCase,
    private val completeBusinessUseCase: CompleteBusinessUseCase,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
    private val setSubscriptionsBusinessUseCase: SetSubscriptionBusinessUseCase,
    private val setEmployeesBusinessUseCase: SetEmployeesBusinessUseCase,
) : ViewModel() {

    private val _buttonStepState = MutableStateFlow(ButtonsStepState())
    val buttonStepState = _buttonStepState.asStateFlow()

    // Business data
    private val _uiState: MutableStateFlow<StepperBusinessUiState> =
        MutableStateFlow(StepperBusinessUiState())
    val uiState = _uiState.asStateFlow()

    private val _userMessage: MutableStateFlow<Int?> = MutableStateFlow(null)
    val userMessage get() = _userMessage.asStateFlow()

    init {
        networkMonitor()
    }

    fun networkMonitor() =
        viewModelScope.launch {
            networkMonitor.isOnline.collect {
                setConnected(it)
            }
        }

    fun signOut() = viewModelScope.launch { accountService.signOut() }

    fun setConnected(isOnline: Boolean) {
        _uiState.update { it.copy(isOnline = isOnline) }
    }

    fun setBusinessInfo(storeType: String, emailBusiness: String, phoneBusiness: String) {
        _uiState.update {
            it.copy(
                storeType = storeType,
                emailBusiness = emailBusiness,
                phoneBusiness = phoneBusiness,
            )
        }
    }

    fun addBranch(branchName: String, branchPhone: String) {
        viewModelScope.launch {
            val branchCode = incrementBranchCode()
            val branch = Branch(branchCode, branchName, branchPhone)
            val branches = uiState.value.branches.toMutableList()
            if (branches.add(branch)) {
                _uiState.update { it.copy(branches = branches) }
                showSnackbarMessage(uiString.core_ui_success_add_branch_message)
            } else {
                showSnackbarMessage(uiString.core_ui_error_add_branch_message)
            }
        }
    }

    /**
     * Sets the update branch.
     */
    fun updateBranch(branchName: String, branchPhone: String) {
        try {
            val branches = uiState.value.branches.toMutableList()

            val index = branches.indexOf(uiState.value.branchSelected)
            val currentBranch = branches[index]
            val updateBranch =
                Branch(uiState.value.branchSelected.branchCode, branchName, branchPhone)

            if (currentBranch != updateBranch) {
                updateBranch.also { branches[index] = it }
                _uiState.update { it.copy(branches = branches) }
                showSnackbarMessage(uiString.core_ui_success_update_branch_message)
            } else {
                showSnackbarMessage(uiString.core_ui_error_update_branch_message)
            }
        } catch (e: IndexOutOfBoundsException) {
            showSnackbarMessage(uiString.core_ui_error_update_branch_message)
        }
    }

    private fun incrementBranchCode(): Int {
        return if (uiState.value.branches.isNotEmpty()) {
            uiState.value.branches.last().branchCode + 1
        } else {
            FIRST_BRANCH_NUMBER
        }
    }

    fun setBranchSelected(branch: Branch) {
        _uiState.update { it.copy(branchSelected = branch) }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun addBusiness(): Business {
        return Business(
            storeType = uiState.value.storeType.toStoreType(),
            email = uiState.value.emailBusiness,
            phone = uiState.value.phoneBusiness,
            branches = uiState.value.branches.toList(),
        )
    }

    fun setBusiness() =
        viewModelScope.launch {
            if (uiState.value.isOnline) {
                // TODO: when no uid move  to sign in screen.
                setBusinessUseCase(addBusiness()).collect { addBusiness ->
                    when (addBusiness) {
                        is Resource.Success -> {
                            _uiState.update { it.copy(isLoading = false) }
                            showSnackbarMessage(R.string.feature_stepper_success_add_business_message)
                            nextStep()
                        }

                        is Resource.Empty, is Resource.Error -> {
                            _uiState.update { it.copy(isLoading = false) }

                            val messageRes =
                                (addBusiness as? Resource.Empty)?.message
                                    ?: (addBusiness as? Resource.Error)?.message
                            showSnackbarMessage(messageRes as? Int ?: uiString.core_ui_error_unknown)
                        }

                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }

            } else {
                showSnackbarMessage(uiString.core_ui_error_network)
            }
        }

    fun getSubscriptionsBusiness() =
        viewModelScope.launch {
            if (uiState.value.subscriptions.isEmpty()) {
                getSubscriptionsUseCase().collect { subscriptionsResource ->
                    when (subscriptionsResource) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    subscriptions = subscriptionsResource.data.toMutableList(),
                                    isLoading = false,
                                    currentSubscription = subscriptionsResource.data.first(),
                                )
                            }
                        }

                        is Resource.Empty -> {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                        is Resource.Error -> {
                            _uiState.update { it.copy(isLoading = false) }
                            showSnackbarMessage(
                                subscriptionsResource.message as? Int ?: uiString.core_ui_error_unknown,
                            )
                        }

                    }
                }
            }
        }

    fun addSubscriptionBusinessSelected(subscription: Subscription) {
        _uiState.update { it.copy(currentSubscription = subscription) }
    }

    fun checkNetworkThenSetSubscriptionBusinessSelected() {
        if (uiState.value.isOnline) {
            addSubscriptionBusinessSelected()
        } else {
            showSnackbarMessage(uiString.core_ui_error_network)
        }
    }

    private fun addSubscriptionBusinessSelected() =
        viewModelScope.launch {

            val subscription = uiState.value.currentSubscription

            when (val resourceAddSubscription =
                setSubscriptionsBusinessUseCase(subscription.asSubscriptionBusiness())) {
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    showSnackbarMessage(uiString.core_ui_success_add_subscription_message)
                    _uiState.update { it.copy(isLoading = false) }

                    if (resourceAddSubscription.data) {
                        nextStep()
                    }
                }

                is Resource.Error, is Resource.Empty -> {
                    _uiState.update { it.copy(isLoading = false) }

                    val messageRes =
                        (resourceAddSubscription as? Resource.Empty)?.message
                            ?: (resourceAddSubscription as? Resource.Error)?.message

                    showSnackbarMessage(messageRes as? Int ?: uiString.core_ui_error_unknown)
                }
            }
        }

    fun addEmployee(
        name: String,
        phone: String,
        password: String,
        branchName: String,
        permission: String,
    ) {
        val currentEmployees = uiState.value.employees
        val employee = Employee(name, phone, password, branchName, permission)
        if (isEmployeeNameDuplicate(currentEmployees, employee)) {
            showSnackbarMessage(uiString.core_ui_error_employee_name_duplicate)
            return
        }

        val newEmployees = currentEmployees.toMutableList().apply { add(employee) }
        if (currentEmployees.size < newEmployees.size) {
            _uiState.update { it.copy(employees = newEmployees) }
            showSnackbarMessage(uiString.core_ui_success_add_employee_message)
        } else {
            showSnackbarMessage(uiString.core_ui_error_add_employee_message)
        }
    }

    private fun isEmployeeNameDuplicate(
        currentEmployees: List<Employee>,
        employee: Employee,
    ): Boolean {
        currentEmployees.forEach {
            if (it.name == employee.name) {
                return true
            }
        }
        return false
    }

    fun setEmployeeSelected(employeeSelect: Employee) {
        _uiState.update { it.copy(employeeSelected = employeeSelect) }
    }

    fun updateEmployee(
        name: String,
        phone: String,
        password: String,
        branchName: String,
        permission: String,
    ) {
        val employee = Employee(name, phone, password, branchName, permission)
        updateEmployee(employee)
    }

    private fun updateEmployee(employee: Employee) {
        val employeesUpdate = uiState.value.employees.toMutableList()
        val index = employeesUpdate.indexOf(uiState.value.employeeSelected)
        if (isOutOfIndex(index)) return showSnackbarMessage(uiString.core_ui_error_update_employee_message)

        val currentEmployee = employeesUpdate[index]
        if (employeesUpdate.isUpdateEmployeeNameDuplicate(employee)) return

        if (currentEmployee != employee) {
            employeesUpdate[index] = employee
            _uiState.update { it.copy(employees = employeesUpdate) }
            showSnackbarMessage(uiString.core_ui_success_update_employee_message)
        } else {
            showSnackbarMessage(uiString.core_ui_error_update_employee_message)
        }
    }

    private fun isOutOfIndex(index: Int): Boolean {
        return index == -1
    }

    private fun List<Employee>.isUpdateEmployeeNameDuplicate(employee: Employee): Boolean {
        this.forEach {
            if (it.name == employee.name && it != uiState.value.employeeSelected) {
                showSnackbarMessage(uiString.core_ui_error_employee_name_duplicate)
                return true
            }
        }
        return false
    }

    fun checkNetworkThenSetEmployees() {
        if (uiState.value.isOnline) {
            setEmployeesBusiness()
        } else {
            showSnackbarMessage(uiString.core_ui_error_network)
        }
    }

    private fun setEmployeesBusiness() =
        viewModelScope.launch {
            val employeesList = uiState.value.employees

            when (val resourceAddEmployees = setEmployeesBusinessUseCase(employeesList)) {
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }

                is Resource.Success -> {
                    if (resourceAddEmployees.data) {
                        showSnackbarMessage(uiString.core_ui_success_add_employees_message)
                        // Then, go on business complete step.
                        setCompletedBusinessStep()
                    }

                }

                is Resource.Error, is Resource.Empty -> {
                    val messageRes =
                        (resourceAddEmployees as? Resource.Empty)?.message
                            ?: (resourceAddEmployees as? Resource.Error)?.message

                    showSnackbarMessage(messageRes as? Int ?: uiString.core_ui_error_unknown)
                }
            }

        }


    private fun setCompletedBusinessStep() {
        viewModelScope.launch {
            when (val isCompleteBusinessStep = completeBusinessUseCase()) {
                is Resource.Success -> {
                    showSnackbarMessage(R.string.feature_stepper_success_complete_business_setup_message)
                    completeStep()
                }

                is Resource.Empty, is Resource.Error -> {
                    val messageRes =
                        (isCompleteBusinessStep as? Resource.Empty)?.message
                            ?: (isCompleteBusinessStep as? Resource.Error)?.message

                    showSnackbarMessage(messageRes as? Int ?: uiString.core_ui_error_unknown)
                }

                is Resource.Loading -> {}

            }
        }
    }


    fun nextStep() {
        viewModelScope.launch {
            _buttonStepState.update { it.copy(buttonNextStep = true) }
        }
    }

    fun restNextStep() {
        viewModelScope.launch {
            _buttonStepState.update { it.copy(buttonNextStep = false) }

        }
    }

    fun previousStep() {
        viewModelScope.launch {
            _buttonStepState.update { it.copy(buttonPreviousStep = true) }

        }
    }

    fun restPreviousStep() {
        viewModelScope.launch {
            _buttonStepState.update { it.copy(buttonPreviousStep = false) }

        }
    }

    private fun completeStep() {
        viewModelScope.launch {
            _buttonStepState.update { it.copy(buttonCompletedSteps = true) }

        }
    }

    private fun showSnackbarMessage(message: Int) {
        viewModelScope.launch {
            _userMessage.update { message }
        }
    }

    fun snackbarMessageShown() {
        _userMessage.update { null }
    }
}

private const val FIRST_BRANCH_NUMBER = 1