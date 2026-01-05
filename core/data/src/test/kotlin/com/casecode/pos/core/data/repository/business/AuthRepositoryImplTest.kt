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
package com.casecode.pos.core.data.repository.business

import com.casecode.pos.core.model.users.User
import com.casecode.pos.core.testing.datasource.TestAuthRemoteDataSource
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthRepositoryImplTest {

    @get:Rule
    val coroutinesRule = CoroutinesTestRule()

    private lateinit var authDataSource: TestAuthRemoteDataSource
    private lateinit var logService: TestLogService
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setup() {
        authDataSource = TestAuthRemoteDataSource()
        logService = TestLogService()
        repository = AuthRepositoryImpl(
            auth = authDataSource,
            logService = logService,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun currentUser_initiallyNull() = runTest {
        assertNull(repository.currentUser.first())
    }

    @Test
    fun currentUser_updatesWhenUserChanges() = runTest {
        val user = User(uid = "123", email = "test@test.com", name = "Test", photoUrl = null)
        authDataSource.setUser(user)
        assertEquals(user, repository.currentUser.first())
    }

    @Test
    fun getCurrentUser_returnsCurrentValue() = runTest {
        val user = User(uid = "123", email = "test@test.com", name = "Test", photoUrl = null)
        authDataSource.setUser(user)
        assertEquals(user, repository.getCurrentUser())
    }

    @Test
    fun signInWithGoogle_success_returnsUser() = runTest {
        val result = repository.signInWithGoogle("valid-token")
        assertTrue(result.isSuccess)
        assertEquals("test-uid", result.getOrNull()?.uid)
    }

    @Test
    fun signOut_clearsCurrentUser() = runTest {
        val user = User(uid = "123", email = "test@test.com", name = "Test", photoUrl = null)
        authDataSource.setUser(user)
        repository.signOut()
        assertNull(repository.currentUser.first())
    }
}
