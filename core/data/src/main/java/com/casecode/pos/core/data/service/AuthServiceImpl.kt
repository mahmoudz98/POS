package com.casecode.pos.core.data.service

import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.datastore.PosPreferencesDataSource
import com.casecode.pos.core.model.data.LoginStateResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val posPreferencesDataSource: PosPreferencesDataSource,
    @Dispatcher(IO) private val io: CoroutineDispatcher,
) : AuthService {
    override val loginData: Flow<LoginStateResult> = posPreferencesDataSource.loginData.map {
        LoginStateResult.Loading
        delay(300L)
        it
    }

    override suspend fun currentUserId(): String {
        return withContext(io) {
            async {
               posPreferencesDataSource.currentUid.first()?:""
            }.await()
        }
    }
    override suspend fun currentNameLogin(): String {
        return withContext(io){
            async {
                posPreferencesDataSource.currentNameLogin.first()?:""
            }.await()
        }
    }

    override val currentUser: Flow<FirebaseUser?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth -> this.trySend(auth.currentUser) }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    override suspend fun hasEmployeeLogin(): Boolean {
        return loginData.last() is LoginStateResult.EmployeeLogin
    }

    override suspend fun hasUser(): Boolean {
      return currentUserId().isNotBlank()
    }
}