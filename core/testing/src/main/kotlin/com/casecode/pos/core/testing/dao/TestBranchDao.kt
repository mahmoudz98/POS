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
package com.casecode.pos.core.testing.dao

import com.casecode.pos.core.database.dao.BranchDao
import com.casecode.pos.core.database.model.BranchEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class TestBranchDao : BranchDao {

    private val branchesFlow = MutableStateFlow<List<BranchEntity>>(emptyList())

    val insertedBranches = mutableListOf<BranchEntity>()

    override suspend fun insertOrReplaceBranches(branches: List<BranchEntity>) {
        insertedBranches.addAll(branches)

        val currentList = branchesFlow.value.toMutableList()
        branches.forEach { newBranch ->
            currentList.removeAll { it.branchId == newBranch.branchId }
            currentList.add(newBranch)
        }
        branchesFlow.value = currentList
    }

    override fun getBranches(businessId: String): Flow<List<BranchEntity>> {
        return branchesFlow.asStateFlow().map { list ->
            list.filter { it.businessId == businessId }
        }
    }

    override suspend fun getBranchesCount(businessId: String): Int {
        return branchesFlow.value.count { it.businessId == businessId }
    }

    override suspend fun deleteBranch(branchId: String) {
        val currentList = branchesFlow.value.toMutableList()
        currentList.removeAll { it.branchId == branchId }
        branchesFlow.value = currentList
    }
}
