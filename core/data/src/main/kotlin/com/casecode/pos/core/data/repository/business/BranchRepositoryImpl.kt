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

import com.casecode.pos.core.analytics.AnalyticsEvent
import com.casecode.pos.core.analytics.AnalyticsEvent.Param
import com.casecode.pos.core.analytics.AnalyticsHelper
import com.casecode.pos.core.common.AppDispatchers
import com.casecode.pos.core.common.Dispatcher
import com.casecode.pos.core.data.model.OutboxPayload
import com.casecode.pos.core.data.model.asEntity
import com.casecode.pos.core.data.model.asNetworkModel
import com.casecode.pos.core.database.dao.BranchDao
import com.casecode.pos.core.database.dao.LocalSignalDao
import com.casecode.pos.core.database.dao.OutboxCommandDao
import com.casecode.pos.core.database.model.LocalSignalStatus
import com.casecode.pos.core.database.model.OutboxCommandEntity
import com.casecode.pos.core.database.model.OutboxCommandStatus
import com.casecode.pos.core.database.model.OutboxEventType
import com.casecode.pos.core.database.model.asEntity
import com.casecode.pos.core.database.model.asExternalModel
import com.casecode.pos.core.database.util.DatabaseTransactionRunner
import com.casecode.pos.core.domain.repository.business.BranchRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.firebase.datasource.BusinessNetworkDataSource
import com.casecode.pos.core.firebase.datasource.InboxNetworkDataSource
import com.casecode.pos.core.firebase.model.NetworkInboxSignal
import com.casecode.pos.core.firebase.model.OperationType
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.data.SyncableEntityType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Clock

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
    private val localSignalDao: LocalSignalDao,
    private val inboxNetworkDataSource: InboxNetworkDataSource,
    private val json: Json,
    private val analyticsHelper: AnalyticsHelper,
    private val logService: LogService,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : BranchRepository {

    override fun getBranches(businessId: String): Flow<List<Branch>> {
        return branchDao.getBranches(businessId)
            .onStart {
                val cachedCount = branchDao.getBranchesCount(businessId)
                if (cachedCount == 0) {
                    fetchAndCacheBranches(businessId)
                }
            }
            .map { localBranches ->
                localBranches.map { it.asExternalModel() }
            }
            .flowOn(ioDispatcher)
    }

    private suspend fun fetchAndCacheBranches(businessId: String) {
        try {
            val networkBranches = network.getBranches(businessId)
            if (networkBranches.isNotEmpty()) {
                val entities = networkBranches.map { it.asEntity(businessId) }
                branchDao.insertOrReplaceBranches(entities)
                logService.log("$TAG: Cached ${networkBranches.size} branches from network")
            } else {
                logService.log("$TAG: No branches available from network")
            }
        } catch (e: Exception) {
            logService.log("$TAG: Network fetch failed. Error: ${e.message}")
            // Optionally: throw custom exception or emit error state
        }
    }

    override suspend fun addBranch(businessId: String, branch: Branch): Result<String> =
        withContext(ioDispatcher) {
            runCatching {
                transaction.run {
                    branchDao.insertOrReplaceBranches(listOf(branch.asEntity(businessId)))

                    val commandPayload = json.encodeToString(
                        OutboxPayload.AddBranchPayload(
                            businessId = businessId,
                            branch = branch.asNetworkModel(),
                        ),
                    )

                    val outboxCommand = OutboxCommandEntity(
                        type = OutboxEventType.Branch.CREATED,
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

    override suspend fun syncUp(): Boolean = withContext(ioDispatcher) {
        val startTime = Clock.System.now()
        analyticsHelper.logEvent(AnalyticsEvent("branch_sync_up_start"))
        var success = true
        try {
            val pendingCommands = outboxCommandDao.getPendingCommands().first()
            val branchCommands = pendingCommands.filter { it.type is OutboxEventType.Branch }

            branchCommands.forEach { command ->
                try {
                    outboxCommandDao.updateCommandStatus(
                        command.id,
                        OutboxCommandStatus.PROCESSING,
                        command.attemptCount,
                        Clock.System.now(),
                    )
                    val payload = json.decodeFromString<OutboxPayload.AddBranchPayload>(command.payload)

                    when (command.type) {
                        OutboxEventType.Branch.CREATED -> {
                            network.addBranch(payload.businessId, payload.branch)
                        }
                        // TODO: Implement when network methods are available
                        // OutboxEventType.Branch.UPDATED -> network.updateBranch(...)
                        // OutboxEventType.Branch.DELETED -> network.deleteBranch(...)
                        else -> logService.log("$TAG(syncUp): Unknown branch command type: ${command.type}")
                    }

                    val operationType = when (command.type) {
                        OutboxEventType.Branch.CREATED -> OperationType.CREATED
                        OutboxEventType.Branch.UPDATED -> OperationType.UPDATED
                        OutboxEventType.Branch.DELETED -> OperationType.DELETED
                        else -> OperationType.UPDATED
                    }
                    inboxNetworkDataSource.postSignal(
                        payload.businessId,
                        NetworkInboxSignal(
                            entityType = SyncableEntityType.BRANCH.name,
                            entityId = payload.branch.id,
                            operationType = operationType,
                            lastUpdated = Clock.System.now().toEpochMilliseconds(),
                        ),
                    )
                    outboxCommandDao.updateCommandStatus(
                        command.id,
                        OutboxCommandStatus.COMPLETED,
                        command.attemptCount,
                        Clock.System.now(),
                    )
                } catch (e: Exception) {
                    logService.log("$TAG(syncUp): Failed to process command ${command.id}. Error: ${e.message}")
                    outboxCommandDao.updateCommandStatus(
                        command.id,
                        OutboxCommandStatus.FAILED,
                        command.attemptCount + 1,
                        Clock.System.now(),
                        e.message,
                    )
                }
            }
        } catch (e: Exception) {
            logService.log("$TAG(syncUp): Failed with exception: ${e.message}")
            success = false
        }
        val duration = Clock.System.now() - startTime
        analyticsHelper.logEvent(
            AnalyticsEvent(
                "branch_sync_up_complete",
                listOf(Param("duration_ms", duration.inWholeMilliseconds.toString())),
            ),
        )
        success
    }

    override suspend fun syncDown(): Boolean = withContext(ioDispatcher) {
        val startTime = Clock.System.now()
        analyticsHelper.logEvent(AnalyticsEvent("branch_sync_down_start"))
        var success = true
        try {
            val pendingSignals = localSignalDao.getPendingSignals().first()
            val branchSignals = pendingSignals.filter { it.entityType == SyncableEntityType.BRANCH }

            branchSignals.forEach { signal ->
                try {
                    val branchId = signal.entityId

                    when (signal.operationType) {
                        OperationType.CREATED, OperationType.UPDATED -> {
                            // Fetch all branches and find the one we need
                            val networkBranches = network.getBranches(signal.businessId)
                            val networkBranch = networkBranches.find { it.id == branchId }

                            if (networkBranch != null) {
                                val branchEntity = networkBranch.asEntity(signal.businessId)
                                branchDao.insertOrReplaceBranches(listOf(branchEntity))
                                logService.log("$TAG(syncDown): Synced branch $branchId from network.")
                            } else {
                                logService.log("$TAG(syncDown): Branch $branchId not found on network.")
                            }
                        }

                        OperationType.DELETED -> {
                            branchDao.deleteBranch(branchId)
                            logService.log("$TAG(syncDown): Deleted branch $branchId locally.")
                        }

                        else -> {
                            logService.log("$TAG(syncDown): Unknown operation type: ${signal.operationType}")
                        }
                    }
                    localSignalDao.updateSignalStatus(signal.id, LocalSignalStatus.PROCESSED)
                } catch (e: Exception) {
                    logService.log("$TAG(syncDown): Failed to process signal ${signal.id}. Error: ${e.message}")
                    localSignalDao.updateSignalStatus(signal.id, LocalSignalStatus.FAILED)
                    success = false
                }
            }
        } catch (e: Exception) {
            logService.log("$TAG(syncDown): Failed with exception: ${e.message}")
            success = false
        }
        val duration = Clock.System.now() - startTime
        analyticsHelper.logEvent(
            AnalyticsEvent(
                "branch_sync_down_complete",
                listOf(Param("duration_ms", duration.inWholeMilliseconds.toString())),
            ),
        )
        success
    }
}
