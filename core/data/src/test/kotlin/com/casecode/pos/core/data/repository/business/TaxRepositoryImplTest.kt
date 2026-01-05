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

import com.casecode.pos.core.firebase.model.NetworkTaxRate
import com.casecode.pos.core.model.business.TaxRate
import com.casecode.pos.core.testing.datasource.TestTaxNetworkDataSource
import com.casecode.pos.core.testing.services.TestLogService
import com.casecode.pos.core.testing.util.CoroutinesTestRule
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TaxRepositoryImplTest {

    @get:Rule
    val coroutinesRule = CoroutinesTestRule()

    private lateinit var db: FirebaseFirestore
    private lateinit var network: TestTaxNetworkDataSource
    private lateinit var logService: TestLogService
    private lateinit var repository: TaxRepositoryImpl

    @Before
    fun setup() {
        db = mockk(relaxed = true)
        network = TestTaxNetworkDataSource()
        logService = TestLogService()
        repository = TaxRepositoryImpl(
            db = db,
            network = network,
            logService = logService,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    @Test
    fun getTaxRates_success_returnsTaxRates() = runTest {
        // Given
        val networkTaxRate = NetworkTaxRate("id1", "VAT", 15.0f)
        network.setTaxRates(listOf(networkTaxRate))

        // When
        val result = repository.getTaxRates("biz1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("VAT", result.getOrNull()?.first()?.name)
    }

    @Test
    fun addTaxRate_success_returnsId() = runTest {
        // Given
        val taxRate = TaxRate(id = "id2", name = "GST", rate = 10.0f)

        // When
        val result = repository.addTaxRate("biz1", taxRate)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("id2", result.getOrNull())
        assertEquals(1, network.getTaxRates("biz1").size)
    }
}
