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

import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.testing.repository.business.TestBranchRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetBranchesUseCaseTest {
    private lateinit var testBranchRepository: TestBranchRepository
    private lateinit var getBranchesUseCase: GetBranchesUseCase

    @Before
    fun setup() {
        testBranchRepository = TestBranchRepository()
        getBranchesUseCase = GetBranchesUseCase(testBranchRepository)
    }

    @Test
    fun businessWithBranches_ReturnsBranches() = runTest {
        val businessId = "business_1"
        val branches = listOf(
            Branch(id = "1", name = "Branch 1", phone = "123456"),
            Branch(id = "2", name = "Branch 2", phone = "654321"),
        )
        testBranchRepository.setBranchesForBusiness(businessId, branches)

        val result = getBranchesUseCase(businessId).first()

        assertEquals(branches, result)
    }

    @Test
    fun businessWithNoBranches_ReturnsEmptyList() = runTest {
        val businessId = "business_2"
        testBranchRepository.setBranchesForBusiness(businessId, emptyList())

        val result = getBranchesUseCase(businessId).first()

        assertTrue(result.isEmpty())
    }
}
