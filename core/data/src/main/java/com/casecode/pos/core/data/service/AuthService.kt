package com.casecode.pos.core.data.service

import com.casecode.pos.core.data.R
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.LoginStateResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthService {
    val currentUser: Flow<FirebaseUser?>
    val loginData: Flow<LoginStateResult>
    suspend fun hasUser(): Boolean
    suspend fun  currentUserId(): String
    suspend fun currentNameLogin(): String
    suspend fun hasEmployeeLogin(): Boolean
}
suspend inline fun  <T> AuthService.checkUserNotFound(onUserNotFound:(Resource<T>) -> Unit)  {
    if (!this.hasUser()) {
        onUserNotFound(Resource.Error(R.string.core_data_uid_empty))
    }
}