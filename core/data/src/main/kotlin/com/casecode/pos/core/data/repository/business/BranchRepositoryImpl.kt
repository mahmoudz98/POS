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
import com.casecode.pos.core.data.model.OutboxPayload
import com.casecode.pos.core.data.model.asNetworkModel
import com.casecode.pos.core.database.dao.BranchDao
import com.casecode.pos.core.database.dao.OutboxCommandDao
import com.casecode.pos.core.database.model.OutboxCommandEntity
import com.casecode.pos.core.database.model.OutboxCommandStatus
import com.casecode.pos.core.database.model.OutboxEventType
import com.casecode.pos.core.database.model.asEntity
import com.casecode.pos.core.database.model.asExternalModel
import com.casecode.pos.core.database.util.DatabaseTransactionRunner
import com.casecode.pos.core.domain.repository.business.BranchRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.firebase.datasource.BusinessNetworkDataSource
import com.casecode.pos.core.model.business.Branch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val TAG = "BranchRepoImpl"

/**
 * Implementation of [BranchRepository] that provides offline-first access to branch data.
 * Data is primarily read from the local Room database, and changes are synchronized
 * with a remote data source (Firestore) via the Outbox Pattern.
 */

class BranchRepositoryImpl @Inject constructor(
    private val network: BusinessNetworkDataSource,
    private val transaction: DatabaseTransactionRunner,
    private val branchDao: BranchDao,
    private val outboxCommandDao: OutboxCommandDao,
    private val logService: LogService,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : BranchRepository {

    override fun getBranches(businessId: String): Flow<List<Branch>> {
        return branchDao.getBranches()
            .map { it.map { branchEntity -> branchEntity.asExternalModel() } }
    }
    override suspend fun addBranch(businessId: String, branch: Branch): Result<String> =
        withContext(ioDispatcher) {
            runCatching {
                // 1. Save to local database first (Lazy Write) and create OutboxCommand
                transaction.run {
                    // Save branch entity to local DB, marking it as not synced yet
                    branchDao.insertOrReplaceBranches(
                        listOf(
                            branch.asEntity(),
                        ),
                    )

                    // Create a command for the Outbox
                    val commandPayload = Json.encodeToString(
                        OutboxPayload.AddBranchPayload(
                            businessId = businessId,
                            branch = branch.asNetworkModel(),
                        ),
                    )

                    val outboxCommand = OutboxCommandEntity(
                        type = OutboxEventType.BRANCH_CREATED.ordinal,
                        payload = commandPayload,
                        status = OutboxCommandStatus.PENDING,
                    )
                    outboxCommandDao.insertCommand(outboxCommand)
                }

                branch.id
            }.onSuccess { newBranchId ->
                logService.log("$TAG: Successfully added new branch with ID $newBranchId to business $businessId locally. Sync requested.")
            }.onFailure { e ->
                logService.log("$TAG: addBranch local save or outbox command creation failed for businessId: $businessId, ${e.message}")
            }
        }

    suspend fun sync(): Boolean = withContext(ioDispatcher) {
        try {
            val unsyncedCommands = outboxCommandDao.getPendingCommands().first()
            val addBranchCommands = unsyncedCommands.filter { it.type == OutboxEventType.BRANCH_CREATED.ordinal }

            addBranchCommands.forEach { command ->
            /*    val payload = Json.decodeFromString<OutboxPayload.AddBranchPayload>(command.payload)
                network.addBranch(payload.businessId, payload.branch)
                outboxCommandDao.updateCommandStatus(
                    command.id,
                    OutboxCommandStatus.COMPLETED,
                    Clock.System.now(),
                )*/
            }
            true
        } catch (e: Exception) {
            logService.log("BranchRepository sync failed: ${e.message}")
            false
        }
    }
}
