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
    fun loadingState_ReturnsLoading() = runTest {
        val result = getCurrentSessionUseCase().first()

        assertEquals(SessionStateResult.Loading, result)
    }

    @Test
    fun loggedOutState_ReturnsNone() = runTest {
        sessionRepository.setSessionInfo(SessionStateResult.None)

        val result = getCurrentSessionUseCase().first()

        assertEquals(SessionStateResult.None, result)
    }

    @Test
    fun ownerOnBoardingState_ReturnsOwnerOnBoarding() = runTest {
        val expectedState = SessionStateResult.OwnerOnBoarding("businessId")
        sessionRepository.setSessionInfo(expectedState)

        val result = getCurrentSessionUseCase().first()

        assertEquals(expectedState, result)
    }

    @Test
    fun ownerLoggedInState_ReturnsOwnerLoggedIn() = runTest {
        val expectedSession = SessionStateResult.OwnerLoggedIn(
            businessId = "businessId",
            activeBranchId = "activeBranchId",
            userId = "userId",
            userName = "userName",
        )
        sessionRepository.setSessionInfo(expectedSession)

        val result = getCurrentSessionUseCase().first()

        assertEquals(expectedSession, result)
    }

    @Test
    fun employeeLoggedInState_ReturnsEmployeeLoggedIn() = runTest {
        val expectedSession = SessionStateResult.EmployeeLoggedIn(
            businessId = "businessId",
            activeBranchId = "activeBranchId",
            userId = "userId",
            userName = "userName",
            role = EmployeeRole.CASHIER,
        )
        sessionRepository.setSessionInfo(expectedSession)

        val result = getCurrentSessionUseCase().first()

        assertEquals(expectedSession, result)
    }
}
