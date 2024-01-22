package com.casecode.pos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.domain.repository.SignInRepository
import com.casecode.domain.utils.Resource
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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

}
