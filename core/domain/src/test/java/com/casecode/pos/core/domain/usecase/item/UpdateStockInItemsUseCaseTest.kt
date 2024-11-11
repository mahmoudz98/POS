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
package com.casecode.pos.core.domain.usecase.item

import com.casecode.pos.core.domain.usecase.UpdateStockInItemsUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.model.data.users.Item
import com.casecode.pos.core.testing.repository.TestItemRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import com.casecode.pos.core.data.R.string as stringData
import com.casecode.pos.core.domain.R.string as stringDomain

class UpdateStockInItemsUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subjects under test
    private val testItemRepository = TestItemRepository()
    private val updateStockInItemsUseCase = UpdateStockInItemsUseCase(testItemRepository)

    @Test
    fun whenItemUpdated_returnsSuccess() = runTest {
        // Given
        val newItem =
            Item(
                name = "New Item",
                unitPrice = 10.0,
                quantity = 22,
                sku = "1212312",
                imageUrl = "newItemImage",
            )
        val itemsUpdate = listOf(newItem)
        // When
        val result = updateStockInItemsUseCase(itemsUpdate)
        // Then
        assertEquals(Resource.success(itemsUpdate), result)
    }

    @Test
    fun whenEmptyItems_returnsMessageEmpty() = runTest {
        // Given
        val emptyItems = listOf<Item>()
        // When
        val result = updateStockInItemsUseCase(emptyItems)
        // Then
        assertEquals(
            Resource.empty(message = stringDomain.core_domain_invoice_items_empty),
            result,
        )
    }

    @Test
    fun whenHasError_returnMessageError() = runTest {
        // Given
        val newItem =
            Item(
                name = "New Item",
                unitPrice = 10.0,
                quantity = 22,
                sku = "1212312",
                imageUrl = "newItemImage",
            )
        val itemsUpdate = listOf(newItem)
        // When
        testItemRepository setReturnError true
        val resultUpdate = updateStockInItemsUseCase(itemsUpdate)
        assertEquals(
            Resource.error(stringData.core_data_update_item_failure_generic),
            resultUpdate,

            )
    }
}