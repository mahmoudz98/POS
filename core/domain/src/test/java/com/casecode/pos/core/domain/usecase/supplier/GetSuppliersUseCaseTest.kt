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
package com.casecode.pos.core.domain.usecase.supplier

import com.casecode.pos.core.domain.usecase.GetSuppliersUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.testing.repository.TestSupplierRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import com.casecode.pos.core.data.R.string as stringData

class GetSuppliersUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testSupplierRepository = TestSupplierRepository()
    private val getSupplierUseCase = GetSuppliersUseCase(testSupplierRepository)

    @Test
    fun whenSuppliersExist_returnsSuppliers() = runTest {
        // Given
        testSupplierRepository.sendSuppliers()
        // when
        val suppliers = getSupplierUseCase()
        // Then
        assertEquals(Resource.success(testSupplierRepository.suppliersTest), suppliers.first())
    }

    @Test
    fun whenHasError_returnsError() = runTest {
        // Given
        testSupplierRepository.setReturnError(true)
        // When
        val suppliers = getSupplierUseCase()
        // Then
        assertEquals(
            Resource.error(stringData.core_data_error_fetching_suppliers),
            suppliers.first(),
        )
    }

    @Test
    fun whenHasNoSuppliers_returnsEmpty() = runTest {
        // Given
        testSupplierRepository.setReturnEmpty(true)
        // When
        val suppliers = getSupplierUseCase()
        // Then
        assertEquals(Resource.empty(), suppliers.first())
    }
}