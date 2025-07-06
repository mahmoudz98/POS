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

import com.casecode.pos.core.domain.repository.AuthRepositoryO
import com.casecode.pos.core.model.data.LoginStateResultOld
import com.casecode.pos.core.model.data.users.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class TestAuthRepositoryO
@Inject
constructor() : AuthRepositoryO {
    var userLoginChecked = false
        private set // To allow checking from tests but not setting externally

    override val loginData: Flow<LoginStateResultOld> = flowOf(LoginStateResultOld.NotSignIn)

    override suspend fun hasUser(): Boolean = true

    override suspend fun currentUserId(): String = "uidTest"

    override suspend fun currentNameLogin(): String = "TestName"

    override val currentUser: Flow<User?> =
        flowOf(User("TestUid", "TestEmail", "TestName", "TestPhotoUrl"))

    override suspend fun hasEmployeeLogin(): Boolean = true

    fun resetLoginCheck() {
        userLoginChecked = false
    }
}