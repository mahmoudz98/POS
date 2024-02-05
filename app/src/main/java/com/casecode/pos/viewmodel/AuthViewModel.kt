package com.casecode.pos.viewmodel

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.domain.model.users.Employee
import com.casecode.domain.usecase.SignInUseCase
import com.casecode.domain.usecase.SignOutUseCase
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

//https://firebase.blog/posts/2022/10/using-coroutines-flows-with-firebase-on-android/
@HiltViewModel
class AuthViewModel @Inject constructor(private val signInUseCase: SignInUseCase, private val signOutUseCase: SignOutUseCase) :
   ViewModel()
{
   private val _employeeLoginResult =
      MutableStateFlow<Resource<ArrayList<Employee>>>(Resource.Loading())
   val employeeLoginResult: StateFlow<Resource<ArrayList<Employee>>> get() = _employeeLoginResult
   
   private val _signInResult = MutableLiveData<FirebaseAuthResult>()
   
   var checkRegistration : MutableLiveData<Resource<Boolean>> = MutableLiveData()
   private set
   val signInResult get() = _signInResult
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
   
   fun signInWithIntent(intent: Intent) = signInUseCase.signInWithIntent(intent)
   fun signOut()
   {
      viewModelScope.launch {
         signOutUseCase()
      }
   }
   
   
   fun performEmployeeLogin(uid: String, employeeId: String, password: String)
   {
      viewModelScope.launch {
         try
         {
            signInUseCase.employeeLogin(uid, employeeId, password)
               .collect { employeeData ->
                  _employeeLoginResult.value = employeeData
               }
         } catch (e: Exception)
         {
            Timber.e("Exception during employee login: $e")
            _employeeLoginResult.value = Resource.Error("Failed to log in: ${e.message}")
         }
      }
   }
   
}
