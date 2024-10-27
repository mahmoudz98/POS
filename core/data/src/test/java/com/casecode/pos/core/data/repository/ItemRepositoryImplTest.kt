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
package com.casecode.pos.core.data.repository

import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.testing.repository.TestItemRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class ItemRepositoryImplTest {
    // Subject under test.
    private val testItemRepository: TestItemRepository = TestItemRepository()

    @Test
    fun getItems_whenHasItems_returnsResourceSuccessItems() =
        runTest {
            // When - get items
            val actualResult = testItemRepository.getItems().first()
            // Then - assert that the result is success
            assertEquals(Resource.Success(testItemRepository.itemsTest), actualResult)
        }

    @Test
    fun getItems_whenNoItems_returnsResourceEmpty() =
        runTest {
            // Given - set return empty items
            testItemRepository.setReturnEmpty(true)
            // When - get items
            val actualResult = testItemRepository.getItems().first()
            // Then - assert that the result is success
            assertEquals(Resource.empty(), actualResult)
        }

    @Test
    fun getItems_whenError_returnsResourceError() =
        runTest {
            // Given - set return error
            testItemRepository.setReturnError(true)
            // When - get items
            val actualResult = testItemRepository.getItems().first()
            // Then - assert that the result is success
            assertEquals(Resource.error("Error"), actualResult)
        }
}