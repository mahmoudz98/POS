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

import com.casecode.pos.core.domain.repository.AccountRepository
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.domain.utils.SignInGoogleState
import javax.inject.Inject

class TestAccountRepository
@Inject
constructor() : AccountRepository {
    var signInResult: SignInGoogleState =
        SignInGoogleState.Success
    var employeeLoginResult: Resource<Boolean> = Resource.Success(true)

    override fun isGooglePlayServicesAvailable(): Boolean = true

    override suspend fun signIn(idToken: suspend () -> String): SignInGoogleState = signInResult

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