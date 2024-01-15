package com.casecode.pos.viewmodel

import androidx.annotation.OpenForTesting
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.casecode.data.mapper.asSubscriptionBusiness
import com.casecode.data.utils.NetworkMonitor
import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.Business
import com.casecode.domain.model.users.Employee
import com.casecode.domain.model.users.StoreType
import com.casecode.domain.repository.AddBusiness
import com.casecode.domain.repository.AddEmployees
import com.casecode.domain.repository.AddSubscriptionBusiness
import com.casecode.domain.repository.SubscriptionsResource
import com.casecode.domain.usecase.GetSubscriptionsUseCase
import com.casecode.domain.usecase.SetBusinessUseCase
import com.casecode.domain.usecase.SetEmployeesBusinessUseCase
import com.casecode.domain.usecase.SetSubscriptionBusinessUseCase
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
class BusinessViewModel @Inject constructor(
   //   private val getStoreUseCase: GetStoreUseCase,
     private val networkMonitor: NetworkMonitor,
     private val firebaseAuth: FirebaseAuth,
     private val setBusinessUseCase: SetBusinessUseCase,
     private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
     private val setSubscriptionsBusinessUseCase: SetSubscriptionBusinessUseCase,
     private val setEmployeesBusinessUseCase: SetEmployeesBusinessUseCase,
                                           ) : BaseViewModel()
{
   
   private val _isOnline: MutableLiveData<Boolean> = MutableLiveData(false)
   val isOnline get() = _isOnline
   private val _userMessage: MutableLiveData<Event<Int>> = MutableLiveData()
   val userMessage get() = _userMessage
   var isAddBusiness: MutableLiveData<AddBusiness?> = MutableLiveData()
      private set
   
   private val _storeType: MutableLiveData<String> = MutableLiveData()
   val storeType: LiveData<String>
      get() = _storeType
   
   private val _email: MutableLiveData<String> = MutableLiveData()
   val emailBusiness: LiveData<String>
      get() = _email
   
   private val _phoneBusiness: MutableLiveData<String> = MutableLiveData()
   val phoneBusiness: LiveData<String>
      get() = _phoneBusiness
   
   
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
   
   private val _subscriptions: MutableLiveData<List<Subscription>> = MutableLiveData()
   val subscriptions: LiveData<List<Subscription>> get() = _subscriptions
   
   private val _isErrorDataSubscriptions: MutableLiveData<Boolean> = MutableLiveData()
   
   private var _subscriptionSelected: MutableLiveData<Subscription> = MutableLiveData()
   
   private var _employees: MutableLiveData<ArrayList<Employee>> =
      MutableLiveData(arrayListOf(newEmployee()))
   val employees get() = _employees
   
   private var _employee: Employee = Employee()
   val employee get() = _employee
   
   private val _isAddEmployee: MutableLiveData<Event<Boolean>> = MutableLiveData()
   val isAddEmployee: LiveData<Event<Boolean>> get() = _isAddEmployee
   
   private val _isUpdateEmployee: MutableLiveData<Event<Boolean>> = MutableLiveData()
   val isUpdateEmployee: LiveData<Event<Boolean>> get() = _isUpdateEmployee
   
   private val _employeeSelected: MutableLiveData<Employee> = MutableLiveData()
   val employeeSelected get() = _employeeSelected
   
   private  val _isAddSubscriptionBusiness: MutableLiveData<AddSubscriptionBusiness?> = MutableLiveData()
   val isAddSubscriptionBusiness get() = _isAddSubscriptionBusiness
   
   private val _isAddEmployees: MutableLiveData<Event<AddEmployees>> = MutableLiveData()
   val isAddEmployees get() = _isAddEmployees
   
   private val _buttonNextStep: MutableLiveData<Event<Unit>> = MutableLiveData()
   val buttonNextStep: LiveData<Event<Unit>> get() = _buttonNextStep
   
   private val _buttonPreviousStep: MutableLiveData<Event<Unit>> = MutableLiveData()
   val buttonPreviousStep: LiveData<Event<Unit>> get() = _buttonPreviousStep
   
   private val _buttonCompletedSteps: MutableLiveData<Event<Unit>> = MutableLiveData()
   val buttonCompletedSteps get() = _buttonCompletedSteps
   
   private var _isCompact: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(true))
   val isCompact get() = _isCompact
   
   init
   {
      hideProgress()
      val uid = firebaseAuth.currentUser?.uid ?: ""
      
      setCurrentUid(uid)
   }
   
   fun setNetworkMonitor() = viewModelScope.launch {
      networkMonitor.isOnline.collect {
         setConnected(it)
      }
      
      
   }
   
   fun setConnected(isOnline: Boolean)
   {
      _isOnline.value = isOnline
   }
   
   fun snackbarMessageShown()
   {
   
   }
   
   private fun showSnackbarMessage(@StringRes message: Int)
   {
      Timber.e("message: $message")
      _userMessage.value = Event(message)
   }
   
   fun setStoreType(store: String)
   {
      _storeType.value = store
   }
   
   fun setEmail(email: String)
   {
      _email.value = email
   }
   
   fun setPhoneBusiness(phone: String)
   {
      _phoneBusiness.value = phone
      
   }
   
   
   fun setBranchName(name: String)
   {
      _branchName.value = name
   }
   
   fun setBranchPhone(phone: String)
   {
      _branchPhone.value = phone
   }
   
   fun addBranch()
   {
      
      incrementBranchCode()
      val branch = Branch(_branchCode.value, _branchName.value, _branchPhone.value)
      
      val branchesValue = branches.value ?: ArrayList()
      val oldBranchesSize = _branches.value?.size ?: 0
      branchesValue.add(branch)
      
      if (oldBranchesSize < branchesValue.size)
      {
         _branches.value = branchesValue
         _isAddBranch.value = Event(true)
         showSnackbarMessage(R.string.add_branch_success)
         
      } else
      {
         _isAddBranch.value = Event(false)
         
         showSnackbarMessage(R.string.add_branch_fail)
         
      }
      
      Timber.e("addBranch:branches = ${_branches.value}")
   }
   
   /**
    * Sets the update branch.
    */
   fun updateBranch()
   {
      try
      {
         val branchesValue = _branches.value ?: ArrayList()
         
         val index = branchesValue.indexOf(_branchSelected.value)
         val currentBranch = branchesValue[index]
         val updateBranch =
            Branch(branchSelected.value?.branchCode, _branchName.value, _branchPhone.value)
         
         if (currentBranch != updateBranch)
         {
            branchesValue[index] = updateBranch
            _branches.value = (branchesValue)
            _isUpdateBranch.value = Event(true)
            showSnackbarMessage(R.string.update_branch_success)
            
         } else
         {
            _isUpdateBranch.value = Event(false)
            showSnackbarMessage(R.string.update_branch_fail)
         }
      } catch (e: IndexOutOfBoundsException)
      {
         _isUpdateBranch.value = Event(false)
         showSnackbarMessage(R.string.update_branch_fail)
      }
      
      
   }
   
   private fun incrementBranchCode()
   {
      _branchCode.value = _branchCode.value?.plus(1)
   }
   
   fun setBranchSelected(branch: Branch)
   {
      _branchSelected.value = branch
   }
   
   @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
   fun addBusiness(): Business
   {
      Timber.e("storeType = ${storeType.value} phone  = ${_phoneBusiness.value}")
      return Business(
         storeType = (storeType.value?.let { StoreType.toStoreType(it) }),
         email = emailBusiness.value,
         phone = phoneBusiness.value,
         branches = _branches.value?.toList() !!
                     )
   }
   
   fun setBusiness() = viewModelScope.launch {
      if (isOnline.value == true)
      {
         // COMPLETE: when no uid move  to sign in screen.
         val uid = currentUid.value ?: ""
         
         isAddBusiness.value = setBusinessUseCase(addBusiness(), uid)
      } else
      {
         isAddBusiness.value = AddBusiness.success(false)
         showSnackbarMessage(R.string.network_error)
      }
      
   }
   
   fun getSubscriptionsBusiness() = viewModelScope.launch {
      if (_subscriptions.value?.isEmpty() == false || ! _subscriptions.isInitialized)
      {
         showProgress()
         getSubscriptionsUseCase().collect { subscriptionsResource ->
            println("getSubscriptionsBusiness, empty = true")
            handleSubscriptionsResource(subscriptionsResource)
         }
      }
      
      
   }
   
   private fun handleSubscriptionsResource(subscriptionsResource: SubscriptionsResource)
   {
      when (subscriptionsResource)
      {
         
         is Resource.Loading ->
         {
            showProgress()
         }
         
         is Resource.Success ->
         {
            // Select first subscription from the subscription list
            val subscriptionFree = subscriptionsResource.data.first()
            setSubscriptionBusinessSelected(subscriptionFree)
            
            _subscriptions.value = subscriptionsResource.data
            Timber.i("getSubscriptions:Success:data, ${subscriptionsResource.data}")
            
            hideProgress()
         }
         
         is Resource.Error ->
         {
            hideProgress()
            _subscriptions.value = emptyList()
         }
         
         is Resource.Empty ->
         {
            println("getSubscriptions:Empty")
            hideProgress()
            _subscriptions.value = emptyList()
            
            //TODO: handle when resource get empty
            Timber.e("getSubscriptions:ELSE")
            
         }
      }
   }
   
   fun setSubscriptionBusinessSelected(subscription: Subscription)
   {
      _subscriptionSelected.value = subscription
   }
   
   fun checkNetworkThenSetSubscriptionBusinessSelected()
   {
      if (isOnline.value == true)
      {
         setSubscriptionBusinessSelected()
      } else
      {
         _isAddSubscriptionBusiness.value = AddSubscriptionBusiness.success(false)
         showSnackbarMessage(R.string.network_error)
      }
   }
   
   private fun setSubscriptionBusinessSelected() = viewModelScope.launch {
      
      showProgress()
      val uid = currentUid.value ?: ""
      val subscription = _subscriptionSelected.value
      if (subscription != null)
      {
         Timber.d("setSubscriptionBusinessSelected not equal null")
         _isAddSubscriptionBusiness.value =
            setSubscriptionsBusinessUseCase(subscription.asSubscriptionBusiness(), uid)
         showSnackbarMessage(R.string.add_subscription_success)
      } else
      {
         Timber.e("setSubscriptionBusinessSelected is equal null")
         _isAddSubscriptionBusiness.value = AddSubscriptionBusiness.empty()
      }
      
      isAddSubscriptionBusiness()
   }
   
   fun isAddSubscriptionBusiness()
   {
      when (val resourceIsAddSubscription = _isAddSubscriptionBusiness.value)
      {
         is Resource.Loading ->
         {
            showProgress()
         }
         
         is Resource.Success ->
         {
            hideProgress()
            
            if (resourceIsAddSubscription.data)
            {
               moveToNextStep()
            }
         }
         
         is Resource.Empty ->
         {
            hideProgress()
            showSnackbarMessage(R.string.add_subscription_business_empty)
            
         }
         
         is Resource.Error ->
         {
            hideProgress()
            showSnackbarMessage(resourceIsAddSubscription.message as Int)
         
         }
         
         null -> {
            showSnackbarMessage(R.string.all_error_save)
            
         }
      }
      
      
   }
   
   
   fun setEmployees() = viewModelScope.launch {
      val uid = currentUid.value ?: ""
      val employeesList = _employees.value
      if (employeesList != null)
      {
         _isAddEmployees.value = Event(setEmployeesBusinessUseCase(employeesList, uid))
      } else
      {
         _isAddEmployees.value = Event(Resource.Empty())
         showSnackbarMessage(R.string.all_error_save)
         
      }
   }
   
   fun moveToNextStep()
   {
      _buttonNextStep.value = Event(Unit)
   }
   
   private fun newEmployee(): Employee
   {
      return Employee(name = "mahmoud",
         phoneNumber = "",
         password = "123456",
         branchName = "Branch name",
         permission = "Admin")
   }
   
   fun setEmployee(
        name: String,
        phone: String,
        password: String,
        branchName: String,
        permission: String,
                  )
   {
      require(name.isNotEmpty()) { "Name cannot be empty" }
      require(phone.isNotEmpty()) { "Phone number cannot be empty" }
      require(branchName.isNotEmpty()) { "branch name cannot be empty" }
      require(permission.isNotEmpty()) { "Permission cannot be empty" }
      
      _employee = Employee(name, phone, password, branchName, permission)
      
   }
   
   fun addEmployee()
   {
      
      val employeesValue = _employees.value ?: ArrayList()
      val oldBranchesSize = _employees.value?.size ?: 0
      employeesValue.add(employee)
      
      if (oldBranchesSize < employeesValue.size)
      {
         _employees.value = employeesValue
         _isAddEmployee.value = Event(true)
         showSnackbarMessage(R.string.add_employee_success)
         
      } else
      {
         _isAddEmployee.value = Event(false)
         
         showSnackbarMessage(R.string.add_employee_fail)
         
      }
      
      Timber.e("employees size = ${_employees.value?.size}")
   }
   
   fun setEmployeeSelected(item: Employee)
   {
      _employeeSelected.value = item
   }
   
   fun updateEmployee()
   {
      try
      {
         val employeesValue = _employees.value ?: ArrayList()
         
         val index = employeesValue.indexOf(_employeeSelected.value)
         val currentEmployee = employeesValue[index]
         val updateEmployee = employee
         
         if (currentEmployee != updateEmployee)
         {
            employeesValue[index] = updateEmployee
            _employees.value = employeesValue
            _isUpdateEmployee.value = Event(true)
            showSnackbarMessage(R.string.update_employee_success)
            
         } else
         {
            _isUpdateEmployee.value = Event(false)
            showSnackbarMessage(R.string.update_employee_fail)
         }
         
      } catch (e: IndexOutOfBoundsException)
      {
         _isUpdateBranch.value = Event(false)
         showSnackbarMessage(R.string.update_branch_fail)
      }
      
   }
   
   fun moveToPreviousStep()
   {
      _buttonPreviousStep.value = Event(Unit)
      
   }
   
   fun completedSteps()
   {
      _buttonCompletedSteps.value = Event(Unit)
      
   }
   
   fun setCompact(isCompact: Boolean)
   {
      _isCompact.value = Event(isCompact)
   }
   
}


