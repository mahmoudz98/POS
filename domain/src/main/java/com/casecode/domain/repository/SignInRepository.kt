package com.casecode.domain.repository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Singleton

@Singleton
interface SignInRepository {
    suspend fun checkRegistration(email: String): Resource<List<String>>
    suspend fun signInWithCredential(credential: AuthCredential): Flow<FirebaseAuthResult>
    fun signOut()
}
