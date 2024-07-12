package com.casecode.pos.core.testing.service

import com.casecode.pos.core.data.service.AccountService
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.testing.R
import javax.inject.Inject

class TestAccountService @Inject constructor() : AccountService {
    var signInResult: Resource<Int> = Resource.Success(com.casecode.pos.core.data.R.string.sign_in_success)
    var employeeLoginResult: Resource<Boolean> = Resource.Success(true)

    override suspend fun signIn(): Resource<Int> {
        return signInResult
    }

    override suspend fun employeeLogin(
        uid: String,
        employeeId: String,
        password: String,
    ): Resource<Boolean> {
        return employeeLoginResult
    }

    override suspend fun checkUserLogin() {}

    override suspend fun checkRegistration(email: String): Resource<Boolean> {
        return Resource.Success(true)
    }

    override suspend fun employeeLogOut() {

    }

    override suspend fun signOut() {

    }
}