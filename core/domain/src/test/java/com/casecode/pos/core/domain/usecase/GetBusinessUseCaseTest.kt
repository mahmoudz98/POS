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

import com.casecode.pos.core.domain.utils.BusinessResult
import com.casecode.pos.core.model.data.users.Business
import com.casecode.pos.core.testing.repository.TestBusinessRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Rule
import kotlin.test.Test

class GetBusinessUseCaseTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testBusinessRepository = TestBusinessRepository()
    private val getBusinessUseCase = GetBusinessUseCase(testBusinessRepository)

    @Test
    fun `when has business then return resource with success`() = runTest {
        val actualBusiness = Business()

        val expected = getBusinessUseCase()
        assertThat(BusinessResult.Success(actualBusiness), `is`(expected))
    }

    @Test
    fun `when has error then return resource with empty`() = runTest {
        testBusinessRepository.setReturnError(true)
        val expected = getBusinessUseCase()
        assertThat(BusinessResult.Error(-1), `is`(expected))
    }
}