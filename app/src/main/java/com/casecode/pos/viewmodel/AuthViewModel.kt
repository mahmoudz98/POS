package com.casecode.pos.viewmodel

import android.content.Intent
import android.content.IntentSender
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.domain.usecase.SignInUseCase
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.casecode.pos.R
import com.casecode.pos.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
     private val signInUseCase: SignInUseCase,
                                       ) : ViewModel()
{
   private val _userMessage: MutableLiveData<Event<Int>> = MutableLiveData()
   val userMessage get() = _userMessage
   
   
   private val _signInResult = MutableLiveData<FirebaseAuthResult>()
   
   var checkRegistration: MutableLiveData<Resource<Boolean>> = MutableLiveData()
      private set
   val signInResult get() = _signInResult
   
   private val _uid: MutableLiveData<String> = MutableLiveData("")
   private val isCompleteScanUid = MutableLiveData<Boolean>()
   private val _name: MutableLiveData<String> = MutableLiveData()
   private val _password: MutableLiveData<String> = MutableLiveData()
   
   private val _isEmployeeLoginSuccess = MutableLiveData<Resource<Boolean>>()
   val isEmployeeLoginSuccess get() = _isEmployeeLoginSuccess
   
   private fun showSnackbarMessage(@StringRes message: Int)
   {
      Timber.e("message: $message")
      _userMessage.value = Event(message)
   }
   
   suspend fun signIn(): IntentSender?
   {
      
      return signInUseCase.signIn()
      
   }
   
   fun checkIfRegistrationAndBusinessCompleted()
   {
      viewModelScope.launch {
         checkRegistration.value = signInUseCase.isRegistrationAndBusinessCompleted()
      }
   }
   
   fun onSignInResult(signInResult: Flow<FirebaseAuthResult>)
   {
      viewModelScope.launch {
         signInResult.collect {
            _signInResult.value = it
         }
      }
   }
   
   fun signInWithIntent(intent: Intent)
   {
      viewModelScope.launch {
         val result = signInUseCase.signInWithIntent(intent)
         onSignInResult(result)
      }
   }
   
   
   fun processScannedResult(uid: String)
   {
      if (uid.isNotEmpty())
      {
         _uid.value = uid
         isCompleteScanUid.value = true
      } else
      {
         isCompleteScanUid.value = false
      }
   }
   
   fun setEmployeeLogin(name: String,password: String)
   {
      _name.value = name
      _password.value = password
   }
   
   fun performEmployeeLogin()
   {
      if (isCompleteScanUid.value == true)
      {
         val uid = _uid.value ?: ""
         viewModelScope.launch {
            isEmployeeLoginSuccess.value = signInUseCase.employeeLogin(uid,_name.value !!,_password.value !!)
         }
      } else
      {
         isEmployeeLoginSuccess.value = Resource.empty()
         showSnackbarMessage(R.string.scan_result_empty)
         
      }
   }
   
}
