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

import com.casecode.pos.core.domain.usecase.DeleteSupplierUseCase
import com.casecode.pos.core.domain.utils.OperationResult
import com.casecode.pos.core.model.data.users.Supplier
import com.casecode.pos.core.testing.repository.TestSupplierRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import com.casecode.pos.core.data.R.string as stringData

class DeleteSupplierUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testSupplierRepository = TestSupplierRepository()
    private val deleteSupplierUseCase = DeleteSupplierUseCase(testSupplierRepository)
    private val deleteSupplier = Supplier(
        id = "1",
        companyName = "Samsung",
        contactName = "John Doe",
        contactPhone = "1234567890",
        contactEmail = "test@supplier.com",
        address = "Test Address",
        category = "Electronics",
    )

    @Test
    fun whenSupplierDeleted_returnSuccess() = runTest {
        val result = deleteSupplierUseCase(deleteSupplier)

        assertEquals(result, OperationResult.Success)
    }

    @Test
    fun whenHasError_returnsError() = runTest {
        testSupplierRepository.setReturnError(true)
        val result = deleteSupplierUseCase(deleteSupplier)
        assertEquals(
            result,
            OperationResult.Failure(stringData.core_data_delete_supplier_failure_generic),
        )
    }
}