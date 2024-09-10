/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.data.repository

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.casecode.pos.core.common.AppDispatchers
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.R
import com.casecode.pos.core.data.model.asExternalModel
import com.casecode.pos.core.data.utils.BUSINESS_FIELD
import com.casecode.pos.core.data.utils.BUSINESS_IS_COMPLETED_STEP_FIELD
import com.casecode.pos.core.data.utils.EMPLOYEES_FIELD
import com.casecode.pos.core.data.utils.EMPLOYEE_NAME_FIELD
import com.casecode.pos.core.data.utils.EMPLOYEE_PASSWORD_FIELD
import com.casecode.pos.core.data.utils.USERS_COLLECTION_PATH
import com.casecode.pos.core.datastore.PosPreferencesDataSource
import com.casecode.pos.core.domain.repository.AccountRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.firebase.services.trace
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AccountRepositoryImpl
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val posPreferencesDataSource: PosPreferencesDataSource,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : AccountRepository {
    private val credentialManager: CredentialManager = CredentialManager.create(context)

    override suspend fun signIn(idToken: suspend () -> String): Resource<Int> {
        trace(SIGN_IN) {
            return withContext(ioDispatcher) {
                try {
                    val googleIdToken = idToken()
                    val googleCredentials = buildGoogleAuthCredential(googleIdToken)
                    val authResult = signInWithGoogleCredentials(googleCredentials)

                    if (authResult.user != null) {
                        Resource.success(R.string.core_data_sign_in_success)
                    } else {
                        Resource.empty(R.string.core_data_sign_in_failure)
                    }
                } catch (e: Exception) {
                    handleSignInException(e)
                }
            }
        }
    }

    private fun handleSignInException(e: Exception): Resource<Int> = when (e) {
        is androidx.credentials.exceptions.GetCredentialCancellationException -> {
            Timber.e(e)
            Resource.error(R.string.core_data_sign_in_cancel)
        }

        is androidx.credentials.exceptions.GetCredentialException -> {
            Timber.e(e)
            Resource.error(com.casecode.pos.core.data.R.string.core_data_sign_in_exception)
        }

        is com.google.android.gms.common.api.UnsupportedApiCallException -> {
            Timber.e("UnsupportedApiCallException: $e")
            Resource.error(com.casecode.pos.core.data.R.string.core_data_unsupported_api_call)
        }

        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> {
            Resource.error(e.message)
        }

        is com.google.firebase.auth.FirebaseAuthException -> {
            Timber.e("Sign-in failed with FirebaseUserException: ${e.message}")
            Resource.error(com.casecode.pos.core.data.R.string.core_data_sign_in_api_exception)
        }

        else -> {
            Timber.e("Sign-in failed with unexpected exception: ${e.message}")
            Resource.error(com.casecode.pos.core.data.R.string.core_data_sign_in_failure)
        }
    }


    private fun buildGoogleAuthCredential(googleIdToken: String): AuthCredential =
        GoogleAuthProvider.getCredential(googleIdToken, null)

    private suspend fun signInWithGoogleCredentials(credentials: AuthCredential): AuthResult =
        firebaseAuth.signInWithCredential(credentials).await()

    override suspend fun checkUserLogin() {
        trace(IS_REGISTRATION_AND_BUSINESS_COMPLETED) {
            withContext(ioDispatcher) {
                val currentUser = firebaseAuth.currentUser ?: return@withContext
                val isAdmin = isUserCompleteStep(currentUser.uid)

                posPreferencesDataSource.setLoginWithAdmin(currentUser.uid, isAdmin)
            }
        }
    }

    /**
     * If user complete step business return true,
     * else false
     */
    private suspend fun isUserCompleteStep(currentUid: String): Boolean =
        withContext(ioDispatcher) {
            try {
                //  val id = firebaseAuth.currentUser?.uid ?: return@withContext false
                Timber.e("id = $currentUid")
                val docRef =
                    firestore
                        .collection(USERS_COLLECTION_PATH)
                        .document(currentUid)
                        .get()
                        .await()
                if (docRef.exists()) {
                    val data =
                        docRef.get("${BUSINESS_FIELD}.${BUSINESS_IS_COMPLETED_STEP_FIELD}")
                    val isCompletedStep = data as? Boolean == true
                    isCompletedStep
                } else {
                    false
                }
            } catch (e: Exception) {
                Timber.e(e)
                false
            }
        }

    override suspend fun checkRegistration(email: String): Resource<Boolean> =
        withContext(ioDispatcher) {
            try {
                // Create a temporary user with a generic password
                firebaseAuth.createUserWithEmailAndPassword(email, "temporary_password")
                // Account creation succeeded, email is available
                Timber.i("checkRegistration: email is created before :true")
                Resource.Success(true)
            } catch (_: FirebaseAuthUserCollisionException) {
                Timber.i("checkRegistration: email is created before :false")
                // Email already exists
                Resource.Success(false) // Assuming password-based sign-in
            } catch (e: Exception) {
                // Other errors
                Resource.Error(e.message)
            }
        }

    override suspend fun employeeLogOut() {
        posPreferencesDataSource.restLogin()
    }

    override suspend fun employeeLogin(
        uid: String,
        employeeId: String,
        password: String,
    ): Resource<Boolean> =
        withContext(ioDispatcher) {
            try {
                val document =
                    firestore
                        .collection(USERS_COLLECTION_PATH)
                        .document(uid)
                        .get()
                        .await()

                @Suppress("UNCHECKED_CAST")
                val employees =
                    document.get(EMPLOYEES_FIELD) as List<Map<String, Any>>?
                val employee =
                    employees?.find { it[EMPLOYEE_NAME_FIELD] == employeeId && it[EMPLOYEE_PASSWORD_FIELD] == password }
                if (employee != null) {
                    Timber.e("employee: $employee")
                    posPreferencesDataSource.setLoginByEmployee(employee.asExternalModel(), uid)
                    Resource.success(true)
                } else {
                    Resource.success(false)
                }
            } catch (e: FirebaseException) {
                Timber.e("ex: $e")
                Resource.error(e.message)
            } catch (e: FirebaseFirestoreException) {
                Timber.e("ex: $e")
                Resource.error(e.message)
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
                posPreferencesDataSource.restLogin()
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