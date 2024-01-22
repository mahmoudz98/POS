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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
// Issue: visit website how to use firebase with coroutines. and firebase team built app with coroutines.
//https://firebase.blog/posts/2022/10/using-coroutines-flows-with-firebase-on-android/
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
    
    override fun employeeLogin(
         uid: String,
         employeeId: String,
         password: String): Flow<Resource<ArrayList<Employee>>> = callbackFlow {
        
            val employeeDocumentSnapshot =
                firestore.collection(USERS_COLLECTION_PATH).document(uid).get()
            
            employeeDocumentSnapshot
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val employeeDataMap = documentSnapshot.data
                        val employeeList = employeeDataMap?.values?.mapNotNull { it as? Employee }
                        val arrayList = ArrayList(employeeList)
                        trySend(Resource.Success(arrayList))
                        Timber.i("Employee data fetched successfully. UID: $uid")
                    } else {
                        trySend(Resource.Error("Employee document does not exist. UID: $uid"))
                        Timber.e("Employee document does not exist. UID: $uid")
                    }
                }
                .addOnFailureListener { exception ->
                    trySend(Resource.Error("Failed to fetch employee data"))
                    Timber.e("Failed to fetch employee data. UID: $uid, Error: $exception")
                }
            
        
    }.flowOn(ioDispatcher)
    
    
}
