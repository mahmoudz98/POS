package com.casecode.data.repository

import android.content.Intent
import android.content.IntentSender
import com.casecode.data.mapper.fromBusinessResponse
import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.repository.SignRepository
import com.casecode.domain.utils.EMPLOYEES_FIELD
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.casecode.pos.data.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SignRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val beginSignInRequest: BeginSignInRequest,
    private val oneTapClient: SignInClient,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : SignRepository {
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()
    override val currentUser: Flow<FirebaseUser?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                this.trySend(auth.currentUser)
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }.flowOn(ioDispatcher)

    override suspend fun signIn(): Resource<IntentSender> {
        return withContext(ioDispatcher) {
            try {
                suspendCoroutine { continuation ->
                    oneTapClient.beginSignIn(beginSignInRequest)
                        .addOnSuccessListener { beginSignInRequest ->
                            continuation.resume(
                                Resource.success(beginSignInRequest.pendingIntent.intentSender)
                            )
                        }.addOnFailureListener {
                            continuation.resume(Resource.error(R.string.sign_in_failure))
                        }
                }
            } catch (e: ApiException) {
                when (e.statusCode) {
                    CommonStatusCodes.CANCELED -> {
                        Resource.error(R.string.sign_in_cancel)
                    }

                    CommonStatusCodes.NETWORK_ERROR -> {
                        Resource.error(R.string.sign_in_network_error)
                    }

                    else -> {
                        Resource.error(R.string.sign_in_api_exception)
                    }
                }
            } catch (e: Exception) {
                // TODO: add firebase crach to track error in signIn.
                e.printStackTrace()
                Resource.error(R.string.sign_in_exception)
            }
        }

    }

    override fun signInWithIntent(intent: Intent): Flow<FirebaseAuthResult> {
        return callbackFlow {

            val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                signInListenerWithIntent(intent, firebaseAuth)
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }

        }.flowOn(ioDispatcher)
    }

    private fun ProducerScope<FirebaseAuthResult>.signInListenerWithIntent(
        intent: Intent, firebaseAuth: FirebaseAuth
    ) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken
            val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

            firebaseAuth.signInWithCredential(googleCredentials).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        channel.trySend(FirebaseAuthResult.SignInSuccess(user)).isSuccess
                        Timber.d("Sign-in successful: $user")
                    } else {
                        channel.trySend(FirebaseAuthResult.SignInFails(null))
                        Timber.w("User not found after sign-in")
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthException) {
                        channel.trySend(FirebaseAuthResult.SignInFails(exception))
                        Timber.e("Sign-in failed with FirebaseUserException: ${exception.message}")
                    } else {
                        channel.trySend(FirebaseAuthResult.SignInFails(exception))
                        Timber.e(
                            "Sign-in failed with unexpected exception: ${
                                exception?.message
                            }"
                        )
                    }
                }
            }
        } catch (e: Exception) {
            channel.trySend(FirebaseAuthResult.Failure(e))
            Timber.e("Error during sign-in: ${e.message}")
        }
    }

    override suspend fun isRegistrationAndBusinessCompleted(): Resource<Boolean> {

        return withContext(ioDispatcher) {
            try {
                if (isFirstTimeSignIn()) {
                    Resource.success(false)

                } else {
                    if (!isUserCompletedStep()) {
                        Timber.e("first Time sign in")
                        Resource.success(false)
                    } else {
                        Timber.e("second Time sign in")

                        Resource.success(true)
                    }
                }

            } catch (e: Exception) {
                Resource.error(e.message)
            }
        }
    }

    private fun isFirstTimeSignIn(): Boolean {
        // if user empty return false
        val user = auth.currentUser ?: return true


        val creationTime = user.metadata?.creationTimestamp
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - creationTime!!

        // Adjust the threshold as needed (e.g., 2 minutes)
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
                val id = auth.currentUser?.uid ?: return@withContext false
                Timber.e("id = $id")
                val docRef = firestore.collection(USERS_COLLECTION_PATH).document(id).get().await()
                if (docRef.exists()) {
                    val data = docRef.data!!
                    val businessResponse = data as Map<String, Any>
                    val business = businessResponse.fromBusinessResponse()
                    Timber.e("isUserCompletedStep = $business")
                    (business.isCompletedStep ?: false)

                } else false


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
                auth.createUserWithEmailAndPassword(email, "temporary_password")
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


    override suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()

            delay(200L)

            Timber.e("SignOut Done")
            Timber.e("SignOut Done id = ${auth.currentUser?.uid}")
        } catch (e: Exception) {
            Timber.e("SignOut exception: $e")
            e.printStackTrace()
            if (e is CancellationException) {
                Timber.e("SignOut Cancellation: $e")

                throw e
            }
        }
    }

    override suspend fun employeeLogin(
        uid: String, employeeId: String, password: String
    ): Resource<Boolean> = withContext(ioDispatcher) {
        try {
            val document = firestore.collection(USERS_COLLECTION_PATH).document(uid).get().await()
            val employees = document.get(EMPLOYEES_FIELD) as List<Map<String, Any>>
            val employee = employees.find { it["name"] == employeeId && it["password"] == password }
            Resource.success(employee != null)

        } catch (e: Exception) {
            Timber.e("exception = $e")
            Resource.error(e.message)

        }

    }


}
