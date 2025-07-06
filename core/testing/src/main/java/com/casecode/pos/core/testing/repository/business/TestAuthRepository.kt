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
package com.casecode.pos.core.testing.repository.business

import com.casecode.pos.core.domain.repository.business.AuthRepository
import com.casecode.pos.core.model.data.users.User
import com.casecode.pos.core.testing.base.FakeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestAuthRepository @Inject constructor() : FakeRepository(), AuthRepository {

    private val _currentUserFlow = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUserFlow.asStateFlow()

    // This variable will hold the pre-configured success result.
    private var successUser: User? = null

    override suspend fun getCurrentUser(): User? {
        // We can't easily simulate a failure for a non-Result return type,
        // so this method remains a simple property read.
        return _currentUserFlow.value
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        // The new pattern: First, check if a failure has been configured.
        getFailureResult<User>()?.let { return it }

        // If no failure is set, return the pre-configured success result.
        // Or fail if no success result was configured, making tests more explicit.
        return successUser?.let { Result.success(it) }
            ?: Result.failure(IllegalStateException("TestAuthRepository: signInWithGoogle was called, but no success or failure state was configured."))
    }

    override suspend fun signOut() {
        _currentUserFlow.value = null
    }

    // --- Test Control Functions ---

    /**
     * Configures the repository to return a successful sign-in result with the given user.
     * It also resets any previously set failure state.
     */
    fun sendSignInSuccess(user: User) {
        returnSuccess() // From FakeRepository base class
        this.successUser = user
        _currentUserFlow.value = user
    }

    /**
     * Overrides the base setFailure to also clear the success user, preventing ambiguity.
     */
    fun sendSignInFailure(exception: Throwable) {
        setFailure(exception) // From FakeRepository base class
        this.successUser = null
    }

    /**
     * Test function to set the initial logged-in user state without faking a sign-in call.
     */
    fun setLoggedInUser(user: User?) {
        _currentUserFlow.value = user
    }

    fun clear() {
        returnSuccess()
        successUser = null
        _currentUserFlow.value = null
    }
}