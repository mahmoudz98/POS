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
package com.casecode.pos.core.data.repository.old

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.casecode.pos.core.common.AppDispatchers
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.R
import com.casecode.pos.core.datastore.PosPreferencesDataSource
import com.casecode.pos.core.domain.repository.old.AccountRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.domain.utils.SignInGoogleState
import com.casecode.pos.core.firebase.BUSINESS_FIELD
import com.casecode.pos.core.firebase.BUSINESS_IS_COMPLETED_STEP_FIELD
import com.casecode.pos.core.firebase.EMPLOYEES_FIELD
import com.casecode.pos.core.firebase.EMPLOYEE_NAME_FIELD
import com.casecode.pos.core.firebase.EMPLOYEE_PASSWORD_FIELD
import com.casecode.pos.core.firebase.FirestoreService
import com.casecode.pos.core.firebase.USERS_COLLECTION_PATH
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.UnsupportedApiCallException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AccountRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val firebaseAuth: FirebaseAuth,
        private val db: FirestoreService,
        private val posPreferencesDataSource: PosPreferencesDataSource,
        private val logService: LogService,
        @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    ) : AccountRepository {
        private val credentialManager: CredentialManager = CredentialManager.create(context)

        override suspend fun signIn(idToken: suspend () -> String): SignInGoogleState =
            withContext(ioDispatcher) {
                try {
                    val googleIdToken = idToken()
                    val googleCredentials = buildGoogleAuthCredential(googleIdToken)
                    val authResult = signInWithGoogleCredentials(googleCredentials)

                    if (authResult.user != null) {
                        SignInGoogleState.Success
                    } else {
                        SignInGoogleState.Error(R.string.core_data_sign_in_failure)
                    }
                } catch (e: Exception) {
                    handleSignInException(e)
                }
            }

        private fun handleSignInException(e: Exception): SignInGoogleState =
            when (e) {
                is GetCredentialCancellationException -> {
                    logService.logNonFatalCrash(e)
                    SignInGoogleState.Cancelled
                }

                is GetCredentialException -> {
                    logService.logNonFatalCrash(e)
                    SignInGoogleState.Error(R.string.core_data_sign_in_exception)
                }

                is UnsupportedApiCallException -> {
                    logService.logNonFatalCrash(e)
                    SignInGoogleState.Error(R.string.core_data_unsupported_api_call)
                }

                is FirebaseAuthInvalidCredentialsException -> {
                    SignInGoogleState.Error(R.string.core_data_sign_in_api_exception)
                }

                is FirebaseAuthException -> {
                    logService.log(e.message ?: "")
                    SignInGoogleState.Error(R.string.core_data_sign_in_api_exception)
                }

                else -> {
                    logService.log(e.message ?: "")
                    SignInGoogleState.Error(R.string.core_data_sign_in_failure)
                }
            }

        override fun isGooglePlayServicesAvailable(): Boolean {
            val apiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)
            return resultCode == ConnectionResult.SUCCESS
        }

        private fun buildGoogleAuthCredential(googleIdToken: String): AuthCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

        private suspend fun signInWithGoogleCredentials(credentials: AuthCredential): AuthResult =
            firebaseAuth.signInWithCredential(
                credentials,
            ).await()

        override suspend fun checkUserLogin(): Boolean {
            return withContext(ioDispatcher) {
                val currentUser = firebaseAuth.currentUser ?: return@withContext false
                val isAdmin = isUserCompleteStep(currentUser.uid)
                // posPreferencesDataSource.setLoginWithAdmin(currentUser.uid, isAdmin)
                !isAdmin
            }
        }

        /**
         * If user complete step business return true,
         * else false
         */
        private suspend fun isUserCompleteStep(currentUid: String): Boolean =
            withContext(ioDispatcher) {
                try {
                    val docRef = db.getDocument(USERS_COLLECTION_PATH, currentUid)

                    if (docRef.exists()) {
                        val data =
                            docRef.get("${BUSINESS_FIELD}.${BUSINESS_IS_COMPLETED_STEP_FIELD}")
                        val isCompletedStep = data as? Boolean == true
                        isCompletedStep
                    } else {
                        false
                    }
                } catch (e: Exception) {
                    logService.logNonFatalCrash(e)
                    false
                }
            }

        override suspend fun checkRegistration(email: String): Resource<Boolean> =
            withContext(ioDispatcher) {
                try {
                    // Create a temporary user with a generic password
                    firebaseAuth.createUserWithEmailAndPassword(email, "temporary_password")
                    Resource.Success(true)
                } catch (_: FirebaseAuthUserCollisionException) {
                    logService.log("checkRegistration: email is created before :false")
                    // Email already exists
                    Resource.Success(false) // Assuming password-based sign-in
                } catch (e: Exception) {
                    logService.logNonFatalCrash(e)
                    // Other errors
                    Resource.Error(e.message)
                }
            }

        override suspend fun employeeLogOut() {
            // posPreferencesDataSource.restLogin()
        }

        override suspend fun employeeLogin(
            uid: String,
            employeeId: String,
            password: String,
        ): Resource<Boolean> =
            withContext(ioDispatcher) {
                try {
                    val document = db.getDocument(USERS_COLLECTION_PATH, uid)

                    @Suppress("UNCHECKED_CAST")
                    val employees =
                        document.get(EMPLOYEES_FIELD) as List<Map<String, Any>>?
                    val employee =
                        employees?.find {
                            it[EMPLOYEE_NAME_FIELD] == employeeId &&
                                it[EMPLOYEE_PASSWORD_FIELD] == password
                        }
                    if (employee != null) {
                        // posPreferencesDataSource.setLoginByEmployee(employee.asExternalModel(), uid)
                        Resource.success(true)
                    } else {
                        Resource.success(false)
                    }
                } catch (e: FirebaseException) {
                    logService.logNonFatalCrash(e)
                    Resource.error(e.message)
                } catch (e: FirebaseFirestoreException) {
                    logService.logNonFatalCrash(e)
                    Resource.error(e.message)
                } catch (e: Exception) {
                    logService.logNonFatalCrash(e)
                    Resource.error(e.message)
                }
            }

        override suspend fun signOut() {
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
                firebaseAuth.signOut()
                //  posPreferencesDataSource.restLogin()
            } catch (e: Exception) {
                logService.logNonFatalCrash(e)
                e.printStackTrace()
                if (e is CancellationException) {
                    logService.logNonFatalCrash(e)
                    throw e
                }
            }
        }
    }
