package com.casecode.data.repository

import android.content.Intent
import android.content.IntentSender
import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.repository.SignRepository
import com.casecode.domain.utils.BUSINESS_FIELD
import com.casecode.domain.utils.BUSINESS_IS_COMPLETED_STEP_FIELD
import com.casecode.domain.utils.EMPLOYEES_FIELD
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class SignRepositoryImpl @Inject constructor(
     private val auth: FirebaseAuth,
     private val firestore: FirebaseFirestore,
     private val beginSignInRequest: BeginSignInRequest,
     private val oneTapClient: SignInClient,
     
     @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
                                            ) : SignRepository
{
   
   override suspend fun signIn(): IntentSender?
   {
      
      val result = try
      {
         oneTapClient.beginSignIn(beginSignInRequest).await()
      } catch (e: Exception)
      {
         e.printStackTrace()
         if (e is CancellationException) throw e
         null
      }
      return result?.pendingIntent?.intentSender
   }
   
   
   override fun signInWithIntent(intent: Intent): Flow<FirebaseAuthResult>
   {
      val credential = oneTapClient.getSignInCredentialFromIntent(intent)
      val googleIdToken = credential.googleIdToken
      val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken,null)
      
      return callbackFlow {
         try
         {
            val callback = auth.signInWithCredential(googleCredentials).addOnCompleteListener { task->
               if (task.isSuccessful)
               {
                  val user = auth.currentUser
                  if (user != null)
                  {
                     
                     trySend(FirebaseAuthResult.SignInSuccess(user)).isClosed
                     Timber.e("Success sign: $user")
                  } else
                  {
                     trySend(FirebaseAuthResult.SignInFails(null))
                  }
               } else
               {
                  Timber.e("exception sign: ${task.exception}")
                  trySend(FirebaseAuthResult.SignInFails(task.exception)).isSuccess
                  
               }
            }
            awaitClose { callback.isComplete }
            
         } catch (e: Exception)
         {
            Timber.e("exception sign: $e")
            trySend(FirebaseAuthResult.Failure(e))
         }
         
      }.flowOn(ioDispatcher)
   }
   
   override suspend fun isRegistrationAndBusinessCompleted(): Resource<Boolean>
   {
      return withContext(ioDispatcher) {
         try
         {
            if (isFirstTimeSignIn())
            {
               Resource.success(false)
               
            } else
            {
               if (! isUserCompletedStep())
               {
                  Timber.e("first Time sign in")
                  Resource.success(false)
               } else
               {
                  Timber.e("second Time sign in")
                  
                  Resource.success(true)
               }
            }
            
         } catch (e: Exception)
         {
            Resource.error(e.message)
         }
      }
   }
   
   private fun isFirstTimeSignIn(): Boolean
   {
      // if user empty return false
      val user = auth.currentUser ?: return true
      
      
      val creationTime = user.metadata?.creationTimestamp
      val currentTime = System.currentTimeMillis()
      val timeDifference = currentTime - creationTime !!
      
      // Adjust the threshold as needed (e.g., 5 minutes)
      return if (timeDifference < 5 * 60 * 1000)
      {
         // First-time sign-in
         true
      } else
      {
         // Existing user
         false
      }
      
   }
   
   /**
    * If user complete step business return true, else false
    */
   private suspend fun isUserCompletedStep(): Boolean
   {
      
      val id = auth.currentUser?.uid ?: return false
      val docRef = firestore.collection(USERS_COLLECTION_PATH).document(id).collection(BUSINESS_FIELD).whereEqualTo(BUSINESS_IS_COMPLETED_STEP_FIELD,true)
      
      return try
      {
         val querySnapshot = docRef.get().await()
         querySnapshot.documents.isNotEmpty()
      } catch (e: Exception)
      {
         false
      }
      
   }
   
   override suspend fun checkRegistration(email: String): Resource<Boolean>
   {
      return withContext(ioDispatcher) {
         try
         {
            
            // Create a temporary user with a generic password
            auth.createUserWithEmailAndPassword(email,"temporary_password")
            // Account creation succeeded, email is available
            Timber.i("checkRegistration: email is created before :true")
            Resource.Success(true)
            
         } catch (e: FirebaseAuthUserCollisionException)
         {
            Timber.i("checkRegistration: email is created before :false")
            // Email already exists
            Resource.Success(false) // Assuming password-based sign-in
         } catch (e: Exception)
         {
            // Other errors
            Resource.Error(e.message)
         }
      }
   }
   
   
   override suspend fun signOut()
   {
      try
      {
         oneTapClient.signOut().await()
         auth.signOut()
      } catch (e: Exception)
      {
         e.printStackTrace()
         if (e is CancellationException) throw e
      }
   }
   
   override suspend fun employeeLogin(uid: String,employeeId: String,password: String): Resource<Boolean> = withContext(ioDispatcher) {
      try
      {
         val document = firestore.collection(USERS_COLLECTION_PATH).document(uid).get().await()
         val employees = document.get(EMPLOYEES_FIELD) as List<Map<String,Any>>
         val employee = employees.find { it["name"] == employeeId && it["password"] == password }
         Resource.success(employee != null)
         
      } catch (e: Exception)
      {
         Timber.e("exception = $e")
         Resource.error(e.message)
         
      }
      
   }
   
   
}
