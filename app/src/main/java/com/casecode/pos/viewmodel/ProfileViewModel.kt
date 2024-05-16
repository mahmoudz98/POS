package com.casecode.pos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.casecode.domain.model.users.Business
import com.casecode.domain.usecase.GetBusinessUseCase
import com.casecode.domain.usecase.GetCurrentUserUseCase
import com.casecode.domain.usecase.GetSubscriptionBusinessUseCase
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.base.BaseViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getBusinessUseCase: GetBusinessUseCase,
    private val getSubscriptionBusinessUseCase: GetSubscriptionBusinessUseCase,
) : BaseViewModel() {
    private val _currentUser: MutableLiveData<FirebaseUser> = MutableLiveData()
    val currentUser  get() = _currentUser

    private val _business: MutableLiveData<Business> = MutableLiveData()
    val business get() = _business

    init {
        fetchCurrentUser()
        fetchBusiness()
    }
    fun fetchCurrentUser(){
        viewModelScope.launch {
            getCurrentUserUseCase().collect{
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

}