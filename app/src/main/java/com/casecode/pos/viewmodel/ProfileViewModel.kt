package com.casecode.pos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.casecode.domain.model.subscriptions.Subscription
import com.casecode.domain.model.users.Branch
import com.casecode.domain.model.users.Business
import com.casecode.domain.repository.SubscriptionsResource
import com.casecode.domain.usecase.AddBranchBusinessUseCase
import com.casecode.domain.usecase.GetBusinessUseCase
import com.casecode.domain.usecase.GetCurrentUserUseCase
import com.casecode.domain.usecase.GetSubscriptionBusinessUseCase
import com.casecode.domain.usecase.GetSubscriptionsUseCase
import com.casecode.domain.usecase.SetSubscriptionBusinessUseCase
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.base.BaseViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getBusinessUseCase: GetBusinessUseCase,
    private val addBranchBusinessUseCase: AddBranchBusinessUseCase,
    private val getSubscriptionBusinessUseCase: GetSubscriptionBusinessUseCase,
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
    private val setSubscriptionsBusinessUseCase: SetSubscriptionBusinessUseCase,
) : BaseViewModel() {
    private val _currentUser: MutableLiveData<FirebaseUser> = MutableLiveData()
    val currentUser get() = _currentUser

    private val _business: MutableLiveData<Business> = MutableLiveData()
    val business get() = _business
    private val branch: MutableLiveData<Branch> = MutableLiveData()
    private val _isSubscriptionsError = MutableLiveData<Boolean>()
    val isSubscriptionsError get() = _isSubscriptionsError
    // Subscriptions data
    private val _subscriptions: MutableLiveData<List<Subscription>> = MutableLiveData()
    val subscriptions: LiveData<List<Subscription>> get() = _subscriptions

    private var subscriptionSelected: MutableLiveData<Subscription> = MutableLiveData()

    private var _isCompact: MutableLiveData<Boolean> = MutableLiveData(true)
    val isCompact get() = _isCompact

    init {
        fetchCurrentUser()
        fetchBusiness()
    }

    private fun fetchCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect {
                _currentUser.value = it
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
                        val message = it.message as? Int ?: R.string.all_error_unknown
                        showSnackbarMessage(message)
                    }

                    Resource.Loading -> showProgress()
                    is Resource.Success -> {
                        _business.value = it.data
                    }
                }
            }
        }
    }

    fun setItemBranch(name: String, phone: String) {
        branch.value = Branch(branchName = name, phoneNumber = phone)
    }

    fun addBranch() {
        val lastBranchCode = _business.value?.branches?.last()?.branchCode?.inc()
        val branch = Branch(
            lastBranchCode,
            branchName = branch.value?.branchName,
            phoneNumber = branch.value?.phoneNumber,
        )
        viewModelScope.launch {
            when (val result = addBranchBusinessUseCase(branch)) {
                is Resource.Empty -> {}
                is Resource.Error -> {
                    val message = result.message as? Int ?: R.string.add_branch_fail
                    showSnackbarMessage(message)
                }

                Resource.Loading -> {}
                is Resource.Success -> {
                    showSnackbarMessage(R.string.add_branch_success)
                }
            }
        }
    }
    fun getSubscriptionsBusiness(){
        viewModelScope.launch {
            if (_subscriptions.value == null || subscriptions.value?.isEmpty() == true) {
                getSubscriptionsUseCase().collect { subscriptionsResource ->
                    handleSubscriptionsResource(subscriptionsResource)
                }
            }
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
                viewModelScope.launch {
                    getSubscriptionBusinessUseCase().collect{ result ->
                        when(result){
                            is Resource.Empty -> {}
                            is Resource.Error -> {}
                            Resource.Loading -> {}
                            is Resource.Success -> {
                                val sub = result.data.last()
                                val subscriptionSelect = subscriptionsResource.data.find { it.type == sub.type }
                                if(subscriptionSelect == null) return@collect showSnackbarMessage(R.string.all_error_unknown)
                                addSubscriptionBusinessSelected(subscriptionSelect)
                            }
                        }


                    }
                }
                _subscriptions.value = subscriptionsResource.data
                Timber.i("getSubscriptions:Success:data, ${subscriptionsResource.data}")
                _isSubscriptionsError.value = false

                hideProgress()
            }

            else -> {
                if (_subscriptions.value == null || subscriptions.value?.isEmpty() == true) {
                    _isSubscriptionsError.value = true
                }
                hideProgress()
                _subscriptions.value = emptyList()
            }
        }
    }
    fun addSubscriptionBusinessSelected(subscription: Subscription) {
        subscriptionSelected.value = subscription
    }
    fun setCompact(isCompact: Boolean) {
        _isCompact.value = (isCompact)
    }
}