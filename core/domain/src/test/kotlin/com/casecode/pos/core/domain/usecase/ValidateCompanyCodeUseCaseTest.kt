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

import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.BusinessStatus
import com.casecode.pos.core.model.business.Vertical
import com.casecode.pos.core.testing.repository.business.TestBusinessRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class ValidateCompanyCodeUseCaseTest {
    private lateinit var testBusinessRepository: TestBusinessRepository
    private lateinit var validateCompanyCodeUseCase: ValidateCompanyCodeUseCase

    private val testBusiness = Business(
        id = "business123",
        name = "Test Business",
        ownerUid = "owner123",
        vertical = Vertical.RETAIL,
        companyCode = "bus-BUBU",
        currencyCode = "USD",
        status = BusinessStatus.ACTIVE,
        email = "test@business.com",
        phone = "1234567890",
        updatedAt = Instant.parse("2024-01-01T00:00:00Z"),
        createdAt = Instant.parse("2024-01-01T00:00:00Z"),
    )

    @Before
    fun setup() {
        testBusinessRepository = TestBusinessRepository()
        validateCompanyCodeUseCase = ValidateCompanyCodeUseCase(testBusinessRepository)
    }

    @Test
    fun whenCodeIsNotValid_ReturnsFailure() = runTest {
        val result = validateCompanyCodeUseCase("short")
        assertTrue(result.isFailure)
    }

    @Test
    fun whenCodeWithoutHyphen_ReturnsFailure() = runTest {
        val result = validateCompanyCodeUseCase("longenoughbutnohyphen")
        assertTrue(result.isFailure)
    }

    @Test
    fun whenExistingBusiness_ReturnsTrue() = runTest {
        testBusinessRepository.addBusiness(testBusiness)

        val result = validateCompanyCodeUseCase("bus-BUBU")

        assertEquals(result.getOrNull(), true)
    }

    @Test
    fun whenNonExistingBusiness_ReturnsFalse() = runTest {
        val result = validateCompanyCodeUseCase("bus-NONE")

        assertTrue(result.isSuccess)
        assertEquals(result.getOrNull(), false)
    }
}
