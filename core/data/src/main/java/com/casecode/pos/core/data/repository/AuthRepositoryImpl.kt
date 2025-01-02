/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.data.repository

import com.casecode.pos.core.common.AppDispatchers.IO
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.datastore.PosPreferencesDataSource
import com.casecode.pos.core.model.data.LoginStateResult
import com.casecode.pos.core.model.data.users.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl
@Inject
constructor(
    private val auth: FirebaseAuth,
    private val posPreferencesDataSource: PosPreferencesDataSource,
    @Dispatcher(IO) private val io: CoroutineDispatcher,
) : com.casecode.pos.core.domain.repository.AuthRepository {
    override val loginData: Flow<LoginStateResult> =
        posPreferencesDataSource.loginData.map {
            LoginStateResult.Loading
            delay(300L)
            it
        }

    override suspend fun currentUserId(): String = withContext(io) {
        async {
            posPreferencesDataSource.currentUid.first() ?: ""
        }.await()
    }

    override suspend fun currentNameLogin(): String = withContext(io) {
        async {
            posPreferencesDataSource.currentNameLogin.first() ?: ""
        }.await()
    }

    override val currentUser: Flow<FirebaseUser?>
        get() =
            callbackFlow {
                val listener =
                    FirebaseAuth.AuthStateListener { auth ->
                        val user =
                            FirebaseUser(
                                auth.currentUser?.email,
                                auth.currentUser?.displayName,
                                auth.currentUser?.photoUrl.toString(),
                            )
                        this.trySend(user)
                    }
                auth.addAuthStateListener(listener)
                awaitClose { auth.removeAuthStateListener(listener) }
            }

    override suspend fun hasEmployeeLogin(): Boolean = loginData.last() is LoginStateResult.EmployeeLogin

    override suspend fun hasUser(): Boolean = currentUserId().isNotBlank()
}