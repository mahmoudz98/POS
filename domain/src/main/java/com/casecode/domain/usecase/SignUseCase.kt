package com.casecode.domain.usecase

import android.content.Intent
import android.content.IntentSender
import com.casecode.domain.utils.Resource
import com.casecode.service.AccountService
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val accountService: AccountService) {

    suspend operator fun invoke() = accountService.signIn()


    suspend fun isRegistrationAndBusinessCompleted(): Resource<Boolean> =
        accountService.isRegistrationAndBusinessCompleted()

    suspend fun employeeLogin(uid: String, employeeId: String, password: String) =
        accountService.employeeLogin(uid, employeeId, password)

}

class SignOutUseCase @Inject constructor(private val accountService: AccountService) {
    suspend operator fun invoke() = accountService.signOut()
}