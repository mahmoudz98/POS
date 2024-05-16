package com.casecode.pos.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.data.utils.NetworkMonitor
import com.casecode.domain.usecase.SignInUseCase
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.casecode.pos.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val signInUseCase: SignInUseCase,
) : ViewModel() {
    private val _isOnline: MutableLiveData<Boolean> = MutableLiveData(false)
    val isOnline get() = _isOnline
    private val _userMessage: MutableLiveData<Event<Int>> = MutableLiveData()
    val userMessage get() = _userMessage

    private val _signInResult = MutableLiveData<FirebaseAuthResult?>()

    val signInResult get() = _signInResult
    var isUserRegistration: MutableLiveData<Event<Boolean>> = MutableLiveData()
        private set

    private val uid: MutableLiveData<String> = MutableLiveData("")
    private val name: MutableLiveData<String> = MutableLiveData()
    private val password: MutableLiveData<String> = MutableLiveData()

    private val _isEmployeeLoginSuccess = MutableLiveData<Resource<Boolean>>()
    val isEmployeeLoginSuccess get() = _isEmployeeLoginSuccess

    private fun showSnackbarMessage(
        @StringRes message: Int,
    ) {
        Timber.e("message: $message")
        _userMessage.value = Event(message)
    }

    fun setNetworkMonitor() = viewModelScope.launch {
        networkMonitor.isOnline.collect {
            setConnected(it)
        }
    }

    private fun setConnected(isOnline: Boolean) {
        _isOnline.value = isOnline
    }

    fun signIn() {
        viewModelScope.launch {
            when(val result = signInUseCase()){
                is Resource.Loading ->{}
                is Resource.Empty -> showSnackbarMessage(result.message as Int)
                is Resource.Error -> {
                showSnackbarMessage(result.message as Int)
                }
                is Resource.Success -> {
                    showSnackbarMessage(result.data)
                    checkIfRegistrationAndBusinessCompleted()
                }
            }

        }
    }

    fun checkIfRegistrationAndBusinessCompleted() {
        viewModelScope.launch {
            Timber.e("checkIfRegistrationAndBusinessCompleted")
            when( val isRegistrationResource = signInUseCase
                .isRegistrationAndBusinessCompleted()){
                is Resource.Empty, is Resource.Loading -> {
                    Timber.e("isRegistrationAndBusinessEMPTYOLOADING")
                }
                is Resource.Error -> {
                    showSnackbarMessage(isRegistrationResource.message as Int)
                }

                is Resource.Success -> {
                    isUserRegistration.value = Event(isRegistrationResource.data)
                }
            }
        }
    }





    fun setEmployeeLogin(
        uid: String,
        name: String,
        password: String,
    ) {
        this.uid.value = uid
        this.name.value = name
        this.password.value = password
    }

    fun performEmployeeLogin() {
        val uid = uid.value ?: ""
        viewModelScope.launch {
            isEmployeeLoginSuccess.value =
                signInUseCase.employeeLogin(uid, name.value!!, password.value!!)
        }

    }
}