package com.casecode.data.service

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.casecode.data.mapper.asEntityBusiness
import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.utils.BUSINESS_IS_COMPLETED_STEP_FIELD
import com.casecode.domain.utils.EMPLOYEES_FIELD
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.casecode.pos.data.R
import com.casecode.service.AccountService
import com.casecode.service.AuthService
import com.google.android.gms.common.api.UnsupportedApiCallException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AccountServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val googleIdOption: GetGoogleIdOption,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authService: AuthService,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : AccountService {
    private val credentialManager: CredentialManager = CredentialManager.create(context)
    override suspend fun signIn(): Resource<Int> {
        trace(SIGN_IN) {
            return withContext(ioDispatcher) {
                try {
                    val googleIdToken = retrieveGoogleIdToken()
                    val googleCredentials = buildGoogleAuthCredential(googleIdToken)
                    val authResult = signInWithGoogleCredentials(googleCredentials)

                    if (authResult.user != null) {
                        Resource.success(R.string.sign_in_success)
                    } else {
                        Resource.empty(null, R.string.sign_in_failure)
                    }
                } catch (e: GetCredentialException) {
                    Timber.e(e)
                    Resource.error(R.string.sign_in_exception)
                } catch (e: UnsupportedApiCallException) {
                    Timber.e("UnsupportedApiCallException: $e")
                    Resource.error(R.string.unsupported_api_call)
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    Resource.error(e.message)
                } catch (e: FirebaseAuthException) {
                    Timber.e("Sign-in failed with FirebaseUserException: ${e.message}")
                    Resource.error(R.string.sign_in_api_exception)
                } catch (e: Exception) {
                    Timber.e("Sign-in failed with unexpected exception: ${e.message}")
                    Resource.error(R.string.sign_in_failure)
                }
            }
        }
    }

    private suspend fun retrieveGoogleIdToken(): String {
        val credentialRequest =
            GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        val credential =
            credentialManager.getCredential(request = credentialRequest, context = context)
        val googleIdTokenCredentialRequest =
            GoogleIdTokenCredential.createFrom(credential.credential.data)
        return googleIdTokenCredentialRequest.idToken
    }

    private fun buildGoogleAuthCredential(googleIdToken: String): AuthCredential {
        return GoogleAuthProvider.getCredential(googleIdToken, null)
    }

    private suspend fun signInWithGoogleCredentials(credentials: AuthCredential): AuthResult {
        return firebaseAuth.signInWithCredential(credentials).await()
    }

    override suspend fun isRegistrationAndBusinessCompleted(): Resource<Boolean> {
        trace(IS_REGISTRATION_AND_BUSINESS_COMPLETED) {

            return withContext(ioDispatcher) {
                try {
                    if (firebaseAuth.currentUser == null) {
                        return@withContext Resource.empty(message = "user is null")
                    }
                    if (isFirstTimeSignIn()) {
                        Resource.success(false)
                    }

                    if (isUserCompletedStep()) {
                        Timber.e("complete businesss ,second Time sign in")
                        Resource.success(true)
                    } else {
                        Timber.e("not completd, second Time sign in")

                        Resource.success(false)
                    }

                } catch (e: Exception) {
                    Resource.error(e.message)
                }
            }
        }
    }

    private fun isFirstTimeSignIn(): Boolean {
        // if user empty return false
        val user = firebaseAuth.currentUser ?: return true

        val creationTime = user.metadata?.creationTimestamp
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - creationTime!!

        // Adjust the threshold as needed (e.g., 5 minutes)
        return if (timeDifference < 5 * 60 * 1000) {
            // First-time sign-in
            true
        } else {
            // Existing user
            false
        }
    }

    /**
     * If user complete step business return true, else false
     */
    private suspend fun isUserCompletedStep(): Boolean {
        return withContext(ioDispatcher) {
            try {
                if (!authService.hasUser) {
                    return@withContext false
                }
                //  val id = firebaseAuth.currentUser?.uid ?: return@withContext false
                Timber.e("id = ${authService.currentUserId}")
                val docRef =
                    firestore.collection(USERS_COLLECTION_PATH).document(authService.currentUserId)
                        .get().await()
                if (docRef.exists()) {
                    val data = docRef.get(BUSINESS_IS_COMPLETED_STEP_FIELD)
                    val isCompletedStep = data as Boolean
                    Timber.e("isCompletedStep = $isCompletedStep")
                    (isCompletedStep ?: false)
                } else {
                    false
                }
            } catch (e: Exception) {
                Timber.e(e)
                false
            }
        }
    }

    override suspend fun checkRegistration(email: String): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                // Create a temporary user with a generic password
                firebaseAuth.createUserWithEmailAndPassword(email, "temporary_password")
                // Account creation succeeded, email is available
                Timber.i("checkRegistration: email is created before :true")
                Resource.Success(true)
            } catch (e: FirebaseAuthUserCollisionException) {
                Timber.i("checkRegistration: email is created before :false")
                // Email already exists
                Resource.Success(false) // Assuming password-based sign-in
            } catch (e: Exception) {
                // Other errors
                Resource.Error(e.message)
            }
        }
    }

    override suspend fun employeeLogOut(): Resource<Boolean> {
        TODO("Not yet implemented")
    }


    override suspend fun employeeLogin(
        uid: String,
        employeeId: String,
        password: String,
    ): Resource<Boolean> = withContext(ioDispatcher) {
        try {
            val document = firestore.collection(USERS_COLLECTION_PATH).document(uid).get().await()
            @Suppress("UNCHECKED_CAST")
            val employees = document.get(EMPLOYEES_FIELD) as List<Map<String, Any>>
            val employee = employees.find { it["name"] == employeeId && it["password"] == password }
            Resource.success(employee != null)

        } catch (e: Exception) {
            Timber.e("exception = $e")
            Resource.error(e.message)
        }
    }

    override suspend fun signOut() {
        trace(SIGN_OUT) {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
                firebaseAuth.signOut()
                delay(200L)

                Timber.e("SignOut Done id = ${firebaseAuth.currentUser?.uid}")
            } catch (e: Exception) {
                Timber.e("SignOut exception: $e")
                e.printStackTrace()
                if (e is CancellationException) {
                    Timber.e("SignOut Cancellation: $e")
                    throw e
                }
            }
        }
    }

    companion object {
        private const val SIGN_IN = "SIGN_IN"
        private const val SIGN_OUT = "SIGN_OUT"
        private const val IS_REGISTRATION_AND_BUSINESS_COMPLETED =
            "IS_REGISTRATION_AND_BUSINESS_COMPLETED"
    }

}