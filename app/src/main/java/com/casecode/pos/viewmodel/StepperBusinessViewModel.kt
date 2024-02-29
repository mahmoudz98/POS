package com.casecode.pos.viewmodel

import androidx.annotation.OpenForTesting
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.casecode.data.mapper.asSubscriptionBusiness
import com.casecode.data.mapper.toStoreType
import com.casecode.data.utils.NetworkMonitor
import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.Business
import com.casecode.domain.model.users.Employee
import com.casecode.domain.repository.AddBusiness
import com.casecode.domain.repository.AddEmployees
import com.casecode.domain.repository.AddSubscriptionBusiness
import com.casecode.domain.repository.CompleteBusiness
import com.casecode.domain.repository.SubscriptionsResource
import com.casecode.domain.usecase.CompleteBusinessUseCase
import com.casecode.domain.usecase.GetSubscriptionsUseCase
import com.casecode.domain.usecase.SetBusinessUseCase
import com.casecode.domain.usecase.SetEmployeesBusinessUseCase
import com.casecode.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.domain.usecase.SignOutUseCase
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.base.BaseViewModel
import com.casecode.pos.utils.Event
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Viewmodel for business screen.
 */
@OpenForTesting
@HiltViewModel
class StepperBusinessViewModel
    @Inject
    constructor(
        private val networkMonitor: NetworkMonitor,
        private val firebaseAuth: FirebaseAuth,
        private val signOutUseCase: SignOutUseCase,
        private val setBusinessUseCase: SetBusinessUseCase,
        private val completeBusinessUseCase: CompleteBusinessUseCase,
        private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
        private val setSubscriptionsBusinessUseCase: SetSubscriptionBusinessUseCase,
        private val setEmployeesBusinessUseCase: SetEmployeesBusinessUseCase,
    ) : BaseViewModel() {
        // Observables
        private val _isOnline: MutableLiveData<Boolean> = MutableLiveData(false)
        val isOnline get() = _isOnline
        private val _userMessage: MutableLiveData<Event<Int>> = MutableLiveData()
        val userMessage get() = _userMessage
        var isAddBusiness: MutableLiveData<AddBusiness?> = MutableLiveData()
            private set

        // Business data
        private val _storeType: MutableLiveData<String> = MutableLiveData()
        val storeType: LiveData<String>
            get() = _storeType

        private val _email: MutableLiveData<String> = MutableLiveData()
        val emailBusiness: LiveData<String>
            get() = _email

        private val _phoneBusiness: MutableLiveData<String> = MutableLiveData()
        val phoneBusiness: LiveData<String>
            get() = _phoneBusiness

        // Branches data
        private val _branches: MutableLiveData<ArrayList<Branch>> = MutableLiveData(ArrayList())
        val branches
            get() = _branches

        private val branch: MutableLiveData<Branch> = MutableLiveData()

        private val _branchCode: MutableLiveData<Int> = MutableLiveData(0)

        private var _branchName: MutableLiveData<String> = MutableLiveData()

        private var _branchPhone: MutableLiveData<String> = MutableLiveData()

        private val _branchSelected: MutableLiveData<Branch> = MutableLiveData()
        val branchSelected: LiveData<Branch>
            get() = _branchSelected

        private val _isAddBranch: MutableLiveData<Event<Boolean>> = MutableLiveData()
        val isAddBranch: LiveData<Event<Boolean>> get() = _isAddBranch
        private var _isUpdateBranch: MutableLiveData<Event<Boolean>> = MutableLiveData()
        val isUpdateBranch get() = _isUpdateBranch

        // Subscriptions data
        private val _subscriptions: MutableLiveData<List<Subscription>> = MutableLiveData()
        val subscriptions: LiveData<List<Subscription>> get() = _subscriptions

        private var _subscriptionSelected: MutableLiveData<Subscription> = MutableLiveData()
        private val _isSubscriptionsError = MutableLiveData<Boolean>()
        val isSubscriptionsError get() = _isSubscriptionsError

        // Employees data
        private var _employees: MutableLiveData<MutableList<Employee>> = MutableLiveData()
        val employees get() = _employees

        private var _employee: Employee = Employee()
        val employee get() = _employee

        private val _isAddEmployee: MutableLiveData<Event<Boolean>> = MutableLiveData()
        val isAddEmployee get() = _isAddEmployee

        private val _isUpdateEmployee: MutableLiveData<Event<Boolean>> = MutableLiveData()
        val isUpdateEmployee get() = _isUpdateEmployee

        private val _employeeSelected: MutableLiveData<Employee> = MutableLiveData()
        val employeeSelected get() = _employeeSelected

        // Subscription business data
        private val _isAddSubscriptionBusiness: MutableLiveData<AddSubscriptionBusiness?> =
            MutableLiveData()
        val isAddSubscriptionBusiness get() = _isAddSubscriptionBusiness

        // Employees business data
        private val _isAddEmployees: MutableLiveData<AddEmployees?> = MutableLiveData()
        val isAddEmployees get() = _isAddEmployees

        // Business completion data
        private val _isCompletedBusinessStep = MutableLiveData<CompleteBusiness>()

        // Navigation events
        private val _buttonNextStep: MutableLiveData<Event<Unit>> = MutableLiveData()
        val buttonNextStep: LiveData<Event<Unit>> get() = _buttonNextStep

        private val _buttonPreviousStep: MutableLiveData<Event<Unit>> = MutableLiveData()
        val buttonPreviousStep: LiveData<Event<Unit>> get() = _buttonPreviousStep

        private val _buttonCompletedSteps: MutableLiveData<Event<Unit>> = MutableLiveData()
        val buttonCompletedSteps get() = _buttonCompletedSteps

        // UI state
        private var _isCompact: MutableLiveData<Boolean> = MutableLiveData(true)
        val isCompact get() = _isCompact

        init {
            // Hide progress initially
            hideProgress()

            // Get the current user's UID
            val uid = firebaseAuth.currentUser?.uid ?: ""

            // Set the current UID
            setCurrentUid(uid)
        }

        fun setNetworkMonitor() =
            viewModelScope.launch {
                networkMonitor.isOnline.collect {
                    setConnected(it)
                }
            }

        fun signOut() = viewModelScope.launch { signOutUseCase() }

        fun setConnected(isOnline: Boolean) {
            _isOnline.value = isOnline
        }

        private fun showSnackbarMessage(
            @StringRes message: Int,
        ) {
            Timber.e("message: $message")
            _userMessage.value = Event(message)
        }

        fun setStoreType(store: String) {
            _storeType.value = store
        }

        fun setEmail(email: String) {
            _email.value = email
        }

        fun setPhoneBusiness(phone: String) {
            _phoneBusiness.value = phone
        }

        fun setBranchName(name: String) {
            _branchName.value = name
        }

        fun setBranchPhone(phone: String) {
            _branchPhone.value = phone
        }

        fun addBranch() {
            incrementBranchCode()
            val branch = Branch(_branchCode.value, _branchName.value, _branchPhone.value)

            val branchesValue = branches.value ?: ArrayList()
            val oldBranchesSize = _branches.value?.size ?: 0
            branchesValue.add(branch)

            if (oldBranchesSize < branchesValue.size) {
                _branches.value = branchesValue
                _isAddBranch.value = Event(true)
                showSnackbarMessage(R.string.add_branch_success)
            } else {
                _isAddBranch.value = Event(false)

                showSnackbarMessage(R.string.add_branch_fail)
            }

            Timber.e("addBranch:branches = ${_branches.value}")
        }

        /**
         * Sets the update branch.
         */
        fun updateBranch() {
            try {
                val branchesValue = _branches.value ?: ArrayList()

                val index = branchesValue.indexOf(_branchSelected.value)
                val currentBranch = branchesValue[index]
                val updateBranch =
                    Branch(branchSelected.value?.branchCode, _branchName.value, _branchPhone.value)

                if (currentBranch != updateBranch) {
                    branchesValue[index] = updateBranch
                    _branches.value = (branchesValue)
                    _isUpdateBranch.value = Event(true)
                    showSnackbarMessage(R.string.update_branch_success)
                } else {
                    _isUpdateBranch.value = Event(false)
                    showSnackbarMessage(R.string.update_branch_fail)
                }
            } catch (e: IndexOutOfBoundsException) {
                _isUpdateBranch.value = Event(false)
                showSnackbarMessage(R.string.update_branch_fail)
            }
        }

        private fun incrementBranchCode() {
            _branchCode.value = _branchCode.value?.plus(1)
        }

        fun setBranchSelected(branch: Branch) {
            _branchSelected.value = branch
        }

        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        fun addBusiness(): Business {
            return Business(
                storeType = _storeType.value.toString().toStoreType(),
                email = emailBusiness.value,
                phone = phoneBusiness.value,
                branches = _branches.value?.toList() ?: listOf(),
            )
        }

        fun setBusiness() =
            viewModelScope.launch {
                if (isOnline.value == true) {
                    // COMPLETE: when no uid move  to sign in screen.
                    val uid = currentUid.value ?: ""

                    isAddBusiness.value = setBusinessUseCase(addBusiness(), uid)
                    observerIsAddBusiness()
                } else {
                    showSnackbarMessage(R.string.network_error)
                }
            }

        private fun observerIsAddBusiness() {
            when (val isAddBusinessResource = isAddBusiness.value) {
                is Resource.Success -> {
                    if (isAddBusinessResource.data) {
                        showSnackbarMessage(R.string.add_business_success)
                        moveToNextStep()
                    }
                }

                is Resource.Empty, is Resource.Error -> {
                    val messageRes =
                        (isAddBusinessResource as? Resource.Empty)?.message
                            ?: (isAddBusinessResource as? Resource.Error)?.message
                    showSnackbarMessage(messageRes as? Int ?: R.string.all_error_save)
                }

                else -> {
                    showSnackbarMessage(R.string.all_error_save)
                }
            }
        }

        fun getSubscriptionsBusiness() =
            viewModelScope.launch {
                if (_subscriptions.value == null || subscriptions.value?.isEmpty() == true) {
                    getSubscriptionsUseCase().collect { subscriptionsResource ->
                        handleSubscriptionsResource(subscriptionsResource)
                    }
                }
            }

        fun subscriptionError() {
            if (_subscriptions.value == null || subscriptions.value?.isEmpty() == true) {
                _isSubscriptionsError.value = true
            }
        }

        private fun handleSubscriptionsResource(subscriptionsResource: SubscriptionsResource) {
            when (subscriptionsResource) {
                is Resource.Loading -> {
                    showProgress()
                    _isSubscriptionsError.value = false
                }

                is Resource.Success -> {
                    // Select first subscription from the subscription list
                    val subscriptionFree = subscriptionsResource.data.first()
                    addSubscriptionBusinessSelected(subscriptionFree)

                    _subscriptions.value = subscriptionsResource.data
                    Timber.i("getSubscriptions:Success:data, ${subscriptionsResource.data}")
                    _isSubscriptionsError.value = false

                    hideProgress()
                }

                else -> {
                    subscriptionError()
                    hideProgress()
                    _subscriptions.value = emptyList()
                }
            }
        }

        fun addSubscriptionBusinessSelected(subscription: Subscription) {
            _subscriptionSelected.value = subscription
        }

        fun checkNetworkThenSetSubscriptionBusinessSelected() {
            if (isOnline.value == true) {
                addSubscriptionBusinessSelected()
            } else {
                showSnackbarMessage(R.string.network_error)
            }
        }

        private fun addSubscriptionBusinessSelected() =
            viewModelScope.launch {
                showProgress()
                val uid = currentUid.value ?: ""
                val subscription = _subscriptionSelected.value
                _isAddSubscriptionBusiness.value =
                    subscription?.asSubscriptionBusiness()
                        ?.let { setSubscriptionsBusinessUseCase(it, uid) }

                isAddSubscriptionBusiness()
            }

        private fun isAddSubscriptionBusiness() {
            when (val resourceIsAddSubscription = _isAddSubscriptionBusiness.value) {
                is Resource.Loading -> {
                    showProgress()
                }

                is Resource.Success -> {
                    hideProgress()
                    showSnackbarMessage(R.string.add_subscription_success)

                    if (resourceIsAddSubscription.data) {
                        moveToNextStep()
                    }
                }

                is Resource.Error, is Resource.Empty -> {
                    hideProgress()
                    val messageRes =
                        (resourceIsAddSubscription as? Resource.Empty)?.message
                            ?: (resourceIsAddSubscription as? Resource.Error)?.message

                    showSnackbarMessage(messageRes as? Int ?: R.string.all_error_save)
                }

                else -> {
                    showSnackbarMessage(R.string.all_error_save)
                }
            }
        }

        fun addDefaultEmployee() {
            val employeesValue = _employees.value ?: ArrayList()
            if (employeesValue.isEmpty() && _branches.value?.isNotEmpty() == true) {
                employeesValue.add(defaultEmployee())
                _employees.value = employeesValue
            }
        }

        private fun defaultEmployee(): Employee {
            val name = firebaseAuth.currentUser?.displayName ?: "Admin"
            val phoneNumber = firebaseAuth.currentUser?.phoneNumber ?: phoneBusiness.value
            val branchName = _branches.value?.get(0)?.branchName ?: ""
            return Employee(
                name = name,
                phoneNumber = phoneNumber ?: "",
                password = "123456",
                branchName = branchName,
                permission = "Admin",
            )
        }

        fun addEmployee(
            name: String,
            phone: String,
            password: String,
            branchName: String,
            permission: String,
        ) {
            _employee = Employee(name, phone, password, branchName, permission)
            addEmployee()
        }

        private fun addEmployee() {
            val currentEmployees = _employees.value ?: emptyList()

            if (isEmployeeNameDuplicate(currentEmployees)) return
            val newEmployees = currentEmployees.toMutableList().apply { add(_employee) }
            if (currentEmployees.size < newEmployees.size) {
                _employees.value = newEmployees
                _isAddEmployee.value = Event(true)
                showSnackbarMessage(R.string.add_employee_success)
            } else {
                _isAddEmployee.value = Event(false)
                showSnackbarMessage(R.string.add_employee_fail)
            }
        }

        private fun isEmployeeNameDuplicate(currentEmployees: List<Employee>): Boolean {
            currentEmployees.forEach {
                if (it.name == _employee.name) {
                    _isAddEmployee.value = Event(false)
                    showSnackbarMessage(R.string.add_employee_duplicate)
                    return true
                }
            }
            return false
        }

        fun setEmployeeSelected(employeeSelect: Employee) {
            _employeeSelected.value = employeeSelect
        }

        fun updateEmployee(
            name: String,
            phone: String,
            password: String,
            branchName: String,
            permission: String,
        ) {
            _employee = Employee(name, phone, password, branchName, permission)
            updateEmployee()
        }

        private fun updateEmployee() {
            val employeesValue = _employees.value ?: ArrayList()
            val index = employeesValue.indexOf(_employeeSelected.value)
            if (isOutOfIndex(index)) return

            val currentEmployee = employeesValue[index]
            val updateEmployee = _employee
            if (isUpdateEmployeeNameDuplicate(employeesValue)) return

            if (currentEmployee != updateEmployee) {
                employeesValue[index] = updateEmployee
                _employees.value = employeesValue
                _isUpdateEmployee.value = Event(true)
                showSnackbarMessage(R.string.update_employee_success)
            } else {
                _isUpdateEmployee.value = Event(false)
                showSnackbarMessage(R.string.update_employee_fail)
            }
        }

        private fun isOutOfIndex(index: Int): Boolean {
            if (index == -1) {
                _isUpdateBranch.value = Event(false)
                showSnackbarMessage(R.string.update_employee_fail)
                return true
            }
            return false
        }

        private fun isUpdateEmployeeNameDuplicate(currentEmployees: List<Employee>): Boolean {
            currentEmployees.forEach {
                if (it.name == _employee.name && it != employeeSelected.value) {
                    _isUpdateEmployee.value = Event(false)
                    showSnackbarMessage(R.string.add_employee_duplicate)
                    return true
                }
            }
            return false
        }

        fun checkNetworkThenSetEmployees() {
            if (isOnline.value == true) {
                setEmployeesBusiness()
            } else {
                showSnackbarMessage(R.string.network_error)
            }
        }

        private fun setEmployeesBusiness() =
            viewModelScope.launch {
                val uid = currentUid.value ?: ""
                val employeesList = _employees.value ?: mutableListOf()

                _isAddEmployees.value = setEmployeesBusinessUseCase(employeesList, uid)
                checkIsAddEmployees()
            }

        private fun checkIsAddEmployees() {
            when (val isAddEmployeesResource = isAddEmployees.value) {
                is Resource.Success -> {
                    if (isAddEmployeesResource.data) {
                        showSnackbarMessage(R.string.add_employees_success)
                        // Then, go on business complete step.
                        setCompletedBusinessStep()
                    }
                }

                is Resource.Error, is Resource.Empty -> {
                    val messageRes =
                        (isAddEmployeesResource as? Resource.Empty)?.message
                            ?: (isAddEmployeesResource as? Resource.Error)?.message

                    showSnackbarMessage(messageRes as? Int ?: R.string.all_error_save)
                }

                else -> {
                    showSnackbarMessage(R.string.all_error_save)
                }
            }
        }

        private fun setCompletedBusinessStep() {
            viewModelScope.launch {
                val uid = currentUid.value ?: ""
                _isCompletedBusinessStep.value = completeBusinessUseCase(uid)
                checkIsCompleteBusinessStep()
            }
        }

        private fun checkIsCompleteBusinessStep() {
            when (val isCompleteBusinessStep = _isCompletedBusinessStep.value) {
                is Resource.Success -> {
                    showSnackbarMessage(R.string.complete_business_step_success)
                    completedSteps()
                }

                is Resource.Empty, is Resource.Error -> {
                    val messageRes =
                        (isCompleteBusinessStep as? Resource.Empty)?.message
                            ?: (isCompleteBusinessStep as? Resource.Error)?.message

                    showSnackbarMessage(messageRes as? Int ?: R.string.all_error_save)
                }

                is Resource.Loading -> {}
                null -> {
                    showSnackbarMessage(R.string.all_error_save)
                }
            }
        }

        fun moveToNextStep() {
            _buttonNextStep.value = Event(Unit)
        }

        fun moveToPreviousStep() {
            _buttonPreviousStep.value = Event(Unit)
        }

        private fun completedSteps() {
            _buttonCompletedSteps.value = Event(Unit)
        }

        fun setCompact(isCompact: Boolean) {
            _isCompact.value = (isCompact)
        }
    }