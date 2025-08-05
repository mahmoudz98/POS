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
import com.casecode.pos.core.domain.repository.business.TaxRepository
import com.casecode.pos.core.domain.service.LogService
import com.casecode.pos.core.firebase.datasource.TaxNetworkDataSource
import com.casecode.pos.core.model.business.TaxRate
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "TaxRepoImpl"

class TaxRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val network: TaxNetworkDataSource,
    private val logService: LogService,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : TaxRepository {

    /**
     * Fetches all configured tax rates for a given business.
     */
    override suspend fun getTaxRates(businessId: String): Result<List<TaxRate>> = withContext(ioDispatcher) {
        runCatching {
            network.getTaxRates(businessId).map { it.asExternalModel() }
        }.onSuccess { taxRates ->
            if (taxRates.isEmpty()) {
                logService.log("$TAG: No tax rates found for businessId: $businessId")
            }
        }.onFailure { e ->
            logService.log("$TAG: getTaxRates failed for businessId: $businessId, ${e.message}")
        }
    }

    /**
     * Adds a new tax rate rule to a business's taxRates sub-collection.
     */
    override suspend fun addTaxRate(businessId: String, taxRate: TaxRate): Result<String> = withContext(ioDispatcher) {
        runCatching {
            network.addTaxRate(businessId, taxRate.asNetworkModel())
        }.onSuccess { newTaxRateId ->
            logService.log("$TAG: Successfully added new tax rate with ID $newTaxRateId to business $businessId")
        }.onFailure { e ->
            logService.log("$TAG: addTaxRate failed for businessId: $businessId, ${e.message}")
        }
    }
}
