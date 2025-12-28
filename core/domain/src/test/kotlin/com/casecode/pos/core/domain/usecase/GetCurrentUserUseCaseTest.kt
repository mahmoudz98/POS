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
package com.casecode.pos.core.domain.usecase

import com.casecode.pos.core.model.users.User
import com.casecode.pos.core.testing.repository.business.TestAuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetCurrentUserUseCaseTest {
    private lateinit var testAuthRepository: TestAuthRepository
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    @Before
    fun setup() {
        testAuthRepository = TestAuthRepository()
        getCurrentUserUseCase = GetCurrentUserUseCase(testAuthRepository)
    }

    @Test
    fun loggedIn_ReturnsCurrentUser() = runTest {
        val user = User(
            uid = "user123",
            email = "test@example.com",
            name = null,
            photoUrl = null,
        )
        testAuthRepository.setLoggedInUser(user)

        val result = getCurrentUserUseCase()

        assertEquals(user, result)
    }

    @Test
    fun notLoggedIn_ReturnsNull() = runTest {
        testAuthRepository.setLoggedInUser(null)

        val result = getCurrentUserUseCase()

        assertNull(result)
    }
}
