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

import com.casecode.pos.core.model.SessionStateResult
import com.casecode.pos.core.model.users.User
import com.casecode.pos.core.testing.repository.business.TestAuthRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.services.TestLogService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SignOutUseCaseTest {
    private lateinit var testAuthRepository: TestAuthRepository
    private lateinit var testSessionRepository: TestSessionRepository
    private lateinit var signOutUseCase: SignOutUseCase

    @Before
    fun setup() {
        testAuthRepository = TestAuthRepository()
        testSessionRepository = TestSessionRepository()
        signOutUseCase = SignOutUseCase(
            authRepository = testAuthRepository,
            sessionRepository = testSessionRepository,
            logService = TestLogService(),
        )
    }

    @Test
    fun whenLoggedInUser_ClearsSessionAndSignsOut() = runTest {
        // Given an active session and logged in user
        testAuthRepository.setLoggedInUser(User(uid = "uid", email = "email", name = null, photoUrl = null))
        testSessionRepository.setSessionInfoOwnerLoggedIn()

        // When signing out
        signOutUseCase()

        // Then user is logged out and session is cleared
        assertNull(testAuthRepository.getCurrentUser())
        assertTrue(testSessionRepository.sessionInfo.first() is SessionStateResult.None)
    }
}
