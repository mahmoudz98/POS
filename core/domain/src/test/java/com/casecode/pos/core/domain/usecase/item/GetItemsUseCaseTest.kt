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

import com.casecode.pos.core.domain.usecase.GetItemsUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.testing.repository.TestItemRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import com.casecode.pos.core.data.R.string as stringData

class GetItemsUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subjects under test
    private val testItemRepository = TestItemRepository()
    private val getItemsUseCase = GetItemsUseCase(testItemRepository)

    @Test
    fun whenItemsExist_returnsItems() =
        runTest {
            val items = getItemsUseCase()
            testItemRepository.sendItems()
            assertEquals(
                items.first(),
                (Resource.success(testItemRepository.itemsTest)),
            )
        }

    @Test
    fun whenHasError_returnsError() =
        runTest {
            // Given
            testItemRepository.setReturnError(true)
            // When
            val items = getItemsUseCase()
            // Then
            assertEquals(items.first(), Resource.error(stringData.core_data_error_fetching_items))
        }

    @Test
    fun whenHasNoItems_returnsEmpty() =
        runTest {
            // Given
            testItemRepository.setReturnEmpty(true)

            // When
            val items = getItemsUseCase()

            // Then
            assertEquals(items.first(), (Resource.empty()))
        }
}