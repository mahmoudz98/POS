package com.casecode.data.repository

import com.casecode.data.utils.AppDispatchers.IO
import com.casecode.data.utils.Dispatcher
import com.casecode.domain.model.users.Employee
import com.casecode.domain.repository.SignInRepository
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.casecode.domain.utils.USERS_COLLECTION_PATH
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
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

    override suspend fun employeeLogin(
        uid: String,
        employeeId: String,
        password: String
    ): Flow<Resource<ArrayList<Employee>>> = flow {
        try {
            val employeeDocumentSnapshot =
                firestore.collection(USERS_COLLECTION_PATH).document(uid).get().await()

            if (employeeDocumentSnapshot.exists()) {
                val employeeDataMap = employeeDocumentSnapshot.data
                Timber.i("employeeDataMap: $employeeDataMap" )
                if (employeeDataMap != null) {
                    val employeeList = employeeDataMap.values.map { it as Employee }
                    val arrayList = ArrayList(employeeList)
                    emit(Resource.Success(arrayList))
                } else {
                    emit(Resource.Error("Failed to parse employee data"))
                }
            } else {
                emit(Resource.Error("Employee data not found"))
            }
        } catch (e: Exception) {
            Timber.e("Exception while getting employee data: $e")
            emit(Resource.Error(e.message ?: "Failed to get employee data"))
        }
    }

}
