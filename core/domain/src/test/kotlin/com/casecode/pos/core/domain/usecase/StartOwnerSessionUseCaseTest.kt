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

import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.users.User
import com.casecode.pos.core.testing.repository.business.TestBranchRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StartOwnerSessionUseCaseTest {
    private lateinit var testSessionRepository: TestSessionRepository
    private lateinit var testBranchRepository: TestBranchRepository
    private lateinit var startOwnerSessionUseCase: StartOwnerSessionUseCase

    @Before
    fun setup() {
        testSessionRepository = TestSessionRepository()
        testBranchRepository = TestBranchRepository()
        startOwnerSessionUseCase = StartOwnerSessionUseCase(testSessionRepository, testBranchRepository)
    }

    @Test
    fun whenBranchId_StartsSessionWithBranchId() = runTest {
        val user = User(
            uid = "user1",
            email = "test@example.com",
            name = null,
            photoUrl = null,
        )
        val branchId = "branch1"

        val result = startOwnerSessionUseCase(user, branchId)

        assertTrue(result.isSuccess)
        assertEquals(user, testSessionRepository.lastOwnerSession)
        assertEquals(branchId, testSessionRepository.lastBranchId)
    }

    @Test
    fun whenEmptyBranchId_FetchesBranchAndStartsSession() = runTest {
        val user = User(
            uid = "user1",
            email = "test@example.com",
            name = null,
            photoUrl = null,
        )
        val branch = Branch(id = "fetched_branch", name = "Fetched Branch", phone = "123")
        testBranchRepository.setBranchesForBusiness(user.uid, listOf(branch))

        val result = startOwnerSessionUseCase(user)

        assertTrue(result.isSuccess)
        assertEquals(user, testSessionRepository.lastOwnerSession)
        assertEquals(branch.id, testSessionRepository.lastBranchId)
    }

    @Test
    fun whenNoBranchesFound_ReturnsFailure() = runTest {
        val user = User(
            uid = "user1",
            email = "test@example.com",
            name = null,
            photoUrl = null,
        )
        testBranchRepository.setBranchesForBusiness(user.uid, emptyList())

        val result = startOwnerSessionUseCase(user)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }
}
