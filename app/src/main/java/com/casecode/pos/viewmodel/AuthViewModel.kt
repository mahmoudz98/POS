package com.casecode.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.domain.model.users.Employee
import com.casecode.domain.repository.SignInRepository
import com.casecode.domain.utils.Resource
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
//https://firebase.blog/posts/2022/10/using-coroutines-flows-with-firebase-on-android/
@HiltViewModel
class AuthViewModel @Inject constructor(private val signInRepository: SignInRepository) :
    ViewModel() {

    private val _checkTheRegistration =
        MutableStateFlow<Resource<List<String>>>(Resource.loading())
    val checkTheRegistration: StateFlow<Resource<List<String>>> = _checkTheRegistration


    fun checkTheRegistration(email: String) {
        viewModelScope.launch {
            val result = signInRepository.checkRegistration(email)
            _checkTheRegistration.value = result
        }
    }

    suspend fun signInWithCredential(credential: AuthCredential) =
        signInRepository.signInWithCredential(credential)


    fun signOut() {
        signInRepository.signOut()
    }

    private val _employeeLoginResult =
        MutableStateFlow<Resource<ArrayList<Employee>>>(Resource.Loading())
    val employeeLoginResult: StateFlow<Resource<ArrayList<Employee>>> get() = _employeeLoginResult

    fun performEmployeeLogin(uid: String, employeeId: String, password: String) {
        viewModelScope.launch {
            try {
                signInRepository.employeeLogin(uid, employeeId, password)
                    .collect { employeeData ->
                        _employeeLoginResult.value = employeeData
                    }
            } catch (e: Exception) {
                Timber.e("Exception during employee login: $e")
                _employeeLoginResult.value = Resource.Error("Failed to log in: ${e.message}")
            }
        }
    }

}
