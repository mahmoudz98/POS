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
package com.casecode.pos.core.testing.datasource

import com.casecode.pos.core.firebase.datasource.BusinessNetworkDataSource
import com.casecode.pos.core.firebase.model.NetworkBillingEvent
import com.casecode.pos.core.firebase.model.NetworkBranch
import com.casecode.pos.core.firebase.model.NetworkBusiness
import com.casecode.pos.core.firebase.model.NetworkSubscription
import com.casecode.pos.core.firebase.model.NetworkTaxRate
import java.util.UUID

class TestBusinessNetworkDataSource : BusinessNetworkDataSource {

    private val businesses = mutableMapOf<String, NetworkBusiness>()

    override suspend fun createInitialBusiness(
        business: NetworkBusiness,
        initialBranches: List<NetworkBranch>,
        initialTaxes: List<NetworkTaxRate>,
        initialSubscription: NetworkSubscription,
        initialBillingEvent: NetworkBillingEvent,
    ): String {
        val newId = UUID.randomUUID().toString()
        val newBusiness = business.copy(id = newId)
        businesses[newId] = newBusiness
        return newId
    }

    override suspend fun findBusinessByOwner(ownerUid: String): NetworkBusiness? {
        return businesses.values.find { it.ownerUid == ownerUid }
    }

    override suspend fun companyCodeExists(companyCode: String): Boolean {
        return businesses.values.any { it.companyCode == companyCode }
    }

    override suspend fun getBranches(businessId: String): List<NetworkBranch> {
        return emptyList()
    }

    override suspend fun addBranch(businessId: String, branch: NetworkBranch): String {
        return ""
    }
}