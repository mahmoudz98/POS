package com.casecode.pos.core.data.service

import android.content.Context
import com.casecode.pos.core.domain.utils.Resource

interface AccountService {
    suspend fun signIn(activityContext:Context): Resource<Int>

    suspend fun employeeLogin(uid: String, employeeId: String, password: String): Resource<Boolean>

    suspend fun checkUserLogin()

    suspend fun checkRegistration(email: String): Resource<Boolean>

    suspend fun employeeLogOut()
    suspend fun signOut()

}