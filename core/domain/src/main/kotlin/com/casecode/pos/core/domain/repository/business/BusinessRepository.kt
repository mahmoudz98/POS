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

/**
 * Repository interface for managing business-related data.
 * Provides methods for creating, retrieving, and refreshing business information.
 */

import com.casecode.pos.core.domain.utils.Syncable
import com.casecode.pos.core.model.business.BillingEvent
import com.casecode.pos.core.model.business.Branch
import com.casecode.pos.core.model.business.Business
import com.casecode.pos.core.model.business.Subscription
import com.casecode.pos.core.model.business.TaxRate

interface BusinessRepository : Syncable {
    /**
     * Initiates the creation of a new business, including its initial branches, taxes,
     * subscription, and billing event. This operation is typically performed locally first
     * and then synchronized with the remote data source.
     *
     * @param business The core business data.
     * @param initialBranches The initial branches associated with the business.
     * @param initialTaxes The initial tax rates for the business.
     * @param initialSubscription The initial subscription details.
     * @param initialBillingEvent The initial billing event.
     * @return A [Result] containing the ID of the newly created business if successful,
     *         or a [Throwable] if an error occurred during local persistence or sync request.
     */
    suspend fun createInitialBusiness(
        business: Business,
        initialBranches: List<Branch>,
        initialTaxes: List<TaxRate>,
        initialSubscription: Subscription,
        initialBillingEvent: BillingEvent,
    ): Result<String>

    suspend fun findBusinessByOwner(ownerUid: String): Result<Business?>

    suspend fun getBusinessByCompanyCode(companyCode: String): Result<Business?>
}
