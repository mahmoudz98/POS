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
package com.casecode.pos.core.domain.usecase.business

import com.casecode.pos.core.domain.usecase.CompleteBusinessUseCase
import com.casecode.pos.core.domain.utils.Resource
import com.casecode.pos.core.testing.repository.TestBusinessRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class CompleteBusinessUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testBusinessRepository = TestBusinessRepository()
    private val completeBusinessUseCase = CompleteBusinessUseCase(testBusinessRepository)

    @Test
    fun `when completed business successfully then return resource success`() = runTest {
        // When
        val result = completeBusinessUseCase()

        // Then
        assert(result is Resource.Success)
    }

    @Test
    fun `when completed business failed then return resource error`() = runTest {
        // given
        testBusinessRepository.setReturnError(true)
        // When
        val result = completeBusinessUseCase()

        // Then
        assert(result is Resource.Error)
    }
}