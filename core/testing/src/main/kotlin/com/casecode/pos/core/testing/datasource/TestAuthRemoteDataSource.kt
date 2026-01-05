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
package com.casecode.pos.core.testing.datasource

import com.casecode.pos.core.firebase.datasource.AuthRemoteDataSource
import com.casecode.pos.core.model.users.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TestAuthRemoteDataSource @Inject
constructor() : AuthRemoteDataSource {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser

    override suspend fun getCurrentUser(): User? = _currentUser.first()

    override suspend fun signInWithGoogle(idToken: String): User {
        val user = User(
            uid = "test-uid",
            email = "test@example.com",
            name = "Test User",
            photoUrl = "test phote",
        )
        _currentUser.value = user
        return user
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }

    fun setUser(user: User?) {
        _currentUser.value = user
    }
}
