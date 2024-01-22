package com.casecode.data.repository

import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.repository.SignInRepository
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : SignInRepository {
    override suspend fun checkRegistration(email: String): Resource<List<String>> {
        return try {
            val signInMethodQueryResult = withContext(ioDispatcher) {
                auth.fetchSignInMethodsForEmail(email).await()
            }
            Resource.Success(signInMethodQueryResult.signInMethods!!)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun signInWithCredential(credential: AuthCredential): Flow<FirebaseAuthResult> =
        callbackFlow {
            try {
                withContext(ioDispatcher) {
                    auth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            trySend(FirebaseAuthResult.SignInSuccess(user!!)).isSuccess
                        } else {
                            trySend(FirebaseAuthResult.SignInFails(task.exception)).isSuccess
                        }
                    }
                }
            } catch (e: Exception) {
                trySend(FirebaseAuthResult.Failure(e)).isSuccess
            }

            awaitClose { /* Perform cleanup or cancellation logic if needed */ }
        }

    override fun signOut() {
        // Sign out from Firebase Authentication
        auth.signOut()
    }
}
