package com.casecode.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.repository.SignInRepository
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
) : SignInRepository {
    override suspend fun checkRegistration(email: String): Resource<List<String>> {
        return try {
            val signInMethodQueryResult = withContext(Dispatchers.IO) {
                auth.fetchSignInMethodsForEmail(email).await()
            }
            Resource.Success(signInMethodQueryResult.signInMethods!!)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    override suspend fun signInWithCredential(credential: AuthCredential): LiveData<FirebaseAuthResult> {
        val resultLiveData = MutableLiveData<FirebaseAuthResult>()

        try {
            withContext(ioDispatcher) {
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        resultLiveData.value = FirebaseAuthResult.SignInSuccess(user!!)
                    } else {
                        resultLiveData.value = FirebaseAuthResult.SignInFails(task.exception)
                    }
                }
            }
        } catch (e: Exception) {
            resultLiveData.value = FirebaseAuthResult.Failure(e)
        }

        return resultLiveData
    }

    override fun signOut() {
        // Sign out from Firebase Authentication
        auth.signOut()
    }
}
