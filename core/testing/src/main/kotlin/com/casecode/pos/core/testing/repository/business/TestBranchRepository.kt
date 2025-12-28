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
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.testing.base.TestRepository
import com.casecode.pos.core.testing.data.branchesTestData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestBranchRepository @Inject constructor() : TestRepository(), BranchRepository {

    private val branchesByBusiness = mutableMapOf<String, MutableList<Branch>>()
    private val branchesFlow: MutableSharedFlow<List<Branch>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun getBranches(businessId: String): Flow<List<Branch>> {
        return branchesFlow.asSharedFlow()
    }

    override suspend fun addBranch(businessId: String, branch: Branch): Result<String> {
        // Pattern: Check for failure first.
        getFailureResult<String>()?.let { return it }

        // If no failure, perform the success logic.
        val branchList = branchesByBusiness.getOrPut(businessId) { mutableListOf() }
        val newId = branch.id.ifEmpty { UUID.randomUUID().toString() } // Simulate ID generation
        val branchWithId = branch.copy(id = newId)
        branchList.add(branchWithId)
        branchesFlow.emit(branchList) // Update the flow when a branch is added

        return Result.success(newId)
    }

    /**
     * A test-only API to send a list of branches to the flow.
     */
    suspend fun sendBranches(branches: List<Branch> = branchesTestData) {
        branchesFlow.emit(branches)
    }

    /**
     * Pre-populates the fake repository with a list of branches for a specific business.
     * This also updates the Flow.
     */
    suspend fun setBranchesForBusiness(businessId: String, branches: List<Branch>) {
        branchesByBusiness[businessId] = branches.toMutableList()
        branchesFlow.emit(branches)
    }

    /**
     * Resets the fake repository to a clean state between tests.
     */
    override fun clear() {
        branchesByBusiness.clear()
        branchesFlow.resetReplayCache()
        returnSuccess() // From FakeRepository base class
    }

    override suspend fun syncUp(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun syncDown(): Boolean {
        TODO("Not yet implemented")
    }
}
