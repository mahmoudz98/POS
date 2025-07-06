package com.casecode.pos.core.data.repository.business

import com.casecode.pos.core.common.AppDispatchers
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.domain.repository.business.AuthRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.firebase.datasource.AuthRemoteDataSource
import com.casecode.pos.core.model.data.users.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: AuthRemoteDataSource,
    private val logService: LogService,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : AuthRepository {

    override val currentUser: Flow<User?> = auth.currentUser.flowOn(ioDispatcher)

    override suspend fun getCurrentUser(): User? = currentUser.first()

    override suspend fun signInWithGoogle(idToken: String): Result<User> =
        withContext(ioDispatcher) {
            runCatching {
                auth.signInWithGoogle(idToken)
            }.onFailure {
                logService.logNonFatalCrash(it)
            }
        }

    override suspend fun signOut() {
        withContext(ioDispatcher) {
            auth.signOut()
            logService.log("AuthRepo: User signed out from Firebase.")
        }
    }
}
