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
package com.casecode.pos.core.domain.repository.business

import com.casecode.pos.core.domain.utils.Syncable
import com.casecode.pos.core.model.business.Branch
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing branch-related data.
 * Provides methods for retrieving, adding, and refreshing branch information.
 */

interface BranchRepository : Syncable {
    /**
     * Retrieves all branches for a given business ID as a reactive [Flow].
     * This flow emits the latest branch data from the local database.
     *
     * @return A [Flow] emitting a list of [Branch] objects.
     */
    fun getBranches(businessId: String): Flow<List<Branch>>

    /**
     * Adds a new branch to a specific business. This operation is typically performed locally first
     * and then synchronized with the remote data source.
     *
     * @param businessId The ID of the business to which the branch will be added.
     * @param branch The [Branch] object to add.
     * @return A [Result] containing the ID of the newly added branch if successful,
     *         or a [Throwable] if an error occurred during local persistence or sync request.
     */
    suspend fun addBranch(businessId: String, branch: Branch): Result<String>
}
