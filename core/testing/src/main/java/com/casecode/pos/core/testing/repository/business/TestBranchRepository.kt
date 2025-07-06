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
package com.casecode.pos.core.testing.repository.business

import com.casecode.pos.core.domain.repository.business.BranchRepository
import com.casecode.pos.core.model.data.business.Branch
import com.casecode.pos.core.testing.base.FakeRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.toMutableList

@Singleton
class TestBranchRepository @Inject constructor() : FakeRepository(), BranchRepository {

    // An in-memory store for our fake data
    private val branchesByBusiness = mutableMapOf<String, MutableList<Branch>>()

    override suspend fun getBranches(businessId: String): Result<List<Branch>> {
        // Pattern: Check for failure first.
        getFailureResult<List<Branch>>()?.let { return it }

        // If no failure, return the data from our in-memory map.
        return Result.success(branchesByBusiness[businessId] ?: emptyList())
    }

    override suspend fun addBranch(businessId: String, branch: Branch): Result<String> {
        // Pattern: Check for failure first.
        getFailureResult<String>()?.let { return it }

        // If no failure, perform the success logic.
        val branchList = branchesByBusiness.getOrPut(businessId) { mutableListOf() }
        val newId = branch.id.ifEmpty { UUID.randomUUID().toString() } // Simulate ID generation
        val branchWithId = branch.copy(id = newId)
        branchList.add(branchWithId)

        return Result.success(newId)
    }

    // --- Test Control Functions ---

    /**
     * Pre-populates the fake repository with a list of branches for a specific business.
     */
    fun setBranchesForBusiness(businessId: String, branches: List<Branch>) {
        branchesByBusiness[businessId] = branches.toMutableList()
    }

    /**
     * Resets the fake repository to a clean state between tests.
     */
    fun clear() {
        branchesByBusiness.clear()
        returnSuccess() // From FakeRepository base class
    }
}