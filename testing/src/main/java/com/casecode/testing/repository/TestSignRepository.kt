package com.casecode.testing.repository

import android.content.Intent
import android.content.IntentSender
import com.casecode.domain.model.users.Employee
import com.casecode.domain.repository.SignRepository
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Author: Mahmoud Abdalhafeez
 * Created: 1/5/2024
 * Description:
 */
class TestSignRepository @Inject constructor()  : SignRepository
{
   override suspend fun signIn(): IntentSender?
   {
      TODO("Not yet implemented")
   }
   
   override fun signInWithIntent(intent: Intent): Flow<FirebaseAuthResult>
   {
      TODO("Not yet implemented")
   }
   
   override suspend fun isRegistrationAndBusinessCompleted(): Resource<Boolean>
   {
      TODO("Not yet implemented")
   }
   
   override suspend fun checkRegistration(email: String): Resource<Boolean>
   {
      TODO("Not yet implemented")
   }
   
   override suspend fun signOut()
   {
      TODO("Not yet implemented")
   }
   
   override suspend fun employeeLogin(
        uid: String,
        employeeId: String,
        password: String,
                                     ): Resource<Boolean>
   {
      TODO("Not yet implemented")
   }
}