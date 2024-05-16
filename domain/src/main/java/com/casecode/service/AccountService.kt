package com.casecode.service

import android.content.IntentSender
import com.casecode.domain.utils.FirebaseAuthResult
import com.casecode.domain.utils.Resource

interface AccountService {
    suspend fun signIn(): Resource<Int>

    suspend fun employeeLogin(uid: String,employeeId: String,password: String): Resource<Boolean>

    suspend fun isRegistrationAndBusinessCompleted(): Resource<Boolean>

    suspend fun checkRegistration(email: String): Resource<Boolean>

    suspend fun employeeLogOut():Resource<Boolean>
    suspend fun signOut()

}