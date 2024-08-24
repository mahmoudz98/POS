package com.casecode.pos.core.testing.service

import com.casecode.pos.core.data.service.AccountService
import com.casecode.pos.core.domain.utils.Resource
import javax.inject.Inject

class TestAccountService
    @Inject
    constructor() : AccountService {
        var signInResult: Resource<Int> =
            Resource.Success(com.casecode.pos.core.data.R.string.core_data_sign_in_success)
        var employeeLoginResult: Resource<Boolean> = Resource.Success(true)

        override suspend fun signIn(activityContext: android.content.Context): Resource<Int> = signInResult

    override suspend fun employeeLogin(
        uid: String,
        employeeId: String,
        password: String,
    ): Resource<Boolean> = employeeLoginResult

    override suspend fun checkUserLogin() {}

    override suspend fun checkRegistration(email: String): Resource<Boolean> =
        Resource.Success(true)

    override suspend fun employeeLogOut() {
    }

    override suspend fun signOut() {
    }
}