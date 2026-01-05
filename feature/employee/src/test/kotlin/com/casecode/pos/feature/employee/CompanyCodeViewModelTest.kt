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
package com.casecode.pos.feature.employee

import com.casecode.pos.core.domain.usecase.GetCompanyCodeUseCase
import com.casecode.pos.core.domain.usecase.GetCurrentSessionUseCase
import com.casecode.pos.core.model.SessionStateResult
import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.BusinessStatus
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import com.casecode.pos.core.testing.repository.business.TestSessionRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.Clock

class CompanyCodeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CompanyCodeViewModel
    private lateinit var sessionRepository: TestSessionRepository
    private lateinit var businessRepository: TestBusinessRepository
    private lateinit var getCompanyCodeUseCase: GetCompanyCodeUseCase

    @Before
    fun setup() {
        sessionRepository = TestSessionRepository()
        businessRepository = TestBusinessRepository()
        val getCurrentSessionUseCase = GetCurrentSessionUseCase(sessionRepository)
        getCompanyCodeUseCase = GetCompanyCodeUseCase(getCurrentSessionUseCase, businessRepository)
        viewModel = CompanyCodeViewModel(getCompanyCodeUseCase)
    }

    @Test
    fun whenBusinessExists_returnsCode() = runTest {
        // Given
        val business = Business(
            id = "bus1",
            name = "Test Business",
            companyCode = "CODE123",
            ownerUid = "owner1",
            vertical = Vertical.RETAIL,
            currencyCode = "USD",
            status = BusinessStatus.ACTIVE,
            email = "test@test.com",
            phone = "123",
            updatedAt = Clock.System.now(),
            createdAt = Clock.System.now(),
        )
        // Helper to collect flow in background
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.companyCode.collect() }

        // When
        businessRepository.addBusiness(business)
        sessionRepository.setSessionInfo(
            SessionStateResult.OwnerLoggedIn("bus1", "branch1", "owner1", "Test Business"),
        )

        advanceUntilIdle()

        // Then
        assertEquals("CODE123", viewModel.companyCode.value)
    }

    @Test
    fun whenNoSession_returnsNull() = runTest {
        // Helper to collect flow in background
        backgroundScope.launch(UnconfinedTestDispatcher()) { viewModel.companyCode.collect() }

        // When
        sessionRepository.setSessionInfo(SessionStateResult.None)

        advanceUntilIdle()

        // Then
        assertEquals(null, viewModel.companyCode.value)
    }
}
