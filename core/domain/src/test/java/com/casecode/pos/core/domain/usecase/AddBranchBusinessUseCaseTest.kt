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

import com.casecode.pos.core.domain.utils.AddBranchBusinessResult
import com.casecode.pos.core.model.data.users.Branch
import com.casecode.pos.core.testing.repository.TestBusinessRepository
import com.casecode.pos.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class AddBranchBusinessUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Subject under test
    private val testBusinessRepository = TestBusinessRepository()
    private val addBranchBusinessUseCaseTest = AddBranchBusinessUseCase(testBusinessRepository)

    @Test
    fun `Successful branch addition`() = runTest {
        val inputBranch = Branch(1, "Test Branch", "1234567890")
        val result = addBranchBusinessUseCaseTest(inputBranch)
        assert(result is AddBranchBusinessResult.Success)
    }

    @Test
    fun `add branch when Repository error then return error`() = runTest {
        testBusinessRepository.setReturnError(true)
        val inputBranch = Branch(1, "Test Branch", "1234567890")
        val result = addBranchBusinessUseCaseTest(inputBranch)
        assert(result is AddBranchBusinessResult.Error)
    }

    @Test
    fun `add branch when Invalid branch data return error`() = runTest {
        val inputBranch = Branch(-1, "", "1234567890")
        val result = addBranchBusinessUseCaseTest(inputBranch)
        assert(result is AddBranchBusinessResult.Error)
    }
}