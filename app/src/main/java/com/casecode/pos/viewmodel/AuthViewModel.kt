package com.casecode.pos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.casecode.pos.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val auth: FirebaseAuth) : ViewModel() {

    private val _signInResult = MutableLiveData<Resource<FirebaseUser>>()
    val signInResult: LiveData<Resource<FirebaseUser>> = _signInResult

    private val _checkTheRegistration = MutableLiveData<Resource<List<String>>>()
    val checkTheRegistration: LiveData<Resource<List<String>>> = _checkTheRegistration

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            val result = signInWithEmailAndPassword(email, password)
            _signInResult.value = result
        }
    }

    private suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        return try {
            val authResult = withContext(Dispatchers.IO) {
                auth.signInWithEmailAndPassword(email, password).await()
            }
            Resource.Success(authResult.user!!)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun checkTheRegistration(email: String) {
        viewModelScope.launch {
            val result = fetchSignInMethodsForEmail(email)
            _checkTheRegistration.value = result
        }
    }

    private suspend fun fetchSignInMethodsForEmail(email: String): Resource<List<String>> {
        return try {
            val signInMethodQueryResult = withContext(Dispatchers.IO) {
                auth.fetchSignInMethodsForEmail(email).await()
            }
            Resource.Success(signInMethodQueryResult.signInMethods!!)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    suspend fun signInWithCredential(credential: AuthCredential): LiveData<FirebaseAuthResult> {
        val resultLiveData = MutableLiveData<FirebaseAuthResult>()

        try {
            withContext(Dispatchers.IO) {
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        resultLiveData.value = FirebaseAuthResult.SignInSuccess(user!!)
                    } else {
                        // If sign in fails, display a message to the user.
                        resultLiveData.value = FirebaseAuthResult.SignInFails(task.exception)
                    }
                }
            }
        } catch (e: Exception) {
            resultLiveData.value = FirebaseAuthResult.Failure(e)
        }

        return resultLiveData
    }


    fun signOut() {
        auth.signOut()
    }
}
