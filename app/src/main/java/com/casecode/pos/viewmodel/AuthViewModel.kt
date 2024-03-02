package com.casecode.pos.viewmodel

import android.content.Intent
import android.content.IntentSender
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.data.utils.NetworkMonitor
import com.casecode.domain.usecase.SignInUseCase
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val networkMonitor: NetworkMonitor,
        private val signInUseCase: SignInUseCase,
    ) : ViewModel() {
        private val _isOnline: MutableLiveData<Boolean> = MutableLiveData(false)
        val isOnline get() = _isOnline
        private val _userMessage: MutableLiveData<Event<Int>> = MutableLiveData()
        val currentUserUID = signInUseCase.currentUser().map { it?.uid ?: "" }
        val userMessage get() = _userMessage

        private val _signInIntentSender = MutableLiveData<Resource<IntentSender>?>()
        val signInIntentSender get() = _signInIntentSender
        private val _signInResult = MutableLiveData<FirebaseAuthResult?>()

        var checkRegistration: MutableLiveData<Resource<Boolean>> = MutableLiveData()
            private set
        val signInResult get() = _signInResult

        private val uid: MutableLiveData<String> = MutableLiveData("")
        private val isCompleteScanUid = MutableLiveData<Boolean>()
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

        fun setNetworkMonitor() =
            viewModelScope.launch {
                networkMonitor.isOnline.collect {
                    setConnected(it)
                }
            }

        private fun setConnected(isOnline: Boolean) {
            _isOnline.value = isOnline
        }

        fun signIn() {
            viewModelScope.launch {
                _signInIntentSender.value = signInUseCase.signIn()
            }
        }

        fun checkIfRegistrationAndBusinessCompleted() {
            viewModelScope.launch {
                Timber.e("checkIfRegistrationAndBusinessCompleted")
                checkRegistration.value = signInUseCase.isRegistrationAndBusinessCompleted()
            }
        }

        fun clearCheckRegistration() {
            checkRegistration.value = Resource.loading()
        }

        fun signInWithIntent(intent: Intent) {
            viewModelScope.launch {
                val result = signInUseCase.signInWithIntent(intent)
                onSignInResult(result)
            }
        }

        private fun onSignInResult(signInResult: Flow<FirebaseAuthResult>) {
            viewModelScope.launch {
                signInResult.collect {
                    _signInResult.value = it
                }
            }
        }

        fun clearSignInResult() {
            _signInResult.value = null
        }

        fun processScannedResult(uid: String) {
            if (uid.isNotEmpty()) {
                this.uid.value = uid
                isCompleteScanUid.value = true
            } else {
                isCompleteScanUid.value = false
            }
        }

        fun setEmployeeLogin(
            name: String,
            password: String,
        ) {
            this.name.value = name
            this.password.value = password
        }

        fun performEmployeeLogin() {
            if (isCompleteScanUid.value == true) {
                val uid = uid.value ?: ""
                viewModelScope.launch {
                    isEmployeeLoginSuccess.value =
                        signInUseCase.employeeLogin(uid, name.value!!, password.value!!)
                }
            } else {
                isEmployeeLoginSuccess.value = Resource.empty()
                showSnackbarMessage(R.string.scan_result_empty)
            }
        }
    }