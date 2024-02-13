package com.casecode.testing.repository

import android.content.Intent
import android.content.IntentSender
import com.casecode.domain.repository.SignRepository
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.casecode.testing.util.MainDispatcherRule
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.auth.FirebaseUser
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject

/**
 * Author: Mahmoud Abdalhafeez
 * Created: 1/5/2024
 * Description:
 */
class TestSignRepository @Inject constructor() : SignRepository
{
   @get:Rule
   val mainDispatcherRule = MainDispatcherRule()
   
   private var shouldReturnError = false
   private var shouldReturnEmpty = false
   
   @Before
   fun setup()
   {
      shouldReturnError = false
      shouldReturnEmpty = false
   }
   
   override val currentUserId: String = "test"
   
   override val currentUser: Flow<FirebaseUser?> = flowOf()
   override suspend fun signIn(): Resource<IntentSender>
   {
      if (shouldReturnError)
      {
         return Resource.error(com.casecode.pos.data.R.string.sign_in_failure)
      }
      
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