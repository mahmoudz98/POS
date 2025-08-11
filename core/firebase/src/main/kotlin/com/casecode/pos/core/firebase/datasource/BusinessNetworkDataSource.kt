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
package com.casecode.pos.core.firebase.datasource

import com.casecode.pos.core.firebase.model.NetworkBillingEvent
import com.casecode.pos.core.firebase.model.NetworkBranch
import com.casecode.pos.core.firebase.model.NetworkBusiness
import com.casecode.pos.core.firebase.model.NetworkSubscription
import com.casecode.pos.core.firebase.model.NetworkTaxRate

interface BusinessNetworkDataSource {
    suspend fun createInitialBusiness(
        business: NetworkBusiness,
        initialBranches: List<NetworkBranch>,
        initialTaxes: List<NetworkTaxRate>,
        initialSubscription: NetworkSubscription,
        initialBillingEvent: NetworkBillingEvent,
    ): String
    suspend fun findBusinessByOwner(ownerUid: String): NetworkBusiness?

    suspend fun companyCodeExists(companyCode: String): Boolean

    suspend fun getBranches(businessId: String): List<NetworkBranch>
    suspend fun addBranch(businessId: String, branch: NetworkBranch): String
}