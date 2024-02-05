package com.casecode.domain.repository

import android.content.Intent
import android.content.IntentSender
import com.casecode.domain.model.users.Employee
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SignRepository {
   suspend fun signIn(): IntentSender?
   
   fun signInWithIntent(intent: Intent): Flow<FirebaseAuthResult>
   suspend fun isRegistrationAndBusinessCompleted(): Resource<Boolean>
   
   suspend fun checkRegistration(email: String): Resource<Boolean>
   
   suspend fun signOut()

     fun employeeLogin(
        uid: String,
        employeeId: String,
        password: String
    ): Flow<Resource<ArrayList<Employee>>>
}
