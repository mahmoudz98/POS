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
package com.casecode.pos.core.data.repository.business

import com.casecode.pos.core.common.AppDispatchers
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.model.asExternalModel
import com.casecode.pos.core.data.model.asNetworkModel
import com.casecode.pos.core.domain.repository.business.BranchRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.firebase.datasource.BusinessNetworkDataSource
import com.casecode.pos.core.model.data.business.Branch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "BranchRepoImpl"

class BranchRepositoryImpl @Inject constructor(
    private val network: BusinessNetworkDataSource,
    private val logService: LogService,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : BranchRepository {


    /**
     * Fetches all branches for a given business, ordered by their creation date.
     */
    override suspend fun getBranches(businessId: String): Result<List<Branch>> =
        withContext(ioDispatcher) {
            runCatching {
                network.getBranches(businessId).map { it.asExternalModel() }
            }.onSuccess { branches ->
                if (branches.isEmpty()) {
                    logService.log("$TAG: No branches found for businessId: $businessId")
                } else {
                    logService.log("$TAG: Successfully fetched ${branches.size} branches for businessId: $businessId")
                }
            }.onFailure { e ->
                logService.log("$TAG: getBranches failed for businessId: $businessId, ${e.message}")
            }
        }

    /**
     * Adds a new branch to a business's branches sub-collection.
     */
    override suspend fun addBranch(businessId: String, branch: Branch): Result<String> =
        withContext(ioDispatcher) {
            runCatching {
                network.addBranch(businessId, branch.asNetworkModel())
            }.onSuccess { newBranchId ->
                logService.log("$TAG: Successfully added new branch with ID $newBranchId to business $businessId")
            }.onFailure { e ->
                logService.log("$TAG: addBranch failed for businessId: $businessId, ${e.message}")
            }
        }
}
