package com.casecode.domain.repository

import com.casecode.domain.model.users.Employee
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface SignInRepository {
    suspend fun checkRegistration(email: String): Resource<List<String>>
    // Issue: Functions returning "Flow" or "Channel" should not be suspending.
    suspend fun signInWithCredential(credential: AuthCredential): Flow<FirebaseAuthResult>
    fun signOut()

     fun employeeLogin(
        uid: String,
        employeeId: String,
        password: String
    ): Flow<Resource<ArrayList<Employee>>>
}
