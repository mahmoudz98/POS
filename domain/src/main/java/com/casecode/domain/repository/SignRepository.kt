package com.casecode.domain.repository

import android.content.Intent
import android.content.IntentSender
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface SignRepository
{
   val currentUserId: String
   
   val currentUser : Flow<FirebaseUser?>
   suspend fun signIn(): Resource<IntentSender>
   
   fun signInWithIntent(intent: Intent): Flow<FirebaseAuthResult>
   suspend fun isRegistrationAndBusinessCompleted(): Resource<Boolean>
   
   suspend fun checkRegistration(email: String): Resource<Boolean>
   
   suspend fun signOut()
   
   suspend fun employeeLogin(uid: String,employeeId: String,password: String): Resource<Boolean>
}


