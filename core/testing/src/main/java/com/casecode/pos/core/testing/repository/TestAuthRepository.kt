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
package com.casecode.pos.core.testing.repository

import com.casecode.pos.core.domain.repository.AuthRepository
import com.casecode.pos.core.model.data.LoginStateResult
import com.casecode.pos.core.model.data.users.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class TestAuthRepository
@Inject
constructor() : AuthRepository {
    override val loginData: Flow<LoginStateResult> = flowOf(LoginStateResult.NotSignIn)

    override suspend fun hasUser(): Boolean = true

    override suspend fun currentUserId(): String = "uidTest"

    override suspend fun currentNameLogin(): String = "TestName"

    override val currentUser: Flow<FirebaseUser?> =
        flowOf(FirebaseUser("TestEmail", "TestName", "TestPhotoUrl"))

    override suspend fun hasEmployeeLogin(): Boolean = true
}