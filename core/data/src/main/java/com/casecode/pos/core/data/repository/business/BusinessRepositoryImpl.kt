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
import com.casecode.pos.core.domain.repository.business.BusinessRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.firebase.datasource.BusinessNetworkDataSource
import com.casecode.pos.core.model.data.business.BillingEvent
import com.casecode.pos.core.model.data.business.Branch
import com.casecode.pos.core.model.data.business.Business
import com.casecode.pos.core.model.data.business.Subscription
import com.casecode.pos.core.model.data.business.TaxRate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BusinessRepositoryImpl @Inject constructor(
    private val network: BusinessNetworkDataSource,
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
            val result = network.createInitialBusiness(
                business = business.asNetworkModel(),
                initialBranches = initialBranches.map { it.asNetworkModel() },
                initialTaxes = initialTaxes.map { it.asNetworkModel() },
                initialSubscription = initialSubscription.asNetworkModel(),
                initialBillingEvent = initialBillingEvent.asNetworkModel(),
            )

            Result.success(result)
        } catch (e: Exception) {
            logService.log("createInitialBusiness failed:${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun findBusinessByOwner(ownerUid: String): Result<Business?> =
        withContext(ioDispatcher) {
            try {
                val result = network.findBusinessByOwner(ownerUid)

                Result.success(result?.asExternalModel())
            } catch (e: Exception) {
                logService.log("getBusiness failed:${e.message}")

                Result.failure(e)
            }
        }

    override suspend fun companyCodeExists(companyCode: String): Result<Boolean> =
        withContext(ioDispatcher) {
            try {
                val isExists = network.companyCodeExists(companyCode)

                Result.success(isExists)
            } catch (e: Exception) {
                logService.log("companyCodeExists failed:${e.message}")
                Result.failure(e)
            }
        }

}
