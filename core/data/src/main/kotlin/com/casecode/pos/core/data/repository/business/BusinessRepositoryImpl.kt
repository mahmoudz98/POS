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
import com.casecode.pos.core.data.model.asExternalModel
import com.casecode.pos.core.data.model.asNetworkModel
import com.casecode.pos.core.database.dao.BranchDao
import com.casecode.pos.core.database.dao.BusinessDao
import com.casecode.pos.core.database.dao.LocalSignalDao
import com.casecode.pos.core.database.dao.OutboxCommandDao
import com.casecode.pos.core.database.dao.SubscriptionDao
import com.casecode.pos.core.database.dao.TaxRateDao
import com.casecode.pos.core.database.model.LocalSignalStatus
import com.casecode.pos.core.database.model.OutboxCommandEntity
import com.casecode.pos.core.database.model.OutboxCommandStatus
import com.casecode.pos.core.database.model.OutboxEventType
import com.casecode.pos.core.database.model.asEntity
import com.casecode.pos.core.database.model.asExternalModel
import com.casecode.pos.core.database.util.DatabaseTransactionRunner
import com.casecode.pos.core.domain.repository.business.BusinessRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.firebase.datasource.BusinessNetworkDataSource
import com.casecode.pos.core.firebase.datasource.InboxNetworkDataSource
import com.casecode.pos.core.firebase.model.NetworkInboxSignal
import com.casecode.pos.core.model.business.BillingEvent
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.Subscription
import com.casecode.pos.core.model.business.TaxRate
import com.casecode.pos.core.model.data.SyncableEntityType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.time.Clock

class BusinessRepositoryImpl @Inject constructor(
    private val network: BusinessNetworkDataSource,
    private val businessDao: BusinessDao,
    private val branchDao: BranchDao,
    private val subscriptionDao: SubscriptionDao,
    private val taxRateDao: TaxRateDao,
    private val outboxCommandDao: OutboxCommandDao,
    private val localSignalDao: LocalSignalDao,
    private val transaction: DatabaseTransactionRunner,
    private val inboxNetworkDataSource: InboxNetworkDataSource,
    private val json: Json,
    private val logService: LogService,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : BusinessRepository {

    override suspend fun createInitialBusiness(
        business: Business,
        initialBranches: List<Branch>,
        initialTaxes: List<TaxRate>,
        initialSubscription: Subscription,
        initialBillingEvent: BillingEvent,
    ): Result<String> = withContext(ioDispatcher) {
        try {
            transaction.run {
                val businessEntity = business.asEntity()
                val branchesEntity = initialBranches.map { it.asEntity() }
                val taxRatesEntity = initialTaxes.map { it.asEntity() }
                val subscriptionEntity = initialSubscription.asEntity()
                businessDao.insertOrReplaceBusiness(businessEntity)
                branchDao.insertOrReplaceBranches(branchesEntity)
                taxRateDao.insertOrReplaceTaxRates(taxRatesEntity)
                subscriptionDao.insertOrReplaceSubscription(subscriptionEntity)

                // 2. Create Outbox command for push sync
                val outboxPayload = json.encodeToString(
                    OutboxPayload.CreateBusinessPayload(
                        business = businessEntity.asNetworkModel(),
                        initialBranches = branchesEntity.map { it.asNetworkModel() },
                        initialTaxes = taxRatesEntity.map { it.asNetworkModel() },
                        initialSubscription = initialSubscription.asNetworkModel(),
                        initialBillingEvent = initialBillingEvent.asNetworkModel(),
                    ),
                )
                outboxCommandDao.insertCommand(
                    OutboxCommandEntity(
                        type = OutboxEventType.Business.CREATED,
                        payload = outboxPayload,
                    ),
                )
            }
            Result.success(business.id)
        } catch (e: Exception) {
            logService.log("CRITICAL FAILURE in createInitialBusiness: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun findBusinessByOwner(ownerUid: String): Result<Business?> {
        return withContext(ioDispatcher) {
            val localBusiness = businessDao.getBusinessByOwner(ownerUid).first()
            println("localBusiness:$localBusiness")
            if (localBusiness != null) {
                Result.success(localBusiness.asExternalModel())
            } else {
                val networkBusiness = network.findBusinessByOwner(ownerUid)
                println("networkBusiness:$network")

                if (networkBusiness != null) {
                    val business = networkBusiness.asExternalModel()
                    businessDao.insertOrReplaceBusiness(business.asEntity())
                    Result.success(business)
                } else {
                    Result.success(null)
                }
            }
        }
    }

    override suspend fun companyCodeExists(companyCode: String): Result<Boolean> =
        withContext(ioDispatcher) {
            runCatching {
                network.companyCodeExists(companyCode)
            }
        }

    override suspend fun syncUp(): Boolean = withContext(ioDispatcher) {
        var success = true
        try {
            val pendingCommands = outboxCommandDao.getPendingCommands().first()
            val businessCommands =
                pendingCommands.filter { it.type == OutboxEventType.Business.CREATED }

            businessCommands.forEach { command ->
                try {
                    outboxCommandDao.updateCommandStatus(
                        command.id,
                        OutboxCommandStatus.PROCESSING,
                        1,
                        Clock.System.now(),
                    )
                    val payload =
                        json.decodeFromString<OutboxPayload.CreateBusinessPayload>(command.payload)
                    val resultBusinessId = network.createInitialBusiness(
                        business = payload.business,
                        initialBranches = payload.initialBranches,
                        initialTaxes = payload.initialTaxes,
                        initialSubscription = payload.initialSubscription,
                        initialBillingEvent = payload.initialBillingEvent,
                    )
                    inboxNetworkDataSource.postSignal(
                        resultBusinessId,
                        NetworkInboxSignal(
                            entityType = SyncableEntityType.BUSINESS.name,
                            entityId = resultBusinessId,
                            lastUpdated = Clock.System.now().toEpochMilliseconds(),
                        ),
                    )
                    outboxCommandDao.updateCommandStatus(
                        command.id,
                        OutboxCommandStatus.COMPLETED,
                        1,
                        Clock.System.now(),
                    )
                } catch (e: Exception) {
                    logService.log("BusinessRepository(syncUp): Failed to process command ${command.id}. Error: ${e.message}")
                    outboxCommandDao.updateCommandStatus(
                        command.id,
                        OutboxCommandStatus.FAILED,
                        1,
                        Clock.System.now(),
                        e.message,
                    )
                    success = false
                }
            }
        } catch (e: Exception) {
            logService.log("BusinessRepository(syncUp): Failed with exception: ${e.message}")
            success = false
        }
        success
    }

    override suspend fun syncDown(): Boolean = withContext(ioDispatcher) {
        var success = true
        try {
            val pendingSignals = localSignalDao.getPendingSignals().first()
            val businessSignals =
                pendingSignals.filter { it.entityType == SyncableEntityType.BUSINESS }

            businessSignals.forEach { signal ->
                try {
                    val ownerUid = signal.entityId
                    val networkBusiness = network.findBusinessByOwner(ownerUid)
                    if (networkBusiness != null) {
                        val businessEntity = networkBusiness.asExternalModel().asEntity()
                        businessDao.insertOrReplaceBusiness(businessEntity)
                        localSignalDao.updateSignalStatus(signal.id, LocalSignalStatus.PROCESSED)
                    }
                } catch (e: Exception) {
                    logService.log("BusinessRepository(syncDown): Failed to process signal ${signal.id}. Error: ${e.message}")
                    success = false
                }
            }
        } catch (e: Exception) {
            logService.log("BusinessRepository(syncDown): Failed with exception: ${e.message}")
            success = false
        }
        success
    }
}
