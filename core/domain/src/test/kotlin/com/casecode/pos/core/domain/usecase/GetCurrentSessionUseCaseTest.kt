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
import com.casecode.pos.core.model.business.EmployeeRole
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetCurrentSessionUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var sessionRepository: TestSessionRepository
    private lateinit var getCurrentSessionUseCase: GetCurrentSessionUseCase

    @Before
    fun setup() {
        sessionRepository = TestSessionRepository()
        getCurrentSessionUseCase = GetCurrentSessionUseCase(sessionRepository)
    }

    @Test
    fun whenLoading_returnsLoadingState() = runTest {
        val result = getCurrentSessionUseCase().first()
        assertEquals(SessionStateResult.Loading, result)
    }

    @Test
    fun whenLoggedOut_returnsNoneState() = runTest {
        sessionRepository.setSessionInfo(SessionStateResult.None)
        val result = getCurrentSessionUseCase().first()
        assertEquals(SessionStateResult.None, result)
    }

    @Test
    fun whenOwnerOnBoarding_returnsOwnerOnBoardingState() = runTest {
        sessionRepository.setSessionInfo(SessionStateResult.OwnerOnBoarding("businessId"))
        val result = getCurrentSessionUseCase().first()
        assertEquals(SessionStateResult.OwnerOnBoarding("businessId"), result)
    }

    @Test
    fun whenOwnerLoggedIn_returnsOwnerLoggedInState() = runTest {
        val expectedSession = SessionStateResult.OwnerLoggedIn(
            "businessId",
            "activeBranchId",
            "userId",
            "userName",
        )
        sessionRepository.setSessionInfo(
            expectedSession,
        )
        val result = getCurrentSessionUseCase().first()
        assertEquals(expectedSession, result)
    }

    @Test
    fun whenEmployeeLoggedIn_returnsEmployeeLoggedInState() = runTest {
        val expectedSession = SessionStateResult.EmployeeLoggedIn(
            "businessId",
            "activeBranchId",
            "userId",
            "userName",
            EmployeeRole.CASHIER,
        )
        sessionRepository.setSessionInfo(
            expectedSession,
        )
        val result = getCurrentSessionUseCase().first()
        assertEquals(expectedSession, result)
    }
}
