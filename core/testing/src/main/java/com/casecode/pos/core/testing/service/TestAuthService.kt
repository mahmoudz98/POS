package com.casecode.pos.core.testing.service

import com.casecode.pos.core.data.service.AuthService
import com.casecode.pos.core.model.data.LoginStateResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import javax.inject.Inject

class TestAuthService @Inject constructor() : AuthService {
    override val loginData: Flow<LoginStateResult> = flowOf(LoginStateResult.NotSignIn)
    override suspend fun hasUser(): Boolean {
         return true
    }

    override suspend fun currentUserId(): String {
        return "uid"
    }

    override suspend fun currentNameLogin(): String {
        TODO("Not yet implemented")
    }

    override val currentUser: Flow<FirebaseUser?> = flowOf(null)
    override suspend fun hasEmployeeLogin(): Boolean {
        return true
    }
}